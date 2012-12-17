/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.config
 * @file BirtWebConfiguration.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-16 下午8:39:02
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.config;

//import org.eclipse.birt.spring.core.BirtEngineFactory;
//import org.eclipse.birt.spring.core.BirtView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import com.jd.birt.spring.example.view.BirtView;

/**
 * <code>BirtWebConfiguration</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-16
 */

@EnableWebMvc
@ComponentScan({ "com.jd.birt.spring.example" })
@Configuration
public class BirtWebConfiguration extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/reports").setViewName("birtView");

	}

	@Bean
	public BirtView birtView() {
		BirtView bv = new BirtView();
		bv.setBirtEngine(this.engine().getObject());
		return bv;
	}

	@Bean
	public BeanNameViewResolver beanNameResolver() {
		BeanNameViewResolver br = new BeanNameViewResolver();
		return br;
	}

	@Bean
	protected BirtEngineFactory engine() {
		BirtEngineFactory factory = new BirtEngineFactory();
		// Enable BIRT Engine Logging
		// factory.setLogLevel( Level.FINEST);
		// factory.setLogDirectory( new FileSystemResource("c:/temp"));

		return factory;
	}

}
