package com.haoyou.tosaas.centerdb.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.centerdb.common.UserIdHolder;
import com.haoyou.tosaas.centerdb.mapper.IBzDBMapper;
import com.haoyou.tosaas.centerdb.po.BzDB;
import com.haoyou.tosaas.centerdb.po.Company;
import com.haoyou.tosaas.centerdb.po.ServerConfig;
import com.haoyou.tosaas.centerdb.po.User;
import com.haoyou.tosaas.centerdb.service.BzDBService;
import com.haoyou.tosaas.centerdb.service.CompanyService;
import com.haoyou.tosaas.centerdb.service.ServerConfigService;
import com.haoyou.tosaas.centerdb.service.UserService;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.redis.RedisUtils;
import com.haoyou.tosaas.subdb.dynamicds.DynamicDataSourceContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AdminUserController class
 *
 * @author gxj
 * @Description 后台管理员控制器
 * @date 2019/06/22
 */
@RestController
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    ServerConfigService serverConfigService;

    @Autowired
    BzDBService bzDBService;

    @Autowired
    IBzDBMapper bzDBMapper;

    @Resource
    RedisUtils redisUtils;


    /**
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return 返回登录令牌、用户信息
     * @author gxj
     * @date 2019/06/22
     * @Description: 后台管理员登录
     */
    @RequestMapping(value = "/adminuser/login.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result login(@RequestBody JSONObject jsonReq) {

        User user=JSONObject.toJavaObject(jsonReq.getJSONObject("content"), User.class);

        if (StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getPwd())) {
            return new Result(false, 1, "用户名密码不能为空！");
        }

        if (!user.getName().equalsIgnoreCase("admin")) {
            return new Result(false, 1, "非管理员不能登录！");
        }

        return userService.login(user.getName(), user.getPwd());
    }


    /**
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return 返回true / false
     * @author gxj
     * @date 2019/06/22
     * @Description: 判断系统是否初始化
     */
    @RequestMapping(value = "/adminuser/isInit.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result isInit() {

        //如果已经有一个企业存在，则已经初始化，否则还没有初始化
        List<Company> lstCompany = companyService.findAll();
        if(lstCompany==null || lstCompany.size()==0){
            Boolean ret = false;
            return new Result(true, 0, "没有初始化！",ret);
        }
        else{
            Boolean ret = true;
            return new Result(true, 0, "已经初始化！",ret);
        }

    }

    /**
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return 返回初始化结果
     * @author gxj
     * @date 2019/06/22
     * @Description: 初始化系统
     */
    @RequestMapping(value = "/adminuser/init.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result init(@RequestBody JSONObject jsonReq) {

        List<Company> lstCompany = companyService.findAll();
        if(lstCompany!=null && lstCompany.size()>0){
            return new Result(false, 1, "系统已经初始化！");
        }

        JSONObject jsonObject = jsonReq.getJSONObject("content");
        String company_name=jsonObject.getString("company_name");
        //String user_name=jsonObject.getString("user_name");
        String user_pwd=jsonObject.getString("user_pwd");
        String user_nick=jsonObject.getString("user_nick");
        String user_phone=jsonObject.getString("user_phone");
        String mysql_serverip=jsonObject.getString("mysql_serverip");
        String mysql_port=jsonObject.getString("mysql_port");
        String mysql_username=jsonObject.getString("mysql_username");
        String mysql_pwd=jsonObject.getString("mysql_pwd");
        String bzdb_name=jsonObject.getString("bzdb_name");


        //创建一个管理员用户
        User user=new User();
        user.setName("admin");
        user.setPwd(user_pwd);
        user.setNick(user_nick);
        user.setPhone(user_phone);
        Result result=userService.reg(user);
        if(!result.getSuccess()){
            return  result;
        }
        //设置当前操作用户
        JSONObject jsonRet= (JSONObject) result.getResult();
        String id = jsonRet.getString("id");
        UserIdHolder.set(id);
        //生成token令牌
        String token = UUID.randomUUID().toString().replace("-","");
        //保存token、用户id到redis ,24小时有效
        String tokenKey = redisUtils.makeSessionKey("token", token);
        String userKey = redisUtils.makeSessionKey("User", token);
        redisUtils.set(tokenKey, token, 3600*24);
        redisUtils.set(userKey, user.getId(), 3600*24);


        //首先创建一个企业
        Company company =new Company();
        company.setName(company_name);
        company.setCreator(user.getId());
        Result result1 = companyService.add(company);

        //用户与企业关联
        JSONObject jsonObject1 = (JSONObject) result1.getResult();
        String companyId = jsonObject1.getString("id");
        user.setCompany_id(companyId);
        userService.update(user);

        //添加一台mysql服务器
        ServerConfig serverConfig=new ServerConfig();
        serverConfig.setServer_ip(mysql_serverip);
        serverConfig.setPort(Integer.parseInt(mysql_port));
        serverConfig.setUser_name(mysql_username);
        serverConfig.setPass(mysql_pwd);
        serverConfig.setTotal_size(10000);
        serverConfig.setStatus(1);
        serverConfigService.add(serverConfig);

        //添加一个业务数据库
        //创建mysql数据库
        String path = "classpath:templates/bzdb.sql";
        File dbfile =null;
        String sql="";
        try {
            dbfile = ResourceUtils.getFile(path);
            FileInputStream in =new FileInputStream(dbfile);
            byte b[]=new byte[(int)dbfile.length()];     //创建合适文件大小的数组
            in.read(b);    //读取文件中的内容到b[]数组
            in.close();
            sql=new String(b);
            System.out.println(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new Result(false, 2, "建库脚本缺失！");
        }


        BzDB bzDB=new BzDB();
        bzDB.setName(bzdb_name);
        bzDB.setCompany_id(company.getId());

        //登记中心库
        bzDB.setServer_ip(serverConfig.getServer_ip());
        bzDBService.add(bzDB);


        //切换数据源
        String key = serverConfig.getServer_ip();
        if(!DynamicDataSourceContextHolder.containDataSourceKey(key))
        {
            List<String> lstKey=new ArrayList<>();
            lstKey.add(key);
            DynamicDataSourceContextHolder.addDataSourceKeys(lstKey);
        }
        DynamicDataSourceContextHolder.setDataSourceKey(key);

        //建库
        String dbName="db_"+bzDB.getId();
        String sqldb = "create database if not exists "+dbName+" ;";
        bzDBMapper.exec(sqldb);

        //建表
        String useSql="use "+dbName+";\r\n";
        bzDBMapper.exec(useSql+sql);

        return  new Result(true, 0,"成功", bzDB);


    }



    /**
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return 返回true / false
     * @author gxj
     * @date 2019/06/23
     * @Description: 获取初始化信息
     */
    @RequestMapping(value = "/adminuser/getInitContent.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result getInitContent(@RequestBody JSONObject jsonReq) {

        //如果已经有一个企业存在，则已经初始化，否则还没有初始化
        List<Company> lstCompany = companyService.findAll();
        if(lstCompany==null || lstCompany.size()==0){
            Boolean ret = false;
            return new Result(false, 1, "没有初始化！",ret);
        }
        Company company=lstCompany.get(0);
        List<ServerConfig> lstServerConfig = serverConfigService.findAll();
        JSONArray jsonServerConfig=new JSONArray();
        for(ServerConfig serverConfig:lstServerConfig){
            jsonServerConfig.add(JSON.toJSON(serverConfig));
        }

        List<BzDB> lstBzDB = bzDBService.findAll(company.getId());
        JSONArray jsonBzDB = new JSONArray();
        for(BzDB bzDB:lstBzDB){
            jsonBzDB.add(JSON.toJSON(bzDB));
        }
        String userId = UserIdHolder.get();
        User user = userService.findById(userId);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("company",JSON.toJSON(company));
        jsonObject.put("ServerConfig",jsonServerConfig);
        jsonObject.put("BzDB",jsonBzDB);
        jsonObject.put("User",JSON.toJSON(user));


        return new Result(true, 0, "成功！",jsonObject);
    }

}
