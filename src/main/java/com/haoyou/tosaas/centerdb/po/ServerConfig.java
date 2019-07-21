package com.haoyou.tosaas.centerdb.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * ServerConfig class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Document(collection = "ServerConfig")
public class ServerConfig implements Serializable {
    @Id
    private String id;

    @Field("server_ip")
    private String server_ip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal_size() {
        return total_size;
    }

    public void setTotal_size(int total_size) {
        this.total_size = total_size;
    }

    public int getUse_size() {
        return use_size;
    }

    public void setUse_size(int use_size) {
        this.use_size = use_size;
    }

    @Field("user_name")
    private String user_name;

    @Field("pass")
    private String pass;

    @Field("port")
    private int port;

    @Field("status")
    private int status;

    @Field("total_size")
    private int total_size;

    @Field("use_size")
    private int use_size;
}
