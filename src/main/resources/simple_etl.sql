CREATE TABLE `etl` (
`id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
`name` varchar(50) NOT NULL COMMENT '任务名称',
`status` int DEFAULT '-1' COMMENT '状态，-1永远执行，0 不再执行 1执行并减1执行一次少一次',
`description` varchar(1000) DEFAULT NULL COMMENT '描述',
`handle_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源类型见 HandleTypeEnums',
`source_db` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '来源数据源类型',
`select_sql` varchar(5000) NOT NULL COMMENT '查询SQL',
`check_exist_sql` varchar(1000) NOT NULL COMMENT '校对记录是否存在',
`batch_sql` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '批量SQL',
`update_sql` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '修改SQL',
`insert_sql` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '插入SQL',
`execute_order` tinyint(1) NOT NULL DEFAULT '0' COMMENT '执行顺序 从小到大0->10',
`error_go_on` tinyint(1) NOT NULL DEFAULT '1' COMMENT '异常是否继续,1 继续，0 不再接下去进行',
`gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
`gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 未删除， 1 已删除',
`gmt_running` datetime DEFAULT NULL COMMENT '任务执行时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDBDEFAULT CHARSET=utf8 COMMENT='etl记录表';

INSERT INTO `etl`(`id`, `name`, `status`, `description`, `handle_type`, `source_db`, `select_sql`, `check_exist_sql`, `batch_sql`, `update_sql`, `insert_sql`, `execute_order`, `error_go_on`,  `gmt_update`, `gmt_create`, `is_del`, `gmt_running`) VALUES (80, 'job1', -1, '示例任务', 'batch', 'store', 'SELECT id,name,age FROM t1 WHERE gmt_create<=NOW() ', 'select 1 from t2 where id=\'#id#\'', 'batch€_t2_€where id>0', NULL, NULL, 0, 1, '2022-05-03 23:12:31', '2022-05-03 23:02:47', 0, '2022-05-04 12:10:45');
INSERT INTO `etl`(`id`, `name`, `status`, `description`, `handle_type`, `source_db`, `select_sql`, `check_exist_sql`, `batch_sql`, `update_sql`, `insert_sql`, `execute_order`, `error_go_on`,  `gmt_update`, `gmt_create`, `is_del`, `gmt_running`) VALUES (81, 'job2', 979, '示例任务', 'auto', 'store', 'SELECT id,name,age FROM t1 WHERE gmt_create BETWEEN \'#startDate#\' AND \'#endDate#\'', 'select 1 from t2 where id=\'#id#\'', NULL, 'UPDATE t2 SET id=\'#id#\',name=\'#name#\',age=\'#age#\' WHERE id = ( SELECT a.id FROM ( SELECT id FROM t2 WHERE  id=\'#id#\' LIMIT 1 )a)', 'INSERT INTO t2(id,name,age)VALUES(\'#id#\',\'#name#\',\'#age#\')', 0, 1, '2022-05-04 12:10:38', '2022-05-03 23:02:47', 0, '2022-05-04 12:10:45');
INSERT INTO `etl`(`id`, `name`, `status`, `description`, `handle_type`, `source_db`, `select_sql`, `check_exist_sql`, `batch_sql`, `update_sql`, `insert_sql`, `execute_order`, `error_go_on`,  `gmt_update`, `gmt_create`, `is_del`, `gmt_running`) VALUES (82, '全局日期参数', -1, NULL, 'sys_value', '', 'SELECT DATE_SUB(CURDATE(), INTERVAL 3 DAY) startDate, DATE_SUB(CURDATE(), INTERVAL 0 DAY) endDate', '', NULL, NULL, NULL, 0, 1, '2022-05-04 11:38:27', '2022-05-04 11:35:49', 0, NULL);
