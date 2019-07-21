package com.haoyou.tosaas.centerdb.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * BzDB class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Document(collection = "BzDB")
public class BzDB implements Serializable {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("server_ip")
    private String server_ip;

    @Field("company_id")
    private String company_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }
}
