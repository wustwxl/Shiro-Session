package com.wust.utils;

import com.wust.dto.WeatherDataDto;
import com.wust.dto.WeatherDto;
import com.wust.dto.WeatherListDto;

import java.io.*;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * java调用天气预报接口
 * 详情网站 https://www.sojson.com/api/
 */
public class WeatherUtil {

	private static final Logger logger = Logger.getLogger(WeatherUtil.class);

	/**
	 * 获取一周天气
	 * 方 法 名：getWeekWeatherMap
	 * City 城市
	 */
	public static String getWeekWeatherMap() {

		String city = null;
		String url = null;
		//参数url化
		try {
			city = java.net.URLEncoder.encode("烟台", "utf-8");
			// 连接API
			url = "https://www.sojson.com/open/api/weather/json.shtml?city=" + city;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String datas = HttpClientUtil.getHttp(url);
		System.out.println(datas);
		WeatherDataDto weatherDataDto = (WeatherDataDto) JsonUtil.getBeanFromJson(datas, WeatherDataDto.class);
		System.out.println(weatherDataDto);

		WeatherListDto weatherListDto = weatherDataDto.getData();
		List<WeatherDto> forecastList = weatherListDto.getForecast();

		WeatherDto today = forecastList.get(0);
		List<WeatherDto> future = forecastList.subList(1, forecastList.size());

		logger.info("输出天气情况");
		logger.info("城市：" + weatherDataDto.getCity());
		logger.info("昨日天气：" + weatherListDto.getYesterday());
		logger.info("今日天气：" + today);
		logger.info("预测天气：" + future);

		StringBuffer content = new StringBuffer("天气预测").append("\n");
		content = content.append("日期：").append(today.getDate()).append("\n")
				.append("温度：").append(today.getLow()).append(" - ").append(today.getHigh()).append("\n")
				.append("风速：").append(today.getFx()).append(" ").append(today.getFl()).append("\n")
				.append("天气：").append(today.getType()).append("\n")
				.append("     ").append(today.getNotice()).append("\n")
				.append("´͈ ᵕ `͈").append("小可爱注意天气变化哟~~~").append("\n");

		return content.toString();
	}

}