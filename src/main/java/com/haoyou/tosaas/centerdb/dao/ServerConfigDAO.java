package com.haoyou.tosaas.centerdb.dao;

import com.haoyou.tosaas.centerdb.po.ServerConfig;

import java.util.List;

/**
 * ServerConfigDAO class
 *
 * @author gxj
 * @date 2019/05/15
 */
public interface ServerConfigDAO {
    //新增或修改
    ServerConfig save(ServerConfig serverConfig);

    //删除
    void delete(ServerConfig serverConfig);

    //删除
    void deleteById(String id);

    //查找
    ServerConfig findById(String id);

    //查找
    ServerConfig findByServer_ip(String server_ip);

    //列表
    List<ServerConfig> findAll();
}
