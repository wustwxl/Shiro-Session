package com.wust.web;

import com.wust.entity.User;
import com.wust.exception.ErrorInfo;
import com.wust.exception.MyException;
import com.wust.mapper.UserMapper;
import com.wust.utils.StringUtil;
import com.wust.vo.MessageVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController{

	@Resource
	private UserMapper userService;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	/**
	 * 用户添加
	 * @return
	 */
	@RequestMapping(value = "/addInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public MessageVo addUser(@RequestBody User users){


		MessageVo mess = new MessageVo();
		String errorCode = StringUtil.getStringRandom(12);
		//打印12位随机字符串,用于日至搜索。
		logger.error("errorCode-----" + errorCode);

		try {

			/**
			 * @Description: 注册手机号应该唯一, 注册前先判断手机号是否可用
			 */
			String phone = users.getPhone();
			int telNum = countByPhone(phone);

			if (telNum <= -1) {
				mess.setCode(ErrorInfo.ERROR);
				mess.setInfo("Register Failed.");
				mess.setData("手机号格式错误,添加用户帐号失败!");
				return  mess;

			} else if (telNum >= 1){
				mess.setCode(ErrorInfo.ERROR);
				mess.setInfo("Select Failed.");
				mess.setData("该手机号已被注册,添加用户帐号失败!");
				return  mess;
			}
			userService.insert(users);
			mess.setCode(ErrorInfo.OK);
			mess.setInfo("Register Success.");
			mess.setData(userService.selectByPhone(phone));
		} catch (Exception e) {
			logger.error(e.getMessage());

			new MyException(errorCode);
		}
		return mess;
	}

	/**
	 * 用户列表查询
	 * @return
	 */
	@RequiresRoles("admin")//角色管理;
	@RequiresPermissions("list")//权限管理;
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public MessageVo userInfo(){


		MessageVo mess = new MessageVo();
		String errorCode = StringUtil.getStringRandom(12);
		//打印12位随机字符串,用于日至搜索。
		logger.error("errorCode-----" + errorCode);

		try {
			List<User> list = userService.selectAllUser();
			mess.setCode(ErrorInfo.OK);
			mess.setInfo("Select Success.");
			mess.setData(list);

		} catch (Exception e) {
			logger.error(e.getMessage());

			new MyException(errorCode);
		}
		return mess;
	}

	/**
	 * 显示指定用户信息
	 * @param tel
	 * @return
	 */
	@RequestMapping(value = "/info/{tel}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public MessageVo getUserAccount(@PathVariable("tel") String tel) {

		MessageVo mess = new MessageVo();
		String errorCode = StringUtil.getStringRandom(12);
		//打印12位随机字符串,用于日至搜索。
		logger.error("errorCode-----" + errorCode);

		try {
			int telNum = countByPhone(tel);
			if (telNum <= -1) {
				mess.setCode(ErrorInfo.ERROR);
				mess.setInfo("Register Failed.");
				mess.setData("手机号格式错误,添加用户帐号失败!");
				return  mess;

			} else if (telNum == 0){
				mess.setCode(ErrorInfo.ERROR);
				mess.setInfo("Select Failed.");
				mess.setData("该手机号未注册,查询失败!");
				return  mess;
			}

			User user = userService.selectByPhone(tel);
			mess.setCode(ErrorInfo.OK);
			mess.setInfo("Select Success.");
			mess.setData(user);
		} catch (Exception e) {
			logger.error(e.getMessage());

			new MyException(errorCode);
		}

		return mess;
	}

	/**
	 * 根据手机号更新指定用户帐号信息
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
	public MessageVo putUserAccount(@RequestBody User user) {

		MessageVo mess = new MessageVo();
		String errorCode = StringUtil.getStringRandom(12);
		//打印12位随机字符串,用于日至搜索。
		logger.error("errorCode-----" + errorCode);

		try {
			int telNum = countByPhone(user.getPhone());
			if (telNum <= -1) {
				mess.setCode(ErrorInfo.ERROR);
				mess.setInfo("Register Failed.");
				mess.setData("手机号格式错误,添加用户帐号失败!");
				return  mess;

			} else if (telNum == 0){
				mess.setCode(ErrorInfo.ERROR);
				mess.setInfo("Select Failed.");
				mess.setData("该手机号未注册,更新失败!");
				return  mess;
			}

			userService.updateByRecord(user);
			mess.setCode(ErrorInfo.OK);
			mess.setInfo("Update Success.");
			mess.setData(userService.selectByPhone(user.getPhone()));

		} catch (Exception e) {
			logger.error(e.getMessage());

			new MyException(errorCode);
		}

		return mess;
	}

	/**
	 * 更换手机号
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/tel", method = RequestMethod.POST)
	public MessageVo putUserAccountTel(@RequestBody Map<String, String> params) {

		MessageVo mess = new MessageVo();
		String errorCode = StringUtil.getStringRandom(12);
		//打印12位随机字符串,用于日至搜索。
		logger.error("errorCode-----" + errorCode);

		/**
		 * @Description: 判断手机号
		 */
		String newTel = params.get("newTel");
		String oldTel = params.get("oldTel");

		int newTelNum = countByPhone(newTel);
		int oldTelNum = countByPhone(oldTel);

		logger.info("newTel:" + newTel + "=" + newTelNum);
		logger.info("oldTel:" + oldTel + "=" + oldTelNum);

		if (newTelNum <= -1 || oldTelNum <= -1) {
			mess.setCode(ErrorInfo.ERROR);
			mess.setInfo("Update Failed.");
			mess.setData("输入手机号格式错误,更新失败!");
			return  mess;
		} else if (oldTelNum == 0) {
			mess.setCode(ErrorInfo.ERROR);
			mess.setInfo("Update Failed.");
			mess.setData("旧手机号未注册,更新失败!");
		} else if (newTelNum >= 1) {
			mess.setCode(ErrorInfo.ERROR);
			mess.setInfo("Update Failed.");
			mess.setData("新手机号已被注册,更新失败!");
			return mess;
		}

		try {
			userService.updatePhone(newTel,oldTel);
			mess.setCode(ErrorInfo.OK);
			mess.setInfo("Update Success.");
			mess.setData(userService.selectByPhone(newTel  ));
		} catch (Exception e) {
			logger.error(e.getMessage());

			new MyException(errorCode);
		}

		return mess;
	}

	/**
	 * 用户删除
	 * @return
	 */
	@RequiresRoles("admin")//角色管理;
	@RequestMapping(value = "/del/{tel}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public MessageVo userDel(@PathVariable("tel") String tel){

		MessageVo mess = new MessageVo();
		String errorCode = StringUtil.getStringRandom(12);
		//打印12位随机字符串,用于日至搜索。
		logger.error("errorCode-----" + errorCode);

		try {

			int telNum = countByPhone(tel);

			if (telNum <= -1) {
				mess.setCode(ErrorInfo.ERROR);
				mess.setInfo("Register Failed.");
				mess.setData("手机号格式错误,添加用户帐号失败!");
				return  mess;

			} else if (telNum == 0){
				mess.setCode(ErrorInfo.ERROR);
				mess.setInfo("Select Failed.");
				mess.setData("该手机号未注册,删除失败!");
				return  mess;
			}

			userService.deleteByPhone(tel);
			mess.setCode(ErrorInfo.OK);
			mess.setInfo("Delete Success.");

		} catch (Exception e) {
			logger.error(e.getMessage());

			new MyException(errorCode);
		}
		return mess;
	}

	/**
	 * 获取注册用户帐号总量
	 * @return
	 */
	@RequiresRoles("admin")//角色管理;
	@RequestMapping(value = "/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public MessageVo getUserAccountNum() {

		MessageVo mess = new MessageVo();
		String errorCode = StringUtil.getStringRandom(12);
		//打印12位随机字符串,用于日至搜索。
		logger.error("errorCode-----"+errorCode);

		try {
			User user = new User();
			long sum = userService.countByRecord(user);
			logger.info("------用户总量："+ sum);
			mess.setCode(ErrorInfo.OK);
			mess.setInfo("Select Success");
			mess.setData(sum);

		} catch (Exception e) {
			logger.error(e.getMessage());

			new MyException(errorCode);
		}

		return mess;
	}

	/**
	 * 查询指定手机号注册用户数量
	 * @param tel
	 * @return
	 */
	private int countByPhone(String tel){

		/**
		 * @Description: 判断手机号格式是否正确
		 */
		Boolean bool = StringUtil.isHandset(tel);

		if (!bool){
			//手机号格式错误
			return -1;
		}

		try {
			int telNum = userService.countByPhone(tel);
			logger.info("------此手机号注册数：" + telNum);
			return telNum;

		} catch (Exception e) {
			logger.error(e.getMessage());

			new MyException("CountByPhone Error");
		}
		return -2;
	}
}