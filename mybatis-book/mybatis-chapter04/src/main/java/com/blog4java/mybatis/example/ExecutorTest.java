package com.blog4java.mybatis.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ReuseExecutor;
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

    @Test
    public void reuseTest()throws Exception {
        ReuseExecutor reuseExecutor = new ReuseExecutor(configuration, jdbcTransaction);
        MappedStatement ms = configuration.getMappedStatement(
            "com.blog4java.mybatis.example.mapper.UserMapper.listAllUser");
        List<Object> list = reuseExecutor.doQuery(ms, null, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER,
            ms.getBoundSql(null));
        System.out.println(list);
    }

    /**
     * 批处理只针对对数据库的update
     * @throws Exception
     */
    @Test
    public void batchTest()throws Exception {
        BatchExecutor executor = new BatchExecutor(configuration, jdbcTransaction);
        MappedStatement ms = configuration.getMappedStatement(
            "com.blog4java.mybatis.example.mapper.UserMapper.setName");
        Map param = new HashMap();
        param.put("art0", 10);
        param.put("arg1", "test");
        //这里多运行几次，但是只会预编译异常sql
        executor.doUpdate(ms, param);
        executor.doUpdate(ms, param);
        executor.doUpdate(ms, param);
    }

    //缓存逻辑

    /**
     * 二级缓存是在提交后，缓存才会有数据，二级缓存会进行夸线程
     * 一级缓存是在执行后就有，不需要等提交，不夸线程
     * @throws SQLException
     */
    @Test
    public void cacheExecutorTest() throws SQLException {
        Executor simpleExecutor = new SimpleExecutor(configuration, jdbcTransaction);
        //装饰模式，让Executor带有二级缓存功能
        Executor cachingExecutor = new CachingExecutor(simpleExecutor);
        MappedStatement ms = configuration.getMappedStatement(
            "com.blog4java.mybatis.example.mapper.UserMapper.setName");
        cachingExecutor.query(ms, 10, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
        cachingExecutor.commit(true);// 故意提交，让应用走二级缓存，1,先走二级缓存，2,在走一级缓存
        cachingExecutor.query(ms, 10, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    }
}
