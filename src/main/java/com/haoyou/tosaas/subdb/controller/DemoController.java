package com.haoyou.tosaas.subdb.controller;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.centerdb.po.Company;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.subdb.mapper.IDemoMapper;
import com.haoyou.tosaas.subdb.model.Demo;
import com.haoyou.tosaas.centerdb.po.BzDB;
import com.haoyou.tosaas.centerdb.po.ServerConfig;
import com.haoyou.tosaas.redis.RedisUtils;
import com.haoyou.tosaas.centerdb.service.BzDBService;
import com.haoyou.tosaas.centerdb.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserController class
 *
 * @author gxj
 * @Description mysql 测试
 * @date 2019/05/20
 */
@RestController
public class DemoController {
    @Autowired
    private IDemoMapper demoMapper;

    @RequestMapping(value = "/demo/add.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result add(@RequestBody JSONObject jsonReq) {

        Demo demo = JSONObject.toJavaObject(jsonReq.getJSONObject("content"), Demo.class);

        demoMapper.add(demo);
        return new Result(true,0,"成功");
    }


    @RequestMapping(value = "/demo/update.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result update(@RequestBody JSONObject jsonReq) {

        Demo demo = JSONObject.toJavaObject(jsonReq.getJSONObject("content"), Demo.class);

        demoMapper.update(demo);
        return new Result(true,0,"成功");
    }


    @RequestMapping(value = "/demo/deleteById.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result deleteById(@RequestBody JSONObject jsonReq) {

        Demo demo = JSONObject.toJavaObject(jsonReq.getJSONObject("content"), Demo.class);

        demoMapper.deleteById(demo.getId());
        return new Result(true,0,"成功");
    }


    @RequestMapping(value = "/demo/findAll.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result findAll(@RequestBody JSONObject jsonReq) {

        List<Demo> lstObj= demoMapper.findAll();

        return  new Result(true,0,"成功",lstObj);
    }


    @RequestMapping(value = "/demo/findById.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Demo findById(@RequestBody JSONObject jsonReq) {

        Demo demo = JSONObject.toJavaObject(jsonReq.getJSONObject("content"), Demo.class);

        return demoMapper.findById(demo.getId());
    }

    @RequestMapping(value = "/demo/findByName.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Demo findByName(@RequestBody JSONObject jsonReq) {

        Demo demo = JSONObject.toJavaObject(jsonReq.getJSONObject("content"), Demo.class);

        if (!StringUtils.isEmpty(demo.getName())) {
            demo = demoMapper.findByName(demo.getName());
        }
        return demo;
    }



}
