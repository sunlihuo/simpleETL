package com.github.hls.domain;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class BaseQueryInfo {
    private String id;
    private int rows;//每页行数
    private int page;//第几页
    private String sidx;//order by
    private String sord;//desc asc
}
