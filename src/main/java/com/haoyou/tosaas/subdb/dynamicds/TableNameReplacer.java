package com.haoyou.tosaas.subdb.dynamicds;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.haoyou.tosaas.common.DBIDHolder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

/**
 * mybatis sql 拦截器
 *
 * 由于连接池都是连接到mysql库，拦截到的sql表名前面都要加上相应的数据库名字
 * @author gxj
 * @date 2019/06/12
 */
@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TableNameReplacer implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        String dbId = DBIDHolder.get();
        if (StringUtils.isEmpty(dbId)) {
            //当创建业务数据库时，不需要设置dbId
            //throw new RuntimeException("DBIDHolder获取不到dbid!");
            return invocation.proceed();
        }


        String dbinstance = "db_" + dbId;

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        // 分离代理对象链
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }

        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");

        if (StringUtils.isEmpty(sql))
            throw new RuntimeException("sql不能为空");
//        boolean replaced = false;
//        if (sql.contains("mysql.")) {
//            replaced = true;
//            sql = sql.replace("mysql.", dbinstance + ".");
//        }
        //可能会执行create database 等sql ,不需要加 mysql.
//        if (!replaced) {
//            System.out.println("sql:" + sql);
//            throw new RuntimeException("sql里表名必须加mysql.");
//        }

        //使用sql解析器改造sql
        // 新建 MySQL Parser
        SQLStatementParser parser = new MySqlStatementParser(sql);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement sqlStatement = parser.parseStatement();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        sqlStatement.accept(visitor);

        Map<TableStat.Name, TableStat> mapTable =  visitor.getTables();
        for (Map.Entry<TableStat.Name, TableStat> entry : mapTable.entrySet()){
            sql = sql.replace(entry.getKey().getName(), dbinstance + "."+entry.getKey().getName());
        }
        //

        metaStatementHandler.setValue("delegate.boundSql.sql", sql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
