package com.blog4java.mybatis.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.Before;
import org.junit.Test;

public class ExecutorTest {

    private Configuration configuration;
    private Connection connection;
    private JdbcTransaction jdbcTransaction;

    @Before
    public void init() throws Exception {
        // 获取配置文件输入流
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        // 通过SqlSessionFactoryBuilder的build()方法创建SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 调用openSession()方法创建SqlSession实例
        SqlSession sqlSession = sqlSessionFactory.openSession();
         configuration = sqlSession.getConfiguration();
         this.connection = sqlSession.getConnection();
         this.jdbcTransaction = new JdbcTransaction(connection);
    }

    @Test
    public void simpleTest()throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, jdbcTransaction);
        MappedStatement ms = configuration.getMappedStatement(
            "com.blog4java.mybatis.example.mapper.UserMapper.listAllUser");
        List<Object> list = simpleExecutor.doQuery(ms, null, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER,
            ms.getBoundSql(null));
        System.out.println(list);
    }
}
