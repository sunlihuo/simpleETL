# simplejob
一个简单的sql处理框架

######## SimpleJob表
| 字段| 类型|说明&备注 |
| -------- | -------- | -------- |
| simpleJobId|int(11)|主键|
| jobName|varchar(50)|任务名称|
| description|varchar(1000)|描述|
| sourceType|varchar(50)|数据源类型，mysql,hive|
| upDateSource|varchar(50)|更新数据源类型|
| selectSQL|varchar(5000)|查询SQL|
| checkExistSQL|varchar(1000)|校对记录是否存在|
| updateSQL|varchar(5000)|修改SQL|
| insertSQL|varchar(5000)|insert SQL|
| status|varchar(500)|状态，RUNNING 运行，PAUSE 暂停|
| executeOrder|int(11)|执行顺序|
| errorGoOn|varchar(255)|异常是否继续,Y 继续，N 不再接下去进行|
| parentJobName|varchar(255)|依赖父任务|
| inputDate|datetime|录入时间|
| updateTime|datetime|记录修改时间|
| stampDate|timestamp|记录更新时间|

######## SimpleJobMonitor表
| 字段| 类型|说明&备注 |
| -------- | -------- | -------- |
| simpleJobMonitorId|int(11)|主键|
| simpleJobId|int(19)|SimpleJobId|
| jobName|varchar(50)|任务名称|
| description|varchar(1000)|描述|
| status|varchar(150)|是否成功|
| parentJobName|varchar(255)|依赖父任务|
| inputDate|datetime|录入时间|
| updateTime|datetime|记录修改时间|
| stampDate|timestamp|记录更新时间|


sourceType

```
    @Getter
    public static enum SOURCE_TYPE {
        section_value("sectionValueStrategy"),//分段sql,提取共同部分
        mysql("mysqlStrategy"),//本地库
        auto_mysql("autoMysqlStrategy"),//从本地中查询，自动生成insert,update语句
        midDataMart("midMysqlStrategy"),//中间库
        auto_midDataMart("autoMidMysqlStrategy"),//从中间库中查询，自动生成insert,update语句
        clear_mid_mysql("clearMidMysqlStrategy"),//查询中间库是否存在，存在 就清表

        private String beanName;

        SOURCE_TYPE(String beanName) {
            this.beanName = beanName;
        }
    }
```

#### sql自动生成：
skynetJob.sourceType 为auto_开头。
编写更新和插入sql,已经优化为自动生成。
原则是：
skynetJob.selectSQL里写select 五个字段 from table
skynetJob.checkExistSQL 里写 SELECT count(1) FROM table WHERE 四个字段
自动生成更新sql是 update 五个字段 from table WHERE 四个字段
自动生成插入sql是 insert into table 五个字段
table是从skynetJob.checkExistSQL 中截取，FROM WHERE  或者 from where，必须一起大写，一起小写。

在非自动生成，sql的情况下 skynetJob.updateSQL  和skynetJob.insertSQL为空就不执行sql;

#### 分段sql:
skynetJob.sourceType是section_value,//分段sql,提取共同部分
section_value优点，提取共同部分，一处修改，处处生效。拥抱变化。
更加强大的优点在上千万数据级时的细化，拆分能力！！！
```
SELECT '2017-01' as dataTime
UNION all
SELECT '2017-02' as dataTime
UNION all
SELECT '2017-03' as dataTime
```
原本一次查询千万数据处理时会系统崩溃。
改成了分多批次执行，群狼战术。完美运行。

#### 批量插入：
skynetJob.checkExistSQL中写
示例：batch?_JobTransportStatsSys_where dataTime>='#toDate#' and isInsert='WHD'
batch?_table[_where condition]
batch?_table   表示批量插入table表,不进行删除
batch?_table_where condition    表示批量插入table表并按 table_where condition  来生成DELETE sql，写where 条件保证业务的幂等。不需要专门去清表。

###### 优点，批量入库的速度可以让时光倒流。

