package com.streamTracker.database;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.ParameterizedType;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides connection to database for DAO classes.
 */
@AllArgsConstructor
public abstract class DatabaseReader<T> {

    /**
     * Session factory connected to database.
     */
    @NonNull
    public SqlSessionFactory sqlSessionFactory;

    /**
     * Executes get method on database and returns the result.
     *
     * @param func Method to be executed from mapper {@code T}.
     * @param <R>  Return type of the function.
     * @return Result of the func.
     */
    public <R> R get(Function<T, R> func) {
        try (SqlSession session = this.sqlSessionFactory.openSession()) {
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
        try (SqlSession session = this.sqlSessionFactory.openSession()) {
            T mapper = session.getMapper((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
            func.accept(mapper);
            session.commit();
        }
    }
}
