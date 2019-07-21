package com.haoyou.tosaas.centerdb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.dao.ServerConfigDAO;
import com.haoyou.tosaas.centerdb.po.ServerConfig;
import com.haoyou.tosaas.centerdb.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ServerConfigServiceImpl class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Service
public class ServerConfigServiceImpl implements ServerConfigService {
    @Autowired
    private ServerConfigDAO serverConfigDAO;


    @Override
    public Result add(ServerConfig serverConfig) {

        try {

            //一个ip只允许有一条配置
            if (serverConfigDAO.findByServer_ip(serverConfig.getServer_ip()) != null) {
                return new Result(false, 2, "配置已经存在！");
            }

            //id由mongodb生成
            serverConfig.setId(null);

            ServerConfig retServerConfig = serverConfigDAO.save(serverConfig);
            JSONObject jsonRet = new JSONObject();
            jsonRet.put("id", retServerConfig.getId());

            return new Result(true, 0, "成功", jsonRet);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public Result update(ServerConfig serverConfig) {
        try {
            ServerConfig existObj = serverConfigDAO.findById(serverConfig.getId());
            if (existObj == null) {
                return new Result(false, 2, "服务器不存在！");
            }


            serverConfigDAO.save(serverConfig);

            return new Result(true, 0, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        serverConfigDAO.deleteById(id);
    }

    @Override
    public ServerConfig findById(String id) {
        return serverConfigDAO.findById(id);
    }

    @Override
    public ServerConfig findByServer_ip(String ip) {
        return serverConfigDAO.findByServer_ip(ip);
    }

    @Override
    public List<ServerConfig> findAll() {
        return serverConfigDAO.findAll();
    }

    @Override
    public ServerConfig allocFreeServer() {
        List<ServerConfig> allObj = serverConfigDAO.findAll();
        List<ServerConfig> lstObj = new ArrayList<ServerConfig>();
        for (ServerConfig obj : allObj) {
            if (obj.getStatus() == 1) {
                lstObj.add(obj);
            }
        }
        if (lstObj.size() == 0) {
            return null;
        }

        Random rand = new Random(lstObj.size());
        int idx = rand.nextInt(lstObj.size());

        return lstObj.get(idx);
    }
}
