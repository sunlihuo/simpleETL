package com.github.hls.simplejob.base.disruptor.info;

public enum DBTypeEnum {
    批量(0),
    校验_插入_更新(1),
    删除(2);

    private int type;

    DBTypeEnum(int type){
        this.type = type;
    }

    public int getCode(){
        return this.type;
    }
}
