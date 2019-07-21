package com.haoyou.tosaas.centerdb.common;

import org.springframework.boot.context.properties.ConfigurationProperties;



@ConfigurationProperties(prefix="spring.data.mongodb")
public class MongoConfig {

    private String hostPort;
    private String database;
    private String username;
    private String password;

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

