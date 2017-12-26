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

@Table(name = "SimpleJobStatus")
@NameStyle(value = Style.normal)
@Getter@Setter@ToString
public class SimpleJobStatusDO {

    // 主键
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simpleJobStatusId;
    private Long simpleJobId;
    // 任务名称
    private String jobName;
    // 描述
    private String description;
    private String isSuccess;
    // 录入时间
    private Date inputDate;
    // 记录修改时间
    private Date updateTime;
    // 记录更新时间
    private Date stampDate;
}
