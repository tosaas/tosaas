package com.haoyou.tosaas.centerdb.common;


import com.haoyou.tosaas.centerdb.common.MongoConfig;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(value= MongoConfig.class)
public class MongodbClient {

    @Autowired
    private MongoConfig mongoConfig;

    /**

     *  创建Mongo 连接驱动

     * @return

     */

    @Bean
    public MongoClient newClient() {

        //MongoClient mongo = new MongoClient(mongoConfig.getHost(),mongoConfig.getPort());
        if(mongoConfig.getUsername()==null || mongoConfig.getUsername().length()==0)
        {
            //String uri = String.format("mongodb://%s/admin", mongoConfig.getHostPort());
            String uri = String.format("mongodb://localhost:27017/admin");
            MongoClientURI mongoClientURI = new MongoClientURI(uri);
            MongoClient mongo = new MongoClient(mongoClientURI);
            return  mongo;
        }
        else {

            //考虑集群
            List<ServerAddress> addresses = new ArrayList<ServerAddress>();
            String[] arr=mongoConfig.getHostPort().split(",");

            for(String addr:arr)
            {
                if(addr.length()==0)
                    continue;
                String[] arr2=addr.split(":");
                if(arr2.length!=2)
                    continue;
                ServerAddress address1 = new ServerAddress(arr2[0],Integer.parseInt(arr2[1]));
                addresses.add(address1);
            }

            List<MongoCredential> mongoCredentialList = new ArrayList<MongoCredential>();
            //用户验证格式是：username，dbname，password
            mongoCredentialList.add(MongoCredential.createScramSha1Credential(mongoConfig.getUsername(), "admin",mongoConfig.getPassword().toCharArray()));
            MongoClient mongo = new MongoClient(addresses,mongoCredentialList);

            return mongo;
        }

    }

    @Bean
    public MongoTemplate newTemplate() {

        MongoTemplate template = new MongoTemplate(newClient(), mongoConfig.getDatabase());
        //MongoTemplate template = new MongoTemplate(newClient(), "tosaas");

        return template;

    }

}
