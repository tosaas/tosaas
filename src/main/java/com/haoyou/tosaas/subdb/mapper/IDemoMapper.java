package com.haoyou.tosaas.subdb.mapper;

import com.haoyou.tosaas.subdb.model.Demo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mysql demo mapper
 *
 * @author gxj
 * @date 2019/05/20
 */
@Mapper
public interface IDemoMapper {

    List<Demo> findAll();

    Demo findById(@Param("id")Integer id);

    void  add(Demo obj);

    void  update(Demo obj);

    void  deleteById(@Param("id")Integer id);

    void deleteAll();
    /**
     * 查找demo
     *
     * @param name 名称
     * @return demo
     */
    Demo findByName(@Param("name") String name);
}
