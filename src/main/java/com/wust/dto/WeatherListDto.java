package com.wust.dto;

import lombok.Data;

import java.util.List;

/**
 * 获取预测天气列表
 */
@Data
public class WeatherListDto {

	String shidu;
	Integer pm25;
	Integer pm10;
	String quality;
	String wendu;
	String ganmao;
	WeatherDto yesterday;
	//预测天气
	List<WeatherDto> forecast;
	//给予建议

}
