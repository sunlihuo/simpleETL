package com.github.hls.simplejob.base.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.hls.simplejob.base.datasource.data.DataSourceNames;
import com.github.hls.simplejob.base.datasource.data.DynamicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置多数据源
 *
 * @author lc
 */
@Configuration
public class DynamicDataSourceConfig {
    /**
     * 创建 DataSource Bean
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.oldmain")
    public DruidDataSource oldmainDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.store")
    public DruidDataSource storeDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.datacenter")
    public DruidDataSource datacenterDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    /**
     * 如果还有数据源,在这继续添加 DataSource Bean
     */

    @Bean
    @Primary
    @DependsOn({ "oldmainDataSource", "datacenterDataSource", "storeDataSource"})
    public DynamicDataSource dataSource(DruidDataSource storeDataSource, DruidDataSource oldmainDataSource, DruidDataSource datacenterDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>(3);
        targetDataSources.put(DataSourceNames.oldmain, oldmainDataSource);
        targetDataSources.put(DataSourceNames.datacenter, datacenterDataSource);
        targetDataSources.put(DataSourceNames.store, storeDataSource);
        // 还有数据源,在targetDataSources中继续添加
        System.out.println("DataSources:" + targetDataSources);
        return new DynamicDataSource(datacenterDataSource, targetDataSources);
    }
}
