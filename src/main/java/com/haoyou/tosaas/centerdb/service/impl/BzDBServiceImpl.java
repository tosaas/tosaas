package com.haoyou.tosaas.centerdb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.dao.BzDBDAO;
import com.haoyou.tosaas.centerdb.po.BzDB;
import com.haoyou.tosaas.centerdb.po.ServerConfig;
import com.haoyou.tosaas.centerdb.service.BzDBService;
import com.haoyou.tosaas.centerdb.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BzDBServiceImpl class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Service
public class BzDBServiceImpl implements BzDBService {
    @Autowired
    private BzDBDAO bzDBDAO;

    @Autowired
    private ServerConfigService serverConfigService;

    @Override
    public Result add(BzDB db) {

        try {

            //id由mongodb生成
            db.setId(null);
            //分配一个服务器
            ServerConfig serverConfig = serverConfigService.allocFreeServer();
            if (serverConfig == null) {
                return new Result(false, 3, "数据库服务器已满！");
            }
            String server_ip = serverConfig.getServer_ip();
            db.setServer_ip(server_ip);

            BzDB retBzDB = bzDBDAO.save(db);
            JSONObject jsonRet = new JSONObject();
            jsonRet.put("id", retBzDB.getId());

            //使用建库脚本创建数据库


            return new Result(true, 0, "成功", jsonRet);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }


    @Override
    public Result update(BzDB db) {

        try {
            BzDB existObj = bzDBDAO.findById(db.getId());
            if (existObj == null) {
                return new Result(false, 2, "数据库不存在！");
            }

            existObj.setName(db.getName());

            bzDBDAO.save(existObj);

            return new Result(true, 0, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        bzDBDAO.deleteById(id);
    }

    @Override
    public BzDB findById(String id) {
        return bzDBDAO.findById(id);
    }

    @Override
    public List<BzDB> findAll(String company_id) {
        return bzDBDAO.findAll(company_id);
    }
}
