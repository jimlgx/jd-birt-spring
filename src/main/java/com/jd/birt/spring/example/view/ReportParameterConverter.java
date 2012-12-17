/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.view
 * @file ReportParameterConverter.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-16 下午9:16:38
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.view;

import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.util.Locale;
import org.eclipse.birt.core.format.DateFormatter;
import org.eclipse.birt.core.format.NumberFormatter;
import org.eclipse.birt.core.format.StringFormatter;

/**
 * <code>ReportParameterConverter</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-16
 */
public class ReportParameterConverter {
	private String format = null;
	private ULocale uLocale = null;
	private TimeZone timeZone = TimeZone.getDefault();

	private StringFormatter sf = null;
	private DateFormatter df = null;
	private NumberFormatter nf = null;

	public ReportParameterConverter(String format, Locale locale) {
		this(format, ULocale.forLocale(locale));
	}

	public ReportParameterConverter(String format, ULocale uLocale) {
		this(format, uLocale, null);
	}

	public ReportParameterConverter(String format, ULocale uLocale,
			TimeZone timeZone) {
		this.format = format;
		this.uLocale = uLocale;
		if (timeZone != null) {
			this.timeZone = timeZone;
		}
	}

	private StringFormatter getStringFormatter() {
		if ((this.sf == null) && (this.uLocale != null)) {
			this.sf = new StringFormatter(this.uLocale);
			if (this.format != null) {
				this.sf.applyPattern(this.format);
			}
		}
		return this.sf;
	}

	private NumberFormatter getNumberFormatter() {
		if ((this.nf == null) && (this.uLocale != null)) {
			this.nf = new NumberFormatter(this.uLocale);
			if (this.format != null) {
				this.nf.applyPattern(this.format);
			}
		}
		return this.nf;
	}

	private DateFormatter getDateFormatter() {
		if ((this.df == null) && (this.uLocale != null)) {
			this.df = new DateFormatter(this.uLocale, this.timeZone);
			if (this.format != null) {
				this.df.applyPattern(this.format);
			}
		}
		return this.df;
	}

	public String format(Object reportParameterObj) {
		String reportParameterValue = null;

		if ((reportParameterObj != null) && (this.uLocale != null)) {
			if ((reportParameterObj instanceof String)) {
				StringFormatter sf = getStringFormatter();
				if (sf != null) {
					reportParameterValue = sf
							.format((String) reportParameterObj);
				} else {
					reportParameterValue = reportParameterObj.toString();
				}
			} else if ((reportParameterObj instanceof java.util.Date)) {
				DateFormatter df = getDateFormatter();
				if (df != null) {
					reportParameterValue = df
							.format((java.util.Date) reportParameterObj);
				} else {
					reportParameterValue = reportParameterObj.toString();
				}
			} else if ((reportParameterObj instanceof Double)) {
				NumberFormatter nf = getNumberFormatter();
				if (nf != null) {
					reportParameterValue = nf
							.format(((Double) reportParameterObj).doubleValue());
				} else {
					reportParameterValue = reportParameterObj.toString();
				}
			} else if ((reportParameterObj instanceof BigDecimal)) {
				NumberFormatter nf = getNumberFormatter();
				if (nf != null) {
					reportParameterValue = nf
							.format((BigDecimal) reportParameterObj);
				} else {
					reportParameterValue = reportParameterObj.toString();
				}
			} else if ((reportParameterObj instanceof Boolean)) {
				reportParameterValue = ((Boolean) reportParameterObj)
						.toString();
			} else if ((reportParameterObj instanceof Number)) {
				NumberFormatter nf = getNumberFormatter();
				if (nf != null) {
					reportParameterValue = nf
							.format((Number) reportParameterObj);
				} else {
					reportParameterValue = reportParameterObj.toString();
				}
			} else {
				reportParameterValue = reportParameterObj.toString();
			}
		}

		return reportParameterValue;
	}

