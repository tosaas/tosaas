package com.haoyou.tosaas.common;

/**
 * UserIdHolder class
 * 数据库id保存到 ThreadLocal 变量里共享
 *
 * @author gxj
 * @date 2019/05/16
 */
public class DBIDHolder {

    private static ThreadLocal<String> dbId = new InheritableThreadLocal<String>();

    public static void set(String dbId) {
        DBIDHolder.dbId.set(dbId);
    }

    public static String get() {
        return dbId.get();
    }

    public static void remove() {
        dbId.remove();
    }

}
