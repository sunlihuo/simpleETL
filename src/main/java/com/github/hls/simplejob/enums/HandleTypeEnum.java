package com.github.hls.simplejob.enums;

public enum HandleTypeEnum {
    正常("normal"),
    自动SQL("auto"),
    批量("batch");

    private String code;

    HandleTypeEnum(String code) {
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
