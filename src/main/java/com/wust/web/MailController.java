package com.wust.web;

import com.wust.exception.ErrorInfo;
import com.wust.utils.MailUtil;
import com.wust.utils.StringUtil;
import com.wust.utils.WeatherUtil;
import com.wust.vo.MessageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mail")
public class MailController {

	private static final Logger logger = LoggerFactory.getLogger(MailController.class);

	@Autowired
	private MailUtil mailSender;

	@RequestMapping("sendemail")
	public MessageVo sendEmail() {

		String errorCode = StringUtil.getStringRandom(12);
		//打印12位随机字符串,用于日至搜索。
		logger.error("errorCode-----"+errorCode);
		MessageVo mess = new MessageVo();
		try {

			mailSender.sendSimpleMail("1175141062@qq.com", "测试邮件主题", "测试邮件内容");

			mess.setCode(ErrorInfo.OK);
			mess.setInfo("Send Success.");
			mess.setData("测试邮件发送成功。");

		} catch (Exception ex) {
			System.out.println("UnknownError -- >" + ex);
			mess.setCode(ErrorInfo.ERROR);
			mess.setInfo("UnknownError");
			mess.setData(errorCode);
		}
		return mess;
	}
}
