package com.haoyou.tosaas.centerdb.controller;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.centerdb.common.UserIdHolder;
import com.haoyou.tosaas.centerdb.mapper.IBzDBMapper;
import com.haoyou.tosaas.centerdb.po.BzDB;
import com.haoyou.tosaas.centerdb.po.ServerConfig;
import com.haoyou.tosaas.centerdb.service.BzDBService;
import com.haoyou.tosaas.centerdb.service.ServerConfigService;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * BzDBController class
 *
 * @author gxj
 * @Description 业务数据库控制器
 * @date 2019/06/10
 */
@RestController
public class BzDBController {

    @Autowired
    BzDBService bzDBService;

    @Autowired
    ServerConfigService serverConfigService;

    @Autowired
    IBzDBMapper bzDBMapper;

    @Resource
    RedisUtils redisUtils;


    /**
     * 查询业务数据库列表
     * @param jsonReq
     * @return
     */
    @RequestMapping(value = "/bzdb/findAll.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result findAll(@RequestBody JSONObject jsonReq) {

        String userId =  UserIdHolder.get();
        System.out.println("userId="+userId);
        String companyKey = redisUtils.makeSessionKey("Company", userId);
        String companyId = redisUtils.get(companyKey);
        if(StringUtils.isEmpty(companyId)){
            return  new Result(false,-1,"请重新登录");
        }
        List<BzDB> lstBzDB = bzDBService.findAll(companyId);

        return  new Result(true,0,"成功",lstBzDB);
    }

        /**
         * 添加一个业务数据库
         * @param jsonReq
         * @return
         */
    @RequestMapping(value = "/bzdb/add.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result add(@RequestBody JSONObject jsonReq) {

        ServerConfig serverConfig = serverConfigService.allocFreeServer();
        if(serverConfig==null)
        {
            return new Result(false, 1, "已经没有服务器分配空间！");
        }

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


        BzDB bzDB=JSONObject.toJavaObject(jsonReq.getJSONObject("content"),BzDB.class);

        //登记中心库
        bzDB.setServer_ip(serverConfig.getServer_ip());
        bzDBService.add(bzDB);

        //建库
        String dbName="db_"+bzDB.getId();
        String sqldb = "create database if not exists "+dbName+" ;";
        bzDBMapper.exec(sqldb);

        //建表
        String useSql="use "+dbName+";\r\n";
        bzDBMapper.exec(useSql+sql);

        return  new Result(true, 0,"成功", bzDB);
    }


}
