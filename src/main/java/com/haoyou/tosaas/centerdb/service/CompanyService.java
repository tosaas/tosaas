package com.haoyou.tosaas.centerdb.service;

import com.haoyou.tosaas.centerdb.po.ServerConfig;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.po.Company;

import java.util.List;

/**
 * CompanyService class
 *
 * @author gxj
 * @date 2019/05/15
 */
public interface CompanyService {

    /**
     * 新增
     *
     * @param company 单位信息
     * @return
     */
    Result add(Company company);

    /**
     * 修改
     *
     * @param company 单位信息
     * @return
     */
    Result update(Company company);

    /**
     * 删除
     *
     * @param id 单位id
     * @return
     */
    void deleteById(String id);


    /**
     * 查找
     *
     * @param id 单位id
     * @return
     */
    Company findById(String id);


    /**
     * 列表
     *
     * @param
     * @return
     */
    List<Company> findAll();


    /**
     * 查找我的企业
     *
     * @return
     */
    Result findMyCompany();
}
