package com.github.hls.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "SimpleJob")
@NameStyle(value = Style.normal)
@Getter@Setter@ToString
public class SimpleJobDO {

    // 主键
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simpleJobId;
    // 任务名称
    private String jobName;
    // 描述
    private String description;
    // 数据源类型，mysql,hive
    private String sourceType;
    //用于区分增删改的数据源，如果为空默认无skynet
    private String upDateSource;
    // 查询SQL
    private String selectSQL;
    // 校对记录是否存在
    private String checkExistSQL;
    // 修改SQL
    private String updateSQL;
    // insert SQL
    private String insertSQL;
    // 状态，RUNNING 运行，PAUSE 暂停
    private String status;
    // 执行顺序
    private Long executeOrder;
    // 异常是否继续,Y 继续，N 不再接下去进行
    private String errorGoOn;
    // 录入时间
    private Date inputDate;
    // 记录修改时间
    private Date updateTime;
    // 记录更新时间
    private Date stampDate;
    // 从属JOB
    private String parentJobName;

}
