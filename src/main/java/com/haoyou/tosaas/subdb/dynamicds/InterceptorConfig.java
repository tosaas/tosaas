package com.haoyou.tosaas.subdb.dynamicds;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis 拦截器配置
 *
 * 在mybatis-config.xml里配置没起作用，改用这种方式配置
 * @author gxj
 * @date 2019/06/12
 */
@Configuration
public class InterceptorConfig {
    @Bean
    public String myInterceptor(SqlSessionFactory sqlSessionFactory) {

        sqlSessionFactory.getConfiguration().addInterceptor(new TableNameReplacer());
        return "interceptor";
    }
}
