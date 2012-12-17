/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.config
 * @file BirtDataServiceConfiguration.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-16 下午8:23:28
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.jd.birt.spring.example.api.CarService;
import com.jd.birt.spring.example.service.CarServiceImpl;

/**
 * <code>BirtDataServiceConfiguration</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-16
 */
@Configuration
public class BirtDataServiceConfiguration {

	@Bean
	public CarService carService() {
		return new CarServiceImpl();

	}

	@Bean
	public HttpInvokerServiceExporter myServiceExporter() {
		HttpInvokerServiceExporter hse = new HttpInvokerServiceExporter();
		hse.setService(this.carService());
		hse.setServiceInterface(CarService.class);
		return hse;
	}

	@Bean
	public SimpleUrlHandlerMapping myUrlMapping() {

		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		Map urlMap = new HashMap();
		urlMap.put("/carService", myServiceExporter());
		mapping.setUrlMap(urlMap);
		mapping.setAlwaysUseFullPath(true);
		return mapping;
	}

}
