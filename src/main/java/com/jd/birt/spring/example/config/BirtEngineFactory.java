/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.config
 * @file BirtEngineFactory.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-16 下午8:27:00
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.config;

import java.io.File;
import java.io.IOException;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.osgi.service.component.annotations.Component;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * <code>BirtEngineFactory</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-16
 */
// @Component
public class BirtEngineFactory implements FactoryBean<IReportEngine>,
		ApplicationContextAware, DisposableBean {

	/**
	 * String SPRING_CONTEXT :birt上下文中 spring库的标志
	 * 
	 * @since 2012-12-17 wangjunming
	 */
	public static final String SPRING_CONTEXT = "spring";

	public boolean isSingleton() {
		return true;
	}

	private ApplicationContext context;
	private IReportEngine birtEngine;

	// private Resource logDirectory;

	private File _resolvedDirectory;
	private java.util.logging.Level logLevel;

	/**
	 * <code>setApplicationContext</code>
	 * 
	 * 
	 * @param ctx
	 * @since 2012-12-17 wangjunming
	 */
	public void setApplicationContext(ApplicationContext ctx) {
		this.context = ctx;
	}

	/**
	 * <code>destroy</code>
	 * 
	 * 销毁 birtEngine
	 * 
	 * @throws Exception
	 * @since 2012-12-17 wangjunming
	 */
	public void destroy() throws Exception {
		birtEngine.destroy();
		Platform.shutdown();
	}

	public void setLogLevel(java.util.logging.Level ll) {
		this.logLevel = ll;
	}

	/**
	 * <code>setLogDirectory</code>
	 * 
	 * 设置日志生成目录的位置
	 * 
	 * @param resource
	 * @since 2012-12-17 wangjunming
	 */
	public void setLogDirectory(org.springframework.core.io.Resource resource) {
		File f = null;
		try {
			f = resource.getFile();
			validateLogDirectory(f);
			this._resolvedDirectory = f;
		} catch (IOException e) {
			throw new RuntimeException("couldn’t set the log directory");
		}

	}

	private void validateLogDirectory(File f) {
		Assert.notNull(f, " the directory must not be null");
		Assert.isTrue(f.isDirectory(), " the path given must be a directory");
		Assert.isTrue(f.exists(), "the path specified must exist!");
	}

	/**
	 * <code>setLogDirectory</code>
	 * 
	 * 设置日志文件位置
	 * 
	 * @param f
	 * @since 2012-12-17 wangjunming
	 */
	public void setLogDirectory(java.io.File f) {
		validateLogDirectory(f);
		this._resolvedDirectory = f;
	}

	/**
	 * <code>getObject</code>
	 * 
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	public IReportEngine getObject() {

		if (this.birtEngine == null) {
			this.birtEngine = createReportEngine();
		}

		return this.birtEngine;
	}

	/**
	 * <code>createReportEngine</code>
	 * 
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	protected IReportEngine createReportEngine() {
		EngineConfig config = new EngineConfig();

		// This line injects the Spring Context into the BIRT Context
		config.getAppContext().put(SPRING_CONTEXT, this.context);

		config.setLogConfig(
				null != this._resolvedDirectory ? this._resolvedDirectory
						.getAbsolutePath() : null, this.logLevel);
		try {

			// 根据 EngineConfig 启动 birt engine

			Platform.startup(config);

		} catch (BirtException e) {

			throw new RuntimeException("Could not start the Birt engine!", e);

		}

		// 获得 IReportEngineFactory
		IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);

		// 获得 IReportEngine
		IReportEngine be = factory.createReportEngine(config);

		this.birtEngine = be;

		return be;
	}

	@Override
	public Class<IReportEngine> getObjectType() {
		return IReportEngine.class;
	}
}