表死锁及解决办法：
引入Disruptor，sql高并发执行，引起mysql死锁，主要原因是update语句条件范围过大，多条sql锁住了相同一批数据，
解决方法是按主键更新，skynetJob会自动生成如下sql
UPDATE 表名称 SET 列名称 = 新值 WHERE id in (select a.id from (select id from 表 where 列名称 = 某值)a)
而你只要写 UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值

##### 注意点   FROM   WHERE  关键字必须要都大写 或 小写
id 是生动生成 规则是  tableId

#### 血缘任务依赖，使用MQ支持集群化任务处理
任务job1依赖job2,执行job1时job2没有执行，会记录到SimpleJobMonitor表status是waiting
job2执行完成时job1会触发

#### 示例

```
INSERT INTO skynet.SimpleJob (simpleJobId, jobName, description, sourceType, upDateSource, selectSQL, checkExistSQL, updateSQL, insertSQL, status, executeOrder, errorGoOn, parentJobName, inputDate, updateTime, stampDate) VALUES ('12', 'TransportStatsSys_YesterDay', '运输分系统', 'section_value', '', 'SELECT\r\n	DATE_SUB(CURDATE(), INTERVAL 1 DAY) AS toDate,\r\n	CURDATE() AS loadDate,\r\n	\'SUM(IFNULL(warehouseIncreNum,0)) as warehouseIncreNum,\r\n	SUM(IFNULL(acceptordernum,0)) as acceptordernum,\r\n	SUM(IFNULL(shipordernum,0)) as shipordernum,\r\n	SUM(IFNULL(startnum,0)) as startnum,\r\n	SUM(IFNULL(arrivenum,0)) as arrivenum,\r\n	SUM(IFNULL(shippingfee,0)) as shippingfee,\r\n	SUM(IFNULL(insteadfee,0)) as insteadfee,\r\n	SUM(IFNULL(insurancefee,0)) as insurancefee,\r\n	SUM(IFNULL(carrierincrenum,0)) as carrierincrenum,\r\n	SUM(IFNULL(senderincrenum,0)) as senderincrenum,\r\n	SUM(IFNULL(receiverincrenum,0)) as receiverincrenum\' AS selectValue', '', '', '', 'RUNNING', '4', 'Y', '', NULL, NULL, '2018-01-08 15:20:47');
INSERT INTO skynet.SimpleJob (simpleJobId, jobName, description, sourceType, upDateSource, selectSQL, checkExistSQL, updateSQL, insertSQL, status, executeOrder, errorGoOn, parentJobName, inputDate, updateTime, stampDate) VALUES ('14', 'TransportStatsSys_YesterDay', '运输分系统网点天', 'auto_mysql', '', 'SELECT\r\n	date as dataTime,\r\n	region,\r\n	wayport,\r\n	partyId,\r\n	partyName,\r\n	warehouseId,\r\n	warehouseName,\r\n	warehouseType,\r\n	fromSystem,\r\n	warehouseProperty,\r\n	\'WHD\' as isInsert,\r\n	\'NO\' as isAll,\r\n	IFNULL(acceptOrderNum,0) as acceptordernum,\r\n	IFNULL(shipOrderNum,0) as shipordernum,\r\n	IFNULL(startNum,0) as startnum,\r\n	IFNULL(arriveNum,0) as arrivenum,\r\n	IFNULL(shippingFee,0) as shippingfee,\r\n	IFNULL(insteadFee,0) as insteadfee,\r\n	IFNULL(insuranceFee,0) as insurancefee,\r\n  IFNULL(warehouseIncreNum,0) as warehouseIncreNum,\r\n  IFNULL(senderNum,0) as senderincrenum,\r\n  IFNULL(receiverNum,0) as receiverincrenum\r\nFROM\r\n	BossTransportStats\r\nWHERE\r\n	loadDate = \'#loadDate#\'\r\nAND date >= \'#toDate#\'\r\nAND date < CURDATE()', 'batch€_JobTransportStatsSys_where dataTime>=\'#toDate#\' and isInsert=\'WHD\'', '', '', 'RUNNING', '5', 'Y', '', NULL, NULL, '2018-01-08 15:20:40');
```
