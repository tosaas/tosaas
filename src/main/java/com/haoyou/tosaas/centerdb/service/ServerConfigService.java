package com.haoyou.tosaas.centerdb.service;

import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.po.ServerConfig;

import java.util.List;

/**
 * ServerConfigService class
 *
 * @author gxj
 * @date 2019/05/15
 */
public interface ServerConfigService {

    /**
     * 新增
     *
     * @param serverConfig 配置信息
     * @return
     */
    Result add(ServerConfig serverConfig);

    /**
     * 修改
     *
     * @param serverConfig 配置信息
     * @return
     */
    Result update(ServerConfig serverConfig);

    /**
     * 删除
     *
     * @param id 配置id
     * @return
     */
    void deleteById(String id);

    /**
     * 查找
     *
     * @param id 配置id
     * @return
     */
    ServerConfig findById(String id);

    /**
     * 根据ip查找
     *
     * @param ip
     * @return
     */
    ServerConfig findByServer_ip(String ip);

    /**
     * 列表
     *
     * @param
     * @return
     */
    List<ServerConfig> findAll();


    /**
     * 随机分配一个服务器
     *
     * @param
     * @return
     */
    ServerConfig allocFreeServer();
}
