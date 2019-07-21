package com.haoyou.tosaas.subdb.dynamicds;

import com.alibaba.druid.pool.DruidDataSource;
import com.haoyou.tosaas.centerdb.dao.ServerConfigDAO;
import com.haoyou.tosaas.centerdb.po.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源实现类
 *
 * @author gxj
 * @date 2019/05/20
 */

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Autowired
    private MysqlConfig mysqlConfig;

    @Autowired
    ServerConfigDAO serverConfigDAO;


    Map<Object, Object> dataSources = new HashMap<>();

    /**
     * 如果不希望数据源在启动配置时就加载好，可以定制这个方法，从任何你希望的地方读取并返回数据源
     * 比如从数据库、文件、外部接口等读取数据源信息，并最终返回一个DataSource实现类对象即可
     */
    @Override
    protected DataSource determineTargetDataSource() {
        //如果数据源还没有创建，则动态创建
        String key=DynamicDataSourceContextHolder.getDataSourceKey();
        if(key!=null && !dataSources.containsKey(key)){
            //key值为mysql 服务器ip
            ServerConfig serverConfig=serverConfigDAO.findByServer_ip(key);
            if(serverConfig==null)
            {
                throw new RuntimeException("ServerConfig 找不到"+key);
            }
            String Server= String.format("%s:%d",serverConfig.getServer_ip(),serverConfig.getPort());
            String jdbcUrl=mysqlConfig.getJdbcUrl();
            jdbcUrl=jdbcUrl.replace("[SERVER]",Server);

            DruidDataSource dds = new DruidDataSource();
            dds.setUrl(jdbcUrl);
            dds.setUsername(serverConfig.getUser_name());
            dds.setPassword(serverConfig.getPass());
            dds.setDriverClassName(mysqlConfig.getDriverClassName());

            dataSources.put(key,dds);

            super.setTargetDataSources(dataSources);
            super.afterPropertiesSet();

            DynamicDataSourceContextHolder.setDataSourceKey(key);
        }
        return super.determineTargetDataSource();
    }

    /**
     * 如果希望所有数据源在启动配置时就加载好，这里通过设置数据源Key值来切换数据，定制这个方法
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }

    /**
     * 设置默认数据源
     *
     * @param defaultDataSource
     */
    public void setDefaultDataSource(Object defaultDataSource) {
        super.setDefaultTargetDataSource(defaultDataSource);
    }

    /**
     * 设置数据源
     *
     * @param dataSources
     */
    public void setDataSources(Map<Object, Object> dataSources) {
        super.setTargetDataSources(dataSources);
        this.dataSources = dataSources;
        // 将数据源的 key 放到数据源上下文的 key 集合中，用于切换时判断数据源是否有效
        DynamicDataSourceContextHolder.addDataSourceKeys(dataSources.keySet());
    }
}
