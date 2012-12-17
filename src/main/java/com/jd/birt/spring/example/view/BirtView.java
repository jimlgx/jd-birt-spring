/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.view
 * @file BirtView.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-16 下午8:35:16
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.api.ReportParameterConverter;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractView;

/**
 * <code>BirtView</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-16
 */
public class BirtView extends AbstractView {

	public static final String PARAM_ISNULL = "__isnull";
	public static final String UTF_8_ENCODE = "UTF-8";
	private IReportEngine birtEngine;
	private String reportNameRequestParameter = "ReportName";
	private String reportFormatRequestParameter = "ReportFormat";

	/**
	 * IRenderOption renderOptions :
	 * 
	 * @since 2012-12-17 wangjunming
	 */
	private IRenderOption renderOptions;

	public void setRenderOptions(IRenderOption ro) {
		this.renderOptions = ro;
	}

	public void setReportFormatRequestParameter(String rf) {
		Assert.hasText(rf, "the report format parameter must not be null");
		this.reportFormatRequestParameter = rf;
	}

	public void setReportNameRequestParameter(String rn) {
		Assert.hasText(rn, "the reportNameRequestParameter must not be null");
		this.reportNameRequestParameter = rn;
	}

	public void setBirtEngine(IReportEngine birtEngine) {
		this.birtEngine = birtEngine;
	}

	/**
	 * <code>renderMergedOutputModel</code>
	 * 
	 * 渲染合并后的数据到前台
	 * 
	 * @param map
	 * @param request
	 * @param response
	 * @throws Exception
	 * @since 2012-12-17 wangjunming
	 */
	protected void renderMergedOutputModel(Map<String, Object> map,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String reportName = getReportName(request);
		String format = getFormat(request);
		ServletContext sc = request.getSession().getServletContext();

		String reportPath = getReportPath(sc, reportName);

		IReportRunnable runnable = null;
		runnable = this.birtEngine.openReportDesign(reportPath);

		IRunAndRenderTask runAndRenderTask = this.birtEngine
				.createRunAndRenderTask(runnable);

		runAndRenderTask.setParameterValues(discoverAndSetParameters(runnable,
				request));

		response.setContentType(this.birtEngine.getMIMEType(format));

		IRenderOption options = getRenderOption();

		// IRenderOption options=null;

		if (format.equalsIgnoreCase("html")) {
			buildHtml(request, response, sc, runAndRenderTask, options);

		} else if (format.equalsIgnoreCase("pdf")) {

			buldPdf(response, runAndRenderTask, options);
		} else {
			buildAttachment4(response, reportName, format, runAndRenderTask,
					options);
		}

		// 设置 birt上下文参数
		runAndRenderTask.getAppContext().put("BIRT_VIEWER_HTTPSERVET_REQUEST",
				request);

		runAndRenderTask.run();
		runAndRenderTask.close();
	}

	/**
	 * <code>buildAttachment4</code>
	 * 
	 * @param response
	 * @param reportName
	 * @param format
	 * @param runAndRenderTask
	 * @param options
	 * @throws IOException
	 * @since 2012-12-17 wangjunming
	 */
	protected void buildAttachment4(HttpServletResponse response,
			String reportName, String format,
			IRunAndRenderTask runAndRenderTask, IRenderOption options)
			throws IOException {
		String att = "download." + format;
		String uReportName = reportName.toUpperCase();
		if (uReportName.endsWith(".RPTDESIGN")) {
			att = uReportName.replace(".RPTDESIGN", "." + format);
		}

		try {
			FileWriter fstream = new FileWriter("c:/test/out.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Hello Java " + format + "--"
					+ this.birtEngine.getMIMEType(format));

			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ att + "\"");
		options.setOutputStream(response.getOutputStream());
		options.setOutputFormat(format);

		runAndRenderTask.setRenderOption(options);
	}

	/**
	 * <code>buldPdf</code>
	 * 
	 * @param response
	 * @param runAndRenderTask
	 * @param options
	 * @throws IOException
	 * @since 2012-12-17 wangjunming
	 */
	protected void buldPdf(HttpServletResponse response,
			IRunAndRenderTask runAndRenderTask, IRenderOption options)
			throws IOException {
		PDFRenderOption pdfOptions = new PDFRenderOption(options);
		pdfOptions.setOutputFormat("pdf");
		pdfOptions
				.setOption("pdfRenderOption.pageOverflow", Integer.valueOf(2));
		pdfOptions.setOutputStream(response.getOutputStream());

		runAndRenderTask.setRenderOption(pdfOptions);
	}

	/**
	 * <code>buildHtml</code>
	 * 
	 * @param request
	 * @param response
	 * @param sc
	 * @param runAndRenderTask
	 * @param options
	 * @throws IOException
	 * @since 2012-12-17 wangjunming
	 */
	protected void buildHtml(HttpServletRequest request,
			HttpServletResponse response, ServletContext sc,
			IRunAndRenderTask runAndRenderTask, IRenderOption options)
			throws IOException {
		HTMLRenderOption htmlOptions = new HTMLRenderOption(options);

		htmlOptions.setOutputFormat("html");
		htmlOptions.setOutputStream(response.getOutputStream());
		htmlOptions.setImageHandler(new HTMLServerImageHandler());
		htmlOptions.setBaseImageURL(request.getContextPath() + "/images");
		htmlOptions.setImageDirectory(sc.getRealPath("/images"));

		runAndRenderTask.setRenderOption(htmlOptions);
	}

