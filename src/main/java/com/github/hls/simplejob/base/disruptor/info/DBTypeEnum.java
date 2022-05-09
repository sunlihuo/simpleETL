package com.github.hls.simplejob.base.disruptor.info;

public enum DBTypeEnum {
    ETL_BATCH(0),
    ETL(1),
    ETL_DEL(2);

    private int type;

    DBTypeEnum(int type){
        this.type = type;
    }

    public int getCode(){
        return this.type;
    }
}
