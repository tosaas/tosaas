package com.haoyou.tosaas.centerdb.dao;

import com.haoyou.tosaas.centerdb.po.BzDB;

import java.util.List;

/**
 * BzDBDAO class
 *
 * @author gxj
 * @date 2019/05/15
 */
public interface BzDBDAO {
    //新增或修改
    BzDB save(BzDB db);

    //删除
    void delete(BzDB db);

    //删除
    void deleteById(String id);

    //查找
    BzDB findById(String id);

    //列表
    List<BzDB> findAll(String company_id);
}
