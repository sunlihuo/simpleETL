package com.github.hls.domain;


import com.github.pagehelper.Page;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class JqGridVO<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String message;
	protected String code;
	private boolean success = true; // 执行是否成功

	private long records = 0;// json中代表数据行总数的数据
	private T rows; // json中代表实际模型数据的入口
	private long total;// json中代表页码总数的数据
	private long page;// json中代表当前页码的数据

	public JqGridVO() {
    }

	public JqGridVO(Boolean bool) {
		success = bool;
	}

	public JqGridVO(T rows, Page pageDO) {
		this.setRows(rows);
		this.setRecords(pageDO.getTotal());
		this.setPage(pageDO.getPageNum());
		this.setTotal(pageDO.getPages());
	}


}
