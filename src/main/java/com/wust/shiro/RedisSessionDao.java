package com.wust.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Component
public class RedisSessionDao extends EnterpriseCacheSessionDAO {

	private static final String prefix = "SHIRO_SESSION_ID:";

	// session 在redis过期时间是30分钟30*60
	private static int expireTime = 1800;

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisSessionDao.class);

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 重写CachingSessionDAO中readSession方法，如果Session中没有登陆信息就调用doReadSession方法从Redis中重读
	 */
	@Override
	public Session readSession(Serializable sessionId) throws UnknownSessionException {
		Session session = getCachedSession(sessionId);
		if (session == null
				|| session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
			session = this.doReadSession(sessionId);
			if (session == null) {
				throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
			} else {
				// 缓存
				cache(session, session.getId());
			}
		}
		return session;
	}


	// 创建session，保存到数据库
	@Override
	protected Serializable doCreate(Session session) {
		LOGGER.info("--------doCreate-----");
		Serializable sessionId = super.doCreate(session);
		redisTemplate.opsForValue().set(prefix + sessionId.toString(),session);
		return sessionId;
	}

	// 获取session
	@Override
	protected Session doReadSession(Serializable sessionId) {
		LOGGER.info("--------doRead-----");

		// 先从缓存中获取session，如果没有再去数据库中获取
		Session session = super.doReadSession(sessionId);
		if (session == null) {
			session = (Session) redisTemplate.opsForValue().get(prefix + sessionId.toString());
		}
		return session;
	}

	// 更新session的最后一次访问时间
	@Override
	protected void doUpdate(Session session) {
		LOGGER.info("--------doUpdate-----");

		super.doUpdate(session);
		String key = prefix + session.getId().toString();
		if (!redisTemplate.hasKey(key)) {
			redisTemplate.opsForValue().set(key, session);
		}
		redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
	}


	// 删除session
	@Override
	protected void doDelete(Session session) {
		LOGGER.info("--------doDelete-----");
		super.doDelete(session);
		redisTemplate.delete(prefix + session.getId().toString());
	}

}