	public Object parse(String reportParameterValue, int parameterValueType) {
		Object parameterValueObj = null;

		if ((reportParameterValue != null) && (this.uLocale != null)) {
			switch (parameterValueType) {
			case 1:
				StringFormatter sf = getStringFormatter();
				if (sf == null) {
					parameterValueObj = null;
				} else {
					try {
						parameterValueObj = sf.parser(reportParameterValue);
					} catch (ParseException e) {
						parameterValueObj = reportParameterValue;
					}

				}

			case 4:
				parameterValueObj = parseDateTime(reportParameterValue);
				break;
			case 2:
				NumberFormatter nf = getNumberFormatter();
				if (nf == null) {
					parameterValueObj = null;
				} else {
					try {
						Number num = nf.parse(reportParameterValue);

						if (num != null) {
							parameterValueObj = new Double(num.toString());
						}
					} catch (ParseException e) {
						nf.applyPattern("General Number");
						try {
							Number num = nf.parse(reportParameterValue);

							if (num != null) {
								parameterValueObj = new Double(num.toString());
							}
						} catch (ParseException ex) {
							parameterValueObj = null;
						}

					}

				}

			case 3:
				NumberFormatter nf3 = getNumberFormatter();
				if (nf3 == null) {
					parameterValueObj = null;
				} else {
					try {
						Number num = nf3.parse(reportParameterValue);

						if (num != null) {
							parameterValueObj = new BigDecimal(num.toString());
						}
					} catch (ParseException e) {
						nf3.applyPattern("General Number");
						try {
							Number num = nf3.parse(reportParameterValue);

							if (num != null) {
								parameterValueObj = new BigDecimal(
										num.toString());
							}
						} catch (ParseException ex) {
							parameterValueObj = null;
						}

					}

				}

			case 5:
				parameterValueObj = Boolean.valueOf(reportParameterValue);
				break;
			case 7:
				try {
					parameterValueObj = java.sql.Date
							.valueOf(reportParameterValue);
				} catch (IllegalArgumentException ie) {
					parameterValueObj = parseDateTime(reportParameterValue);
					if (parameterValueObj != null) {
						parameterValueObj = new java.sql.Date(
								((java.util.Date) parameterValueObj).getTime());
					}

				}

			case 8:
				try {
					parameterValueObj = Time.valueOf(reportParameterValue);
				} catch (IllegalArgumentException ie) {
					parameterValueObj = parseDateTime(reportParameterValue);
					if (parameterValueObj != null) {
						parameterValueObj = new Time(
								((java.util.Date) parameterValueObj).getTime());
					}

				}

			case 6:
				NumberFormatter nf4 = getNumberFormatter();
				if (nf4 == null) {
					parameterValueObj = null;
				} else {
					try {
						Number num = nf4.parse(reportParameterValue);

						if (num != null) {
							parameterValueObj = Integer.valueOf(num.intValue());
						}
					} catch (ParseException ex) {
						nf4.applyPattern("General Number");
						try {
							Number num = nf4.parse(reportParameterValue);

							if (num != null) {
								parameterValueObj = Integer.valueOf(num
										.intValue());
							}
						} catch (ParseException pex) {
							try {
								parameterValueObj = Integer
										.valueOf(reportParameterValue);
							} catch (NumberFormatException nfe) {
								parameterValueObj = null;
							}
						}
					}
				}
			}
		}

		return parameterValueObj;
	}

	protected Object parseDateTime(String reportParameterValue) {
		DateFormatter df = getDateFormatter();
		if (df == null) {
			return null;
		}

		try {
			return df.parse(reportParameterValue);
		} catch (ParseException e) {
			df = new DateFormatter("Short Date", this.uLocale);
			try {
				return df.parse(reportParameterValue);
			} catch (ParseException ex) {
				df = new DateFormatter("Medium Time", this.uLocale);
				try {
					return df.parse(reportParameterValue);
				} catch (ParseException exx) {
				}
			}
		}
		return null;
	}

}
