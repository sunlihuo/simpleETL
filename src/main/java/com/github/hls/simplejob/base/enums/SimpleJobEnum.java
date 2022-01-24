package com.github.hls.simplejob.base.enums;

import lombok.Getter;

public class SimpleJobEnum {
    @Getter
    public static enum STATUS {
        RUNNING("运行"), STOP("暂停");
        private String des;

        STATUS(String des) {
            this.des = des;
        }
    }

    @Getter
    public static enum SOURCE_TYPE {
        section_value("sectionValueStrategy"),//分段sql,提取共同部分
        mysql("autoPageStrategy"),
        auto_mysql("autoPageStrategy");

        private String beanName;

        SOURCE_TYPE(String beanName) {
            this.beanName = beanName;
        }
    }

}
