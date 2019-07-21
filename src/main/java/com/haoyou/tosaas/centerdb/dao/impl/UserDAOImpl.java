package com.haoyou.tosaas.centerdb.dao.impl;

import com.haoyou.tosaas.centerdb.dao.UserDAO;
import com.haoyou.tosaas.centerdb.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserDAOImpl class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Repository
public class UserDAOImpl implements UserDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public User save(User user) {
        return mongoTemplate.save(user);
    }

    @Override
    public void delete(User user) {
        this.deleteById(user.getId());
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);

    }

    @Override
    public User findByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        User user = mongoTemplate.findOne(query, User.class);
        return user;
    }

    @Override
    public User findById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        User user = mongoTemplate.findOne(query, User.class);
        return user;
    }

    @Override
    public List<User> findAll(String company_id) {
        Query query = new Query(Criteria.where("company_id").is(company_id));
        return mongoTemplate.find(query, User.class);
    }
}
