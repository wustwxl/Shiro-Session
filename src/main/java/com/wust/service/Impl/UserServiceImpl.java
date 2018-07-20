package com.wust.service.Impl;

import com.alibaba.fastjson.JSON;
import com.wust.entity.User;
import com.wust.mapper.UserMapper;
import com.wust.redis.RedisClient;
import com.wust.service.UserService;
import com.wust.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RedisClient redisClinet;

	/**
	 * 插入用户数据
	 *
	 * @param record
	 * @return
	 */
	@Override
	public int insert(User record) {

		return userMapper.insert(record);
	}

	/**
	 * 根据条件查询用户列表
	 *
	 * @param record
	 * @return
	 */
	@Override
	public List<User> selectByRecord(User record) {

		return userMapper.selectByRecord(record);
	}

	/**
	 * 查询所有用户列表
	 *
	 * @return
	 */
	@Override
	public List<User> selectAllUser() {

		return userMapper.selectAllUser();
	}

	/**
	 * 根据手机号查询指定用户
	 *
	 * @param phone
	 * @return
	 */
	@Override
	public User selectByPhone(String phone) {

		return userMapper.selectByPhone(phone);
	}

	/**
	 * 根据手机号删除指定用户
	 *
	 * @param phone
	 * @return
	 */
	@Override
	public int deleteByPhone(String phone) {

		return userMapper.deleteByPhone(phone);
	}

	/**
	 * 更新用户数据
	 *
	 * @param record
	 * @return
	 */
	@Override
	public int updateByRecord(User record) {

		return userMapper.updateByRecord(record);
	}

	/**
	 * 更新用户手机号
	 *
	 * @param newPhone
	 * @param oldPhone
	 * @return
	 */
	@Override
	public int updatePhone(String newPhone, String oldPhone) {

		return userMapper.updatePhone(newPhone,oldPhone);
	}

	/**
	 * 查询满足条件用户数目
	 *
	 * @param record
	 * @return
	 */
	@Override
	public Long countByRecord(User record) {

		return userMapper.countByRecord(record);
	}

	/**
	 * 根据手机号查询注册用户数量
	 *
	 * @param phone
	 * @return
	 */
	@Override
	public int countByPhone(String phone) {
		return userMapper.countByPhone(phone);
	}

	/**
	 * 根据手机号更换密码
	 *
	 * @param phone
	 * @param pwd
	 * @return
	 */
	@Override
	public int updatePwd(String phone, String pwd) {
		return userMapper.updatePwd(phone,pwd);
	}

	/**
	 * 从缓存中获取用户信息
	 *
	 * @param phone
	 * @return
	 */
	@Override
	public User getUserCache(String phone) {
		//非空
		if(phone == null || StringUtil.IsNullOrEmpty(phone)){
			return null;
		}

		String getString = redisClinet.hget("users:" + phone,"info");
		if (null == getString){
			return null;
		}

		Object object = JSON.parseObject(getString, User.class);
		return (User) object;
	}

	/**
	 * 在缓存中存储Json信息
	 * @param entity
	 * @return
	 */
	public boolean insertUserCache(User entity) {
		//非空
		if(entity == null ){
			return false;
		}

		redisClinet.hset("users:" + entity.getPhone(),"info", JSON.toJSONString(entity));
		return true;
	}

	/**
	 * 增加输入密码错误次数
	 *
	 * @param key
	 */
	@Override
	public void incrLoginCount(String key) {

		redisClinet.incr("loginCount:" + key);
		if(Integer.valueOf(getLoginCount(key))  > 3){
			expire("loginCount:" + key);
		}
	}

	/**
	 * 获取密码输错次数
	 *
	 * @param key
	 * @return
	 */
	@Override
	public String getLoginCount(String key) {
		String value = redisClinet.get("loginCount:" + key);
		return value == null ? "0":value;
	}

	/**
	 * 设置过期时间 密码错误3次以上 300秒后可再次重试
	 *
	 * @param key
	 */
	@Override
	public void expire(String key) {

		redisClinet.expire(key,300);
	}

	/**
	 * 重置密码输错次数
	 *
	 * @param key
	 */
	@Override
	public void removeCount(String key) {

		redisClinet.del("loginCount:"+key);
	}

}
