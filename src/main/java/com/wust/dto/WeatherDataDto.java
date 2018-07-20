package com.wust.dto;

import lombok.Data;

/**
 * 获取天气接口返回类型
 */
@Data
public class WeatherDataDto {

	String date;
	String message;
	Integer status;
	String city;
	Integer count;
	WeatherListDto data;
}
