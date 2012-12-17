/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.api
 * @file CarService.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-16 下午8:24:40
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.api;

import java.util.List;

import com.jd.birt.spring.example.model.Car;

/**
 * <code>CarService</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-16
 */
public interface CarService {
	public List<Car> getAllCars();
}
