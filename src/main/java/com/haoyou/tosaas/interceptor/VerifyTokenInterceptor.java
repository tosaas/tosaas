package com.haoyou.tosaas.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.centerdb.dao.ServerConfigDAO;
import com.haoyou.tosaas.centerdb.dao.UserDAO;
import com.haoyou.tosaas.centerdb.po.ServerConfig;
import com.haoyou.tosaas.centerdb.po.User;
import com.haoyou.tosaas.centerdb.service.ServerConfigService;
import com.haoyou.tosaas.common.DBIDHolder;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.common.UserIdHolder;
import com.haoyou.tosaas.centerdb.dao.BzDBDAO;
import com.haoyou.tosaas.subdb.dynamicds.DynamicDataSourceContextHolder;
import com.haoyou.tosaas.centerdb.po.BzDB;
import com.haoyou.tosaas.redis.RedisUtils;
import com.haoyou.tosaas.utils.RSATool;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * token拦截器
 * @author gxj
 * @date 2019/06/12
 */
@Component
public class VerifyTokenInterceptor  extends HandlerInterceptorAdapter {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(VerifyTokenInterceptor.class);

    @Resource
    RedisUtils redisUtils;

    @Autowired
    BzDBDAO bzDBDAO;

    @Autowired
    ServerConfigService serverConfigService;

    @Autowired
    UserDAO userDAO;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {

        String token = request.getParameter("token");
        String DBID = request.getParameter("DBID");

        if (StringUtils.isEmpty(token)) {
            //判断post参数
            try {
                RequestWrapper requestWrapper = new RequestWrapper(request);
                String body = requestWrapper.getBody();
                //System.out.println(body);
                if (!StringUtils.isEmpty(body)) {
                    JSONObject jsonReq = JSONObject.parseObject(body);
                    token = (String) jsonReq.get("token");
                    DBID = (String) jsonReq.get("DBID");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,XFILENAME,XFILECATEGORY,XFILESIZE");

        //如果没有传递token，则考虑从session里取
        String sessionTokenKey = redisUtils.makeSessionKey("token", request.getSession().getId());
        if (StringUtils.isEmpty(token)) {
            token = (String) redisUtils.get(sessionTokenKey);
        }

        String sessionUserIdKey = redisUtils.makeSessionKey("UserId", request.getSession().getId());

        log.info("拦截成功VerifyTokenInterceptor");
        if (!StringUtils.isEmpty(token)) {

            DBIDHolder.remove();
            UserIdHolder.remove();
            //如果DBID没有设置，分配一台服务器用于建库
            if(StringUtils.isEmpty(DBID)){
                //切换数据源
                //使用mysql服务器ip作为key
                ServerConfig serverConfig = serverConfigService.allocFreeServer();
                if(serverConfig!=null)
                {
                    String key = serverConfig.getServer_ip();
                    if(!DynamicDataSourceContextHolder.containDataSourceKey(key))
                    {
                        List<String> lstKey=new ArrayList<>();
                        lstKey.add(key);
                        DynamicDataSourceContextHolder.addDataSourceKeys(lstKey);
                    }
                    DynamicDataSourceContextHolder.setDataSourceKey(key);
                }
                //
            }
            else {
                DBIDHolder.set(DBID);
                //切换数据源
                //使用mysql服务器ip作为key
                BzDB bzDB = bzDBDAO.findById(DBID);
                if(bzDB!=null)
                {
                    String key = bzDB.getServer_ip();
                    if(!DynamicDataSourceContextHolder.containDataSourceKey(key))
                    {
                        List<String> lstKey=new ArrayList<>();
                        lstKey.add(key);
                        DynamicDataSourceContextHolder.addDataSourceKeys(lstKey);
                    }
                    DynamicDataSourceContextHolder.setDataSourceKey(key);
                }
                //

            }

            //如果redis已经缓存结果，则直接返回
            String tokenKey = redisUtils.makeSessionKey("token", token);
            String userKey = redisUtils.makeSessionKey("User", token);
            if (redisUtils.exists(tokenKey)) {
                String userId = (String) redisUtils.get(userKey);
                if (userId != null) {
                    UserIdHolder.set(userId);

                    String companyKey = redisUtils.makeSessionKey("Company", userId);

                    String companyId = redisUtils.get(companyKey);
                    if(companyId==null){
                        User user=userDAO.findById(userId);
                        if(user!=null) {
                            companyId = user.getCompany_id();
                        }
                    }
                    redisUtils.set(companyKey,companyId,3600*24);

                    //判断用户是否有操作数据库的权限,即是否同属于一个公司
                    if(!StringUtils.isEmpty(DBID)) {
                        if (!hasAuth(companyId, DBID)) {
                            Result result = new Result(false, -1, "没有权限。");
                            String json = JSONObject.toJSONString(result);
                            returnJson(response, json);
                        }
                    }

                    //更新session
                    redisUtils.set(sessionTokenKey, token, 3600);
                    redisUtils.set(sessionUserIdKey, userId, 3600);
                    return true;
                }
                else {
                    Result result = new Result(false, -1, "userId无效，请重新登录。");
                    String json = JSONObject.toJSONString(result);
                    returnJson(response, json);
                    log.info("拦截失败--userId缺失");
                    return false;
                }
            }
            else {
                Result result = new Result(false, -1, "token无效，请重新登录。");
                String json = JSONObject.toJSONString(result);
                returnJson(response, json);
                log.info("拦截失败--token缺失");
                return false;
            }

        } else {
            Result result = new Result(false, -1, "token为空");
            String json = JSONObject.toJSONString(result);
            returnJson(response, json);
            log.info("拦截失败--token为空");
            return false;
        }

    }


    /**
     * 判断用户是否有操作数据库的权限,即是否同属于一个公司
     * @param companyId
     * @param DBID
     * @return
     */
    boolean hasAuth(String companyId,String DBID){
        String dbcompanyKey = redisUtils.makeSessionKey("DbCompany", DBID);

        String dbcompanyId =  redisUtils.get(dbcompanyKey);
        if(dbcompanyId==null){
            BzDB bzDB = bzDBDAO.findById(DBID);
            if(bzDB!=null) {
                dbcompanyId = bzDB.getCompany_id();
                redisUtils.set(dbcompanyKey,dbcompanyId,3600*24);
            }
        }


        if(!dbcompanyId.equalsIgnoreCase(companyId))
            return  false;

        return  true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception e) throws Exception {

    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

}
