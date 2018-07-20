package com.wust.timing;

import com.wust.utils.MailUtil;
import com.wust.utils.WeatherUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时执行
 */

@Component
public class ScheduledTasks {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private MailUtil mailSender;

	//@Scheduled(fixedRate = 5000) //上一次开始执行时间点之后5秒再执行
    //@Scheduled(fixedDelay = 5000) //上一次执行完毕时间点之后5秒再执行
    //@Scheduled(initialDelay=1000, fixedRate=5000) //第一次延迟1秒后执行，之后按fixedRate的规则每5秒执行一次
    @Scheduled(cron="0 15 5 ? * *") //通过cron表达式定义规则：每天5：15执行
	//@Scheduled(fixedRate = 50000) //每5秒执行一次
	public void reportCurrentTime() {

		System.out.println("现在时间：" + dateFormat.format(new Date()));
		//获取天气
		String content = WeatherUtil.getWeekWeatherMap();
	    mailSender.sendSimpleMail("857684025@qq.com", "发给小可爱的每日邮件", content);
	    mailSender.sendSimpleMail("1175141062@qq.com", "每日邮件", content);
	}

}
