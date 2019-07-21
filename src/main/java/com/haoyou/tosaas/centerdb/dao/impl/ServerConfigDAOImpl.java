package com.haoyou.tosaas.centerdb.dao.impl;

import com.haoyou.tosaas.centerdb.dao.ServerConfigDAO;
import com.haoyou.tosaas.centerdb.po.ServerConfig;
import com.haoyou.tosaas.centerdb.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ServerConfigDAOImpl class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Repository
public class ServerConfigDAOImpl implements ServerConfigDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ServerConfig save(ServerConfig serverConfig) {
        return mongoTemplate.save(serverConfig);
    }

    @Override
    public void delete(ServerConfig serverConfig) {
        this.deleteById(serverConfig.getId());
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);
    }

    @Override
    public ServerConfig findById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        ServerConfig serverConfig = mongoTemplate.findOne(query, ServerConfig.class);
        return serverConfig;
    }

    @Override
    public ServerConfig findByServer_ip(String server_ip) {
        Query query = new Query(Criteria.where("server_ip").is(server_ip));
        ServerConfig serverConfig = mongoTemplate.findOne(query, ServerConfig.class);
        return serverConfig;
    }

    @Override
    public List<ServerConfig> findAll() {
        return mongoTemplate.findAll(ServerConfig.class);
    }
}
