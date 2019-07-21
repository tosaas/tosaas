package com.haoyou.tosaas.centerdb.dao.impl;

import com.haoyou.tosaas.centerdb.dao.CompanyDAO;
import com.haoyou.tosaas.centerdb.po.Company;
import com.haoyou.tosaas.centerdb.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CompanyDAOImpl class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Repository
public class CompanyDAOImpl implements CompanyDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Company save(Company company) {
        return mongoTemplate.save(company);
    }

    @Override
    public void delete(Company company) {
        deleteById(company.getId());
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);
    }

    @Override
    public Company findById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        Company company = mongoTemplate.findOne(query, Company.class);
        return company;
    }

    @Override
    public Company findByCreator(String creator) {
        Query query = new Query(Criteria.where("creator").is(creator));
        Company company = mongoTemplate.findOne(query, Company.class);
        return company;
    }

    @Override
    public List<Company> findAll() {
        return mongoTemplate.findAll(Company.class);
    }
}
