package com.github.hls.simplejob.enums;

public enum HandleTypeEnum {
    正常("normal"),
    自动SQL("auto"),
    批量("batch"),
    批量_清理("batch_clear");

    private String code;

    HandleTypeEnum(String code) {
        this.code = code;
    }
}
