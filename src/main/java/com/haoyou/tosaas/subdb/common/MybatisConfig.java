package com.haoyou.tosaas.subdb.common;

import com.haoyou.tosaas.subdb.dynamicds.DynamicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Mybatis配置
 *
 * @author Louis
 * @date Oct 31, 2018
 */
@Configuration
@MapperScan(basePackages = {"com.haoyou.**.mapper"})
public class MybatisConfig {

//    @Bean("master")
//    @Primary
//    @ConfigurationProperties(prefix = "spring.datasource.master")
//    public DataSource master() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean("slave")
//    @ConfigurationProperties(prefix = "spring.datasource.slave")
//    public DataSource slave() {
//        return DataSourceBuilder.create().build();
//    }

    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
//        dataSourceMap.put("master", master());
//        dataSourceMap.put("slave", slave());
        // 将 master 数据源作为默认指定的数据源
//        dynamicDataSource.setDefaultDataSource(master());
        // 将 master 和 slave 数据源作为指定的数据源
        dynamicDataSource.setDataSources(dataSourceMap);
        return dynamicDataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        // 配置数据源，此处配置为关键配置，如果没有将 dynamicDataSource作为数据源则不能实现切换
        sessionFactory.setDataSource(dynamicDataSource());
        sessionFactory.setTypeAliasesPackage("com.haoyou.**.model");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        // 配置事务管理, 使用事务时在方法头部添加@Transactional注解即可
        return new DataSourceTransactionManager(dynamicDataSource());
    }
}
