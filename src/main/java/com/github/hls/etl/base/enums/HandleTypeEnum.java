package com.github.hls.etl.base.enums;

public enum HandleTypeEnum {
    正常("normal"),
    自动SQL("auto"),
    批量("batch"),

    分段_参数("section_value"),
    全局_参数("sys_value");

    private String code;

    HandleTypeEnum(String code) {
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
