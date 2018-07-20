package com.wust.service;

public interface ShiroService {

	/**
	 * 从缓存中获取Session信息
	 * @return
	 */
	String getSessionCache(String phone);

	/**
	 * 在缓存中存储Session信息
	 * @return
	 */
	boolean insertSessionCache(String SessionId, String phone);
}
