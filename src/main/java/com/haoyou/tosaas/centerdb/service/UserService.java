package com.haoyou.tosaas.centerdb.service;

import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.po.User;

import java.util.List;

/**
 * UserService class
 *
 * @author gxj
 * @date 2019/05/15
 */
public interface UserService {


    /**
     * 注册
     *
     * @param user 用户信息
     * @return
     */
    Result reg(User user);

    /**
     * 登录
     *
     * @param name 用户名
     * @param pwd  密码
     * @return
     */
    Result login(String name, String pwd);


    /**
     * 修改密码
     *
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return
     */
    Result modifyPwd(String oldPwd, String newPwd);


    /**
     * 修改
     *
     * @param user 用户信息
     * @return
     */
    Result update(User user);

    /**
     * 删除
     *
     * @param id 用户id
     * @return
     */
    void deleteById(String id);


    /**
     * 查找
     *
     * @param id 用户id
     * @return
     */
    User findById(String id);


    /**
     * 查找
     *
     * @param name 用户名
     * @return
     */
    User findByName(String name);


    /**
     * 查询单位所有用户列表
     *
     * @param company_id 单位id
     * @return
     */
    List<User> findAll(String company_id);
}
