package com.haoyou.tosaas.centerdb.controller;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.centerdb.common.UserIdHolder;
import com.haoyou.tosaas.centerdb.po.BzDB;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.po.Company;
import com.haoyou.tosaas.centerdb.service.CompanyService;
import com.haoyou.tosaas.redis.RedisUtils;
import com.haoyou.tosaas.subdb.model.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * CompanyController class
 *
 * @author gxj
 * @Description 单位控制器
 * @date 2019/05/18
 */
@RestController
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Resource
    RedisUtils redisUtils;


    /**
     * @Description: 查找我的单位
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return 返回单位
     * @author gxj
     * @date 2019/06/12
     */
    @RequestMapping(value = "/company/findMyCompany.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result findMyCompany(@RequestBody JSONObject jsonReq) {

        return companyService.findMyCompany();
    }

    /**
     * @Description: 创建单位
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return 返回新单位id
     * @author gxj
     * @date 2019/05/16
     */
    @RequestMapping(value = "/company/add.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result add(@RequestBody JSONObject jsonReq) {

        Company company = JSONObject.toJavaObject(jsonReq.getJSONObject("content"), Company.class);

        if (StringUtils.isEmpty(company.getName())) {
            return new Result(false, 1, "名称name不能为空！");
        }

        return companyService.add(company);
    }


    /**
     * @Description: 修改单位
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return
     * @author gxj
     * @date 2019/05/16
     */
    @RequestMapping(value = "/company/update.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result update(@RequestBody JSONObject jsonReq) {

        Company company = JSONObject.toJavaObject(jsonReq.getJSONObject("content"), Company.class);

        String userId =  UserIdHolder.get();
        String companyKey = redisUtils.makeSessionKey("Company", userId);
        String companyId = redisUtils.get(companyKey);

        if(!company.getId().equalsIgnoreCase(companyId))
        {
            return new Result(false, 1, "没有权限修改！");
        }

        if (StringUtils.isEmpty(company.getName())) {
            return new Result(false, 1, "名称name不能为空！");
        }

        return companyService.update(company);
    }
}
