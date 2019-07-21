package com.haoyou.tosaas.centerdb.dao;

import com.haoyou.tosaas.centerdb.po.Company;

import java.util.List;

/**
 * CompanyDAO class
 *
 * @author gxj
 * @date 2019/05/15
 */
public interface CompanyDAO {
    //新增或修改
    Company save(Company company);

    //删除
    void delete(Company company);

    //删除
    void deleteById(String id);

    //查找
    Company findById(String id);

    //通过创建人查找
    Company findByCreator(String creator);

    //列表
    List<Company> findAll();
}
