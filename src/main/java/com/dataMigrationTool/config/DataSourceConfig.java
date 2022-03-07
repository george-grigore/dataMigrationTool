package com.dataMigrationTool.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Autowired
    private Environment env;

	@Primary
    @Bean(name = "db1")
    public DataSource db1() throws NamingException {
        return DataSourceBuilder.create()
                .url(env.getProperty("app.datasource.db1.url"))
                .driverClassName(env.getProperty("app.datasource.db1.driver-class-name"))
                .username(env.getProperty("app.datasource.db1.username"))
                .password(env.getProperty("app.datasource.db1.password"))
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "db2")
    public DataSource db2() throws NamingException {
        return DataSourceBuilder.create()
                .url(env.getProperty("app.datasource.db2.url"))
                .driverClassName(env.getProperty("app.datasource.db2.driver-class-name"))
                .username(env.getProperty("app.datasource.db2.username"))
                .password(env.getProperty("app.datasource.db2.password"))
                .type(HikariDataSource.class)
                .build();
    }
}
