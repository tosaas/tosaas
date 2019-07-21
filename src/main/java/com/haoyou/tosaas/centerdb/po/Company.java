package com.haoyou.tosaas.centerdb.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * Company class
 *
 * @author gxj
 * @date 2019/05/15
 */
@Document(collection = "Company")
public class Company implements Serializable {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("created")
    private Date created;

    @Field("creator")
    private String creator;

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
