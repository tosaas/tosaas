package com.haoyou.tosaas.centerdb.common;

/**
 * UserIdHolder class
 * 用户id保存到 ThreadLocal 变量里共享
 *
 * @author gxj
 * @date 2019/05/16
 */
public class UserIdHolder {

    private static ThreadLocal<String> userId = new InheritableThreadLocal<String>();

    public static void set(String userId) {
        UserIdHolder.userId.set(userId);
    }

    public static String get() {
        return userId.get();
    }

    public static void remove() {
        userId.remove();
    }

}
