package com.github.hls.etl.base.datasource;

import com.github.hls.etl.base.datasource.data.DataSourceNames;
import com.github.hls.etl.base.datasource.data.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置多数据源
 *
 * @author sunlihuo
 */
@Slf4j
@Configuration
public class DynamicDataSourceConfig {
    /**
     * 创建 DataSource Bean
     */
    @Bean()
    @ConfigurationProperties(prefix = "spring.datasource.oldmain")
    public DataSource oldmainDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean()
    @ConfigurationProperties(prefix = "spring.datasource.store")
    public DataSource storeDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean()
    @ConfigurationProperties(prefix = "spring.datasource.datacenter")
    public DataSource datacenterDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 如果还有数据源,在这继续添加 DataSource Bean
     */

    @Bean
    @Primary
    @DependsOn({ "oldmainDataSource", "datacenterDataSource", "storeDataSource"})
    public DynamicDataSource dataSource(HikariDataSource storeDataSource, HikariDataSource oldmainDataSource, HikariDataSource datacenterDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceNames.oldmain, oldmainDataSource);
        targetDataSources.put(DataSourceNames.datacenter, datacenterDataSource);
        targetDataSources.put(DataSourceNames.store, storeDataSource);
        // 还有数据源,在targetDataSources中继续添加
        System.out.println("DataSources:" + targetDataSources);
        return new DynamicDataSource(datacenterDataSource, targetDataSources);
    }
}
