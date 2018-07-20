package com.wust.dto;

import lombok.Data;

/**
 * 单日天气情况
 */

@Data
public class WeatherDto {
	String date;
	String sunrise;
	String high;
	String low;
	String sunset;
	Integer aqi;
	String fx;
	String fl;
	String type;
	String notice;
}
