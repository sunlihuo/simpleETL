# ETL
```
一个简单的ETL处理框架，将数据从来源端经过抽取（extract）、转换（transform）、加载（load）至目的端

支持多数据源查询，多数据源入库，支持集群，父子任务依赖
```
* handle_type
```
正常("normal"),
自动SQL("auto"),
批量("batch"),
分段_参数("section_value");
```

* 分段sql:
```
section_value优点，提取共同部分
SELECT '2017-01' as dataTime
UNION all
SELECT '2017-02' as dataTime
UNION all
SELECT '2017-03' as dataTime
原本一次查询千万数据处理时会系统崩溃。
改成了分多批次执行.
```

* 批量插入：
```
handle_type为batch
batchSQL填入执行sql
示例：batch€_table_€where dataTime>='#toDate#' and isInsert='WHD'
batch€_table_€[where condition]
带where会根据where条件去清表,保证任务幂等.
```

* 表死锁及解决办法：
```
引入Disruptor，sql高并发执行，引起mysql死锁，主要原因是update语句条件范围过大，多条sql锁住了相同一批数据，
解决方法是按主键更新，skynetetl会自动生成如下sql
UPDATE 表名称 SET 列名称 = 新值 WHERE id in (select a.id from (select id from 表 where 列名称 = 某值)a)
而你只要写 UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
```
##### 注意点   FROM   WHERE  关键字必须要都大写 或 小写

