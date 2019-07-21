package com.haoyou.tosaas.subdb.dynamicds;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:mysql.properties")
public class MysqlConfig {

    @Value("${mysql.driver-class-name}")
    private String driverClassName;

    @Value("${mysql.jdbcUrl}")
    private String jdbcUrl;


    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

}
