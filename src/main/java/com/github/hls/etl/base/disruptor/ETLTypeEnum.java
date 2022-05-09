package com.github.hls.etl.base.disruptor;

public enum ETLTypeEnum {
    ETL_BATCH(0),
    ETL(1),
    ETL_DEL(2);

    private int type;

    ETLTypeEnum(int type){
        this.type = type;
    }

    public int getCode(){
        return this.type;
    }
}
