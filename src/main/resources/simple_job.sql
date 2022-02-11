SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for simple_job
-- ----------------------------
DROP TABLE IF EXISTS `simple_job`;
CREATE TABLE `simple_job` (
  `simple_job_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_name` varchar(50) NOT NULL COMMENT '任务名称',
  `status` bigint(20) DEFAULT '-1' COMMENT '状态，-1永远执行，0 不再执行  1执行并减1执行一次少一次',
  `description` varchar(1000) DEFAULT NULL COMMENT '描述',
  `source_type` varchar(50) NOT NULL COMMENT '数据源类型，mysql,hive',
  `source_db` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '来源数据源类型',
  `select_sql` varchar(5000) NOT NULL COMMENT '查询SQL',
  `check_exist_sql` varchar(1000) NOT NULL COMMENT '校对记录是否存在',
  `update_sql` varchar(5000) NOT NULL COMMENT '修改SQL',
  `insert_sql` varchar(5000) NOT NULL COMMENT 'insert SQL',
  `execute_order` int(11) NOT NULL COMMENT '执行顺序',
  `error_go_on` varchar(255) NOT NULL COMMENT '异常是否继续,Y 继续，N 不再接下去进行',
  `parent_job_name` varchar(255) NOT NULL COMMENT '依赖父任务',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `gmt_create` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 未删除， 1 已删除',
  PRIMARY KEY (`simple_job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8 COMMENT='job记录表';

