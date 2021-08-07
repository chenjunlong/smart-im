package com.smart.biz.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author chenjunlong
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Lazy
    public JdbcTemplate jdbcTemplate(@Qualifier("shardingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource, true);
    }
}
