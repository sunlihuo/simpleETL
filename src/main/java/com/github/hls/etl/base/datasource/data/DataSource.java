package com.github.hls.etl.base.datasource.data;

/**
 * 多数据源注解
 * @author sunlihuo
 */

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
        String value() default DataSourceNames.datacenter;
}
