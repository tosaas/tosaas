package com.haoyou.tosaas.centerdb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.common.UserIdHolder;
import com.haoyou.tosaas.centerdb.dao.UserDAO;
import com.haoyou.tosaas.centerdb.po.User;
import com.haoyou.tosaas.redis.RedisUtils;
import com.haoyou.tosaas.centerdb.service.UserService;
import com.haoyou.tosaas.utils.EncryptUtil;
import com.haoyou.tosaas.utils.RSATool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * UserServiceImpl class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    private EncryptUtil encryptUtil = EncryptUtil.getInstance();

    @Resource
    RedisUtils redisUtils;

    @Override
    public Result reg(User user) {

        try {
            if (user.getId() != null && userDAO.findById(user.getId()) != null) {
                return new Result(false, 2, "用户id已经存在！");
            }
            if (userDAO.findByName(user.getName()) != null) {
                return new Result(false, 2, "用户名称已经存在！");
            }
            //密码要加密
            String pwdEnc = encryptUtil.MD5(user.getPwd());
            user.setPwd(pwdEnc);
            //注册时间
            user.setCreated(new Date());

            User retUser = userDAO.save(user);
            JSONObject jsonRet = new JSONObject();
            jsonRet.put("id", retUser.getId());

            return new Result(true, 0, "成功", jsonRet);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public Result login(String name, String pwd) {

        try {
            User user = userDAO.findByName(name);
            if (user == null) {
                return new Result(false, 2, "用户不存在！");
            }
            //密码要加密
            String pwdEnc = encryptUtil.MD5(pwd);

            if (!pwdEnc.equals(user.getPwd())) {
                return new Result(false, 2, "用户名密码错误！");
            }
            //生成token令牌
            String token = UUID.randomUUID().toString().replace("-","");
            //保存token、用户id到redis ,24小时有效
            String tokenKey = redisUtils.makeSessionKey("token", token);
            String userKey = redisUtils.makeSessionKey("User", token);
            redisUtils.set(tokenKey, token, 3600*24);
            redisUtils.set(userKey, user.getId(), 3600*24);
            //保存企业id
            String companyId = user.getCompany_id();
            if(!StringUtils.isEmpty(companyId)){
                String companyKey = redisUtils.makeSessionKey("Company", user.getId());
                redisUtils.set(companyKey, companyId, 3600*24);
            }

            //返回用户信息以及token给客户端
            JSONObject jsonObj = new JSONObject();
            //需要屏蔽掉密码
            user.setPwd(null);
            jsonObj.put("user", user);
            jsonObj.put("token", token);

            return new Result(true, 0, "成功", jsonObj);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public Result modifyPwd(String oldPwd, String newPwd) {
        try {
            String userId = UserIdHolder.get();
            User user = userDAO.findById(userId);
            if (user == null) {
                return new Result(false, 2, "请重新登录！");
            }
            String oldPwdEnc = encryptUtil.MD5(oldPwd);
            if (!oldPwdEnc.equals(user.getPwd())) {
                return new Result(false, 2, "旧密码错误！");
            }
            String newPwdEnc = encryptUtil.MD5(newPwd);
            user.setPwd(newPwdEnc);
            userDAO.save(user);

            return new Result(true, 0, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public Result update(User user) {
        try {
            User existObj = userDAO.findById(user.getId());
            if (existObj == null) {
                return new Result(false, 2, "用户不存在！");
            }

            if(user.getNick()!=null)
                existObj.setNick(user.getNick());
            if(user.getPhone()!=null)
                existObj.setPhone(user.getPhone());
            if(user.getCompany_id()!=null)
                existObj.setCompany_id(user.getCompany_id());

            userDAO.save(existObj);

            return new Result(true, 0, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, 99, e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        userDAO.deleteById(id);
    }

    @Override
    public User findById(String id) {
        return userDAO.findById(id);
    }

    @Override
    public User findByName(String name) {
        return userDAO.findByName(name);
    }

    @Override
    public List<User> findAll(String company_id) {
        return userDAO.findAll(company_id);
    }
}