	/**
	 * <code>getRenderOption</code>
	 * 
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	protected IRenderOption getRenderOption() {
		return null == this.renderOptions ? new RenderOption()
				: this.renderOptions;
	}

	/**
	 * <code>getReportPath</code>
	 * 
	 * @param sc
	 * @param reportName
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	protected String getReportPath(ServletContext sc, String reportName) {
		String reportPath = sc.getRealPath("/Reports") + "/" + reportName;
		return reportPath;
	}

	/**
	 * <code>getFormat</code>
	 * 
	 * @param request
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	protected String getFormat(HttpServletRequest request) {
		String format = request.getParameter(this.reportFormatRequestParameter);

		if (format == null) {
			format = "html";
		}

		return format;
	}

	/**
	 * <code>getReportName</code>
	 * 
	 * @param request
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	protected String getReportName(HttpServletRequest request) {
		String reportName = request
				.getParameter(this.reportNameRequestParameter);
		return reportName;
	}

	/**
	 * <code>discoverAndSetParameters</code>
	 * 
	 * 设置报表参数
	 * 
	 * @param report
	 * @param request
	 * @return
	 * @throws Exception
	 * @since 2012-12-17 wangjunming
	 */
	protected HashMap<String, Object> discoverAndSetParameters(
			IReportRunnable report, HttpServletRequest request)
			throws Exception {
		HashMap<String, Object> parms = new HashMap<String, Object>();
		IGetParameterDefinitionTask task = this.birtEngine
				.createGetParameterDefinitionTask(report);

		@SuppressWarnings("unchecked")
		Collection<IParameterDefnBase> params = task.getParameterDefns(true);
		Iterator<IParameterDefnBase> iter = params.iterator();
		while (iter.hasNext()) {
			IParameterDefnBase param = (IParameterDefnBase) iter.next();

			IScalarParameterDefn scalar = (IScalarParameterDefn) param;
			if (request.getParameter(param.getName()) != null) {
				parms.put(param.getName(), getParamValueObject(request, scalar));
			}
		}
		task.close();
		return parms;
	}

	/**
	 * <code>getParamValueObject</code>
	 * 
	 * @param request
	 * @param parameterObj
	 * @return
	 * @throws Exception
	 * @since 2012-12-17 wangjunming
	 */
	protected Object getParamValueObject(HttpServletRequest request,
			IScalarParameterDefn parameterObj) throws Exception {
		String paramName = parameterObj.getName();
		String format = parameterObj.getDisplayFormat();
		if (doesReportParameterExist(request, paramName)) {
			ReportParameterConverter converter = new ReportParameterConverter(
					format, request.getLocale());

			String paramValue = getReportParameter(request, paramName, null);

			return converter.parse(paramValue, parameterObj.getDataType());
		}
		return null;
	}

	/**
	 * <code>getReportParameter</code>
	 * 
	 * 设置参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	public static String getReportParameter(HttpServletRequest request,
			String name, String defaultValue) {
		assert ((request != null) && (name != null));

		String value = getParameter(request, name);
		if ((value == null) || (value.length() <= 0)) {
			value = "";
		}

		@SuppressWarnings("unchecked")
		Map<String, String[]> paramMap = request.getParameterMap();
		if ((paramMap == null) || (!paramMap.containsKey(name))) {
			value = defaultValue;
		}

		Set<?> nullParams = getParameterValues(request, "__isnull");

		if ((nullParams != null) && (nullParams.contains(name))) {
			value = null;
		}

		return value;
	}

	/**
	 * <code>doesReportParameterExist</code>
	 * 
	 * 判断指定的 report 报表参数是否存在
	 * 
	 * @param request
	 * @param name
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	public static boolean doesReportParameterExist(HttpServletRequest request,
			String name) {
		assert ((request != null) && (name != null));

		boolean isExist = false;

		@SuppressWarnings("unchecked")
		Map<String, String[]> paramMap = request.getParameterMap();
		if (paramMap != null) {
			isExist = paramMap.containsKey(name);
		}
		Set<String> nullParams = getParameterValues(request, "__isnull");

		if ((nullParams != null) && (nullParams.contains(name))) {
			isExist = true;
		}

		return isExist;
	}

	/**
	 * <code>getParameter</code>
	 * 
	 * 获得请求的 参数的值
	 * 
	 * @param request
	 * @param parameterName
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	public static String getParameter(HttpServletRequest request,
			String parameterName) {
		if (request.getCharacterEncoding() == null) {
			try {
				request.setCharacterEncoding("UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return request.getParameter(parameterName);
	}

	/**
	 * <code>getParameterValues</code>
	 * 
	 * 获得指定 param key对应的值的集合
	 * 
	 * @param request
	 * @param parameterName
	 * @return
	 * @since 2012-12-17 wangjunming
	 */
	public static Set<String> getParameterValues(HttpServletRequest request,
			String parameterName) {
		Set<String> parameterValues = null;
		String[] parameterValuesArray = request
				.getParameterValues(parameterName);

		if (parameterValuesArray != null) {
			parameterValues = new LinkedHashSet<String>();

			for (int i = 0; i < parameterValuesArray.length; i++) {
				parameterValues.add(parameterValuesArray[i]);
			}
		}

		return parameterValues;
	}

}
