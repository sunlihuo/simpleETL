package com.github.hls.base.disruptor;

import com.github.hls.base.disruptor.info.CheckUpInInfo;
import org.apache.log4j.Logger;

import com.lmax.disruptor.ExceptionHandler;


public class IntEventExceptionHandler implements ExceptionHandler<CheckUpInInfo> {
	private static final Logger logger = Logger.getLogger(IntEventExceptionHandler.class);

	public void handleEventException(Throwable ex, long sequence, CheckUpInInfo event) {
		logger.error("handleEventException", ex);
	}

	public void handleOnStartException(Throwable ex) {
		logger.error("handleOnStartException", ex);
	}

	public void handleOnShutdownException(Throwable ex) {
		logger.error("handleOnShutdownException", ex);
	}
}