/*
 * @project jd-birt-spring
 * @package com.jd.birt.spring.example.service
 * @file CarServiceImpl.java
 * @version  1.0
 * @author  wangjunming
 * @time  2012-12-16 下午8:21:34
 * Copyright(c) 2004-2012, 360buy.com  All Rights Reserved
 */
package com.jd.birt.spring.example.service;

import java.util.Arrays;
import java.util.List;

import com.jd.birt.spring.example.api.CarService;
import com.jd.birt.spring.example.model.Car;

/**
 * <code>CarServiceImpl</code>
 * 
 * @version 1.0
 * @author wangjunming
 * @since 1.0 2012-12-16
 */
public class CarServiceImpl implements CarService {

	public List<Car> getAllCars() {
		Car car1 = new Car();
		car1.setYear("2000");
		car1.setMake("Chevrolet");
		car1.setModel("Corvette");
		Car car2 = new Car();
		car2.setYear("2005");
		car2.setMake("Dodge");
		car2.setModel("Viper");
		Car car3 = new Car();
		car3.setYear("2002");
		car3.setMake("Ford");
		car3.setModel("Mustang GT");
		List<Car> cars = Arrays.asList(car1, car2, car3);
		return cars;

	}
}
