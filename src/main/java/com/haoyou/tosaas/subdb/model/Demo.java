package com.haoyou.tosaas.subdb.model;


import java.io.Serializable;

/**
 * mysql 测试类
 */
public class Demo implements Serializable {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
