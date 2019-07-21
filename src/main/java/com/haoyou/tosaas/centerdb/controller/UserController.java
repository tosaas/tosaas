package com.haoyou.tosaas.centerdb.controller;

import com.alibaba.fastjson.JSONObject;
import com.haoyou.tosaas.centerdb.po.Company;
import com.haoyou.tosaas.common.Result;
import com.haoyou.tosaas.centerdb.po.User;
import com.haoyou.tosaas.centerdb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController class
 *
 * @author gxj
 * @Description 用户控制器
 * @date 2019/05/18
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return 返回新用户id
     * @author gxj
     * @date 2019/05/16
     * @Description: 注册
     */
    @RequestMapping(value = "/user/reg.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result reg(@RequestBody JSONObject jsonReq) {

        User user=JSONObject.toJavaObject(jsonReq.getJSONObject("content"), User.class);

        if (StringUtils.isEmpty(user.getName())) {
            return new Result(false, 1, "用户名name不能为空！");
        }
        if (StringUtils.isEmpty(user.getPwd())) {
            return new Result(false, 1, "密码pwd不能为空！");
        }
        if (StringUtils.isEmpty(user.getNick())) {
            return new Result(false, 1, "昵称nick不能为空！");
        }
        if (StringUtils.isEmpty(user.getPhone())) {
            return new Result(false, 1, "电话phone不能为空！");
        }

        return userService.reg(user);
    }

    /**
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return 返回登录令牌、用户信息
     * @author gxj
     * @date 2019/05/16
     * @Description: 登录
     */
    @RequestMapping(value = "/user/login.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result login(@RequestBody JSONObject jsonReq) {

        User user=JSONObject.toJavaObject(jsonReq.getJSONObject("content"), User.class);

        if (StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getPwd())) {
            return new Result(false, 1, "用户名密码不能为空！");
        }

        return userService.login(user.getName(), user.getPwd());
    }


    /**
     * @param jsonReq 这个是阿里的 fastjson对象
     * @return
     * @author gxj
     * @date 2019/05/18
     * @Description: 修改密码
     */
    @RequestMapping(value = "/user/modifyPwd.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result modifyPwd(@RequestBody JSONObject jsonReq) {

        JSONObject content = jsonReq.getJSONObject("content");
        String oldPwd = content.getString("oldPwd");
        String newPwd = content.getString("newPwd");

        if (StringUtils.isEmpty(oldPwd)) {
            return new Result(false, 1, "旧密码不能为空！");
        }

        if (StringUtils.isEmpty(newPwd)) {
            return new Result(false, 1, "新密码不能为空！");
        }

        return userService.modifyPwd(oldPwd, newPwd);
    }
}
