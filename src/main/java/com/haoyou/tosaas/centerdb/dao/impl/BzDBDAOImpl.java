package com.haoyou.tosaas.centerdb.dao.impl;

import com.haoyou.tosaas.centerdb.dao.BzDBDAO;
import com.haoyou.tosaas.centerdb.po.BzDB;
import com.haoyou.tosaas.centerdb.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BzDBDAOImpl class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Repository
public class BzDBDAOImpl implements BzDBDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BzDB save(BzDB db) {
        return mongoTemplate.save(db);
    }

    @Override
    public void delete(BzDB db) {
        deleteById(db.getId());
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);
    }

    @Override
    public BzDB findById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        BzDB bzDB = mongoTemplate.findOne(query, BzDB.class);
        return bzDB;
    }

    @Override
    public List<BzDB> findAll(String company_id) {
        Query query = new Query(Criteria.where("company_id").is(company_id));
        return mongoTemplate.find(query, BzDB.class);
    }
}
