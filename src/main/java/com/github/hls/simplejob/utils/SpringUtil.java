package com.github.hls.simplejob.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("applicationContext正在初始化,application:"+applicationContext);
		SpringUtil.applicationContext = applicationContext;
	}

	/**
	 * 从spring容器中获取bean
	 * @param name
	 * @param <T>
	 * @return
	 * @throws BeansException
	 */
	public static <T> T getBean(String name) throws BeansException {
		return (T) applicationContext.getBean(name);
	}
}
