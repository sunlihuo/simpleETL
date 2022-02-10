package com.github.hls.simplejob.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
@TableName("simple_job")
@ApiModel(description="")
public class SimpleJobEntity {

    @TableId
    @ApiModelProperty(value="主键", required=false)
    private Long simpleJobId;
    @ApiModelProperty(value="任务名称", required=false)
    private String jobName;
    @ApiModelProperty(value="描述", required=false)
    private String description;
    @ApiModelProperty(value="数据源类型，mysql,hive", required=false)
    private String sourceType;
    @ApiModelProperty(value="来源数据源类型", required=false)
    private String sourceDb;
    @ApiModelProperty(value="查询SQL", required=false)
    private String selectSql;
    @ApiModelProperty(value="校对记录是否存在", required=false)
    private String checkExistSql;
    @ApiModelProperty(value="修改SQL", required=false)
    private String updateSql;
    @ApiModelProperty(value="insert SQL", required=false)
    private String insertSql;
    @ApiModelProperty(value="状态，-1永远执行，0 不再执行  1执行并减1执行一次少一次", required=false)
    private Long status;
    @ApiModelProperty(value="执行顺序", required=false)
    private Long executeOrder;
    @ApiModelProperty(value="异常是否继续,Y 继续，N 不再接下去进行", required=false)
    private String errorGoOn;
    @ApiModelProperty(value="依赖父任务", required=false)
    private String parentJobName;
    @ApiModelProperty(value="记录更新时间", required=false)
    private Date gmtUpdate;
    @ApiModelProperty(value="创建时间", required=false)
    private Date gmtCreate;
    @ApiModelProperty(value="0 未删除， 1 已删除", required=false)
    private Boolean isDel;

}


