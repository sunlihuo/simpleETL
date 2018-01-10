package com.github.hls.base.enums;

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
        mysql("mysqlStrategy"),//本地库
        auto_mysql("autoMysqlStrategy"),//从本地中查询，自动生成insert,update语句
        midDataMart("midMysqlStrategy"),//中间库
        auto_midDataMart("autoMidMysqlStrategy"),//从中间库中查询，自动生成insert,update语句
        clear_mid_mysql("clearMidMysqlStrategy"),//查询中间库是否存在，存在 就清表
        /**
         * //查询本地库，填充分段sqlValue到sql中，并支持多sql
         * [
         * {
         * "sqlStr":"SELECT  DATE_FORMAT(dataTime, '%Y-%m') as dataTime,region,wayport,partyId,partyName,warehouseId,warehouseName,warehouseType,endWarehouseId,endWarehouseName,fromSystem,warehouseProperty,'WHM' AS isInsert,'NO' AS isAll,#selectValue# FROM JobShippingOrderSys WHERE  DATE_FORMAT(dataTime, '%Y-%m') = DATE_FORMAT('#toDate#', '%Y-%m') AND isInsert='WHD' AND isAll='NO' GROUP BY  DATE_FORMAT(dataTime, '%Y-%m'), region,wayport,partyid,partyName,warehouseId,warehouseName,endWarehouseId,endWarehouseName,fromSystem,warehouseProperty",
         * "checkExistSQL":"batch€_JobShippingOrderSys_where isInsert='WHM' AND DATE_FORMAT(CONCAT(dataTime,'-01'), '%Y-%m') = DATE_FORMAT('#toDate#', '%Y-%m')"
         * }
         * ]
         */
        auto_mysql_json("");

        private String beanName;

        SOURCE_TYPE(String beanName) {
            this.beanName = beanName;
        }
    }

}
