/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.model
 * @file Car.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-16 下午8:20:12
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.model;

/**
 * <code>Car</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-16
 */
public class Car {
	private String make;
	private String model;
	private String year;

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Car() {

	}

	public String toString() {
		return "Make:--" + this.make + " Model:--" + this.model + " Year:--"
				+ this.year;
	}

	public String getCarString() {
		return (this.toString());
	}
}
