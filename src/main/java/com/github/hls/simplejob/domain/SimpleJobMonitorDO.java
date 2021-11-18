package com.github.hls.simplejob.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Getter@Setter@ToString
public class SimpleJobMonitorDO {

    // 主键
    private Long simpleJobMonitorId;
    private Long simpleJobId;
    // 任务名称
    private String jobName;
    // 描述
    private String description;
    private String status;
    private String parentJobName;
    // 录入时间
    private Date inputDate;
    // 记录修改时间
    private Date updateTime;
    // 记录更新时间
    private Date stampDate;
}
