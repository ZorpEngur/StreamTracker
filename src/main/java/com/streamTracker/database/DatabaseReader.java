package com.streamTracker.database;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides connection to database for DAO classes.
 */
public abstract class DatabaseReader<T> {

    /**
     * Session factory connected to database.
     */
    public SqlSessionFactory sqlSessionFactory;

    public DatabaseReader() {
        Properties properties = new Properties();
        properties.setProperty("DB_URL", System.getenv().get("DB_URL"));
        properties.setProperty("DB_USER", System.getenv().get("DB_USER"));
        properties.setProperty("DB_PASSWORD", System.getenv().get("DB_PASSWORD"));
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream;
            inputStream = Resources.getResourceAsStream(resource);
            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes get method on database and returns the result.
     *
     * @param func Method to be executed from mapper {@code T}.
     * @param <R>  Return type of the function.
     * @return Result of the func.
     */
    public <R> R get(Function<T, R> func) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            T mapper = session.getMapper((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
            return func.apply(mapper);
        }
    }

    /**
     * Executes insert method on database.
     *
     * @param func Method to be executed from mapper {@code T}.
     */
    public void insert(Consumer<T> func) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            T mapper = session.getMapper((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
            func.accept(mapper);
            session.commit();
        }
    }
}
