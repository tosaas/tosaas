package com.haoyou.tosaas.centerdb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.common.UserIdHolder;
import com.haoyou.tosaas.centerdb.dao.CompanyDAO;
import com.haoyou.tosaas.centerdb.po.Company;
import com.haoyou.tosaas.centerdb.service.CompanyService;
import com.haoyou.tosaas.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * CompanyServiceImpl class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyDAO companyDAO;

    @Resource
    RedisUtils redisUtils;

    @Override
    public Result add(Company company) {

        try {
            String userId = UserIdHolder.get();
            //一个用户只允许有一个单位
            if (companyDAO.findByCreator(userId) != null) {
                return new Result(false, 2, "只能创建一个单位！");
            }

            //id由mongodb生成
            company.setId(null);
            company.setCreated(new Date());
            //单位创建人即为单位管理员
            company.setCreator(UserIdHolder.get());

            Company retCompany = companyDAO.save(company);
            JSONObject jsonRet = new JSONObject();
            jsonRet.put("id", retCompany.getId());

            return new Result(true, 0, "成功", jsonRet);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public Result update(Company company) {

        try {
            Company existObj = companyDAO.findById(company.getId());
            if (existObj == null) {
                return new Result(false, 2, "单位不存在！");
            }

            if(company.getName()!=null)
                existObj.setName(company.getName());

            companyDAO.save(existObj);

            return new Result(true, 0, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        companyDAO.deleteById(id);
    }

    @Override
    public Company findById(String id) {
        return companyDAO.findById(id);
    }

    @Override
    public List<Company> findAll() {
        return companyDAO.findAll();
    }

    @Override
    public Result findMyCompany() {
        String userId =  UserIdHolder.get();
        String companyKey = redisUtils.makeSessionKey("Company", userId);
        String companyId = redisUtils.get(companyKey);

        Company company = this.findById(companyId);
        return new Result(true, 0, "成功", company);
    }

}
