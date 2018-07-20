package com.wust.service.Impl;

import com.wust.redis.RedisClient;
import com.wust.service.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroServiceImpl implements ShiroService{

	@Autowired
	private RedisClient redisClinet;

	/**
	 * 从缓存中获取Session信息
	 *
	 * @return
	 */
	@Override
	public String getSessionCache(String phone) {
		return redisClinet.hget("users:" + phone,"sessions");
	}

	/**
	 * 在缓存中存储Session信息
	 * @return
	 */
	@Override
	public boolean insertSessionCache(String sessionId, String phone) {
		//非空
		if(sessionId == null ){
			return false;
		}

		redisClinet.hset("users:" + phone,"sessions", sessionId);
		return true;
	}
}
