package com.github.hls.simplejob.base.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import com.github.hls.simplejob.base.disruptor.info.DataInfo;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class IntEventExceptionHandler implements ExceptionHandler<DataInfo> {

	public void handleEventException(Throwable ex, long sequence, DataInfo event) {
		log.error("handleEventException", ex);
	}

	public void handleOnStartException(Throwable ex) {
		log.error("handleOnStartException", ex);
	}

	public void handleOnShutdownException(Throwable ex) {
		log.error("handleOnShutdownException", ex);
	}
}