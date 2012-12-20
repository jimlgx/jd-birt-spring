/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.config
 * @file BirtEngineTest.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-17 下午4:21:53
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.config;

import javax.servlet.ServletContext;

import org.eclipse.birt.report.engine.api.IReportEngine;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * <code>BirtEngineTest</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-17
 */
// @Configuration
@ComponentScan({ "com.jd.birt.spring.example" })
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { BirtEngineFactory.class })
public class BirtEngineTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void getBirtEngine() {
		IReportEngine engine = this.applicationContext
				.getBean(IReportEngine.class);

		System.out.println("engine = " + engine);
	}
}
