package com.haoyou.tosaas.centerdb.service;

import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.po.BzDB;

import java.util.List;

/**
 * BzDBService class
 *
 * @author gxj
 * @date 2019/05/15
 */
public interface BzDBService {

    /**
     * 新增
     *
     * @param db 数据库信息
     * @return
     */
    Result add(BzDB db);

    /**
     * 修改
     *
     * @param db 数据库信息
     * @return
     */
    Result update(BzDB db);


    /**
     * 删除
     *
     * @param id 数据库id
     * @return
     */
    void deleteById(String id);

    /**
     * 查找
     *
     * @param id 数据库id
     * @return
     */
    BzDB findById(String id);

    /**
     * 查询一个单位的所有数据库列表
     *
     * @param company_id 单位id
     * @return
     */
    List<BzDB> findAll(String company_id);
}
