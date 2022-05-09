package com.github.hls.simplejob.base.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import com.github.hls.simplejob.base.disruptor.info.DataDTO;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class IntEventExceptionHandler implements ExceptionHandler<DataDTO> {

	public void handleEventException(Throwable ex, long sequence, DataDTO event) {
		log.error("handleEventException", ex);
	}

	public void handleOnStartException(Throwable ex) {
		log.error("handleOnStartException", ex);
	}

	public void handleOnShutdownException(Throwable ex) {
		log.error("handleOnShutdownException", ex);
	}
}