package com.haoyou.tosaas.centerdb.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * mysql bzdb mapper
 * 执行创建数据库、表等sql语句，或者代码动态拼接的sql语句
 * @author gxj
 * @date 2019/06/10
 */
@Mapper
public interface IBzDBMapper {

    /**
     * 执行sql语句
     *
     * @param sql
     * @return
     */
    void  exec(@Param("sql")String sql);
}
