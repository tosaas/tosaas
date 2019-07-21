package com.haoyou.tosaas.centerdb.dao;

import com.haoyou.tosaas.centerdb.po.User;

import java.util.List;

/**
 * UserDAO class
 *
 * @author gxj
 * @date 2019/05/15
 */
public interface UserDAO {
    //新增或修改
    User save(User user);

    //删除
    void delete(User user);

    //删除
    void deleteById(String id);

    //查找
    User findByName(String name);

    //查找
    User findById(String id);

    //列表
    List<User> findAll(String company_id);
}
