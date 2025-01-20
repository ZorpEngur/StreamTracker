package com.streamTracker.database;

import com.streamTracker.database.twitch.ITwitchBotMapper;
import com.streamTracker.database.twitch.TwitchBotDAO;
import com.streamTracker.database.twitch.TwitchBotService;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@Import(FlywayAutoConfiguration.class)
public class DatabaseConfiguration {

    @Bean @NonNull
    public SqlSessionFactory sqlSessionFactory(@NonNull DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        SqlSessionFactory sessionFactory = Objects.requireNonNull(factoryBean.getObject());

        sessionFactory.getConfiguration().addMapper(ITwitchBotMapper.class);

        return sessionFactory;
    }

    @Bean @NonNull
    public TwitchBotService twitchBotService(@NonNull SqlSessionFactory sessionFactory) {
        return new TwitchBotService(new TwitchBotDAO(sessionFactory));
    }
}
