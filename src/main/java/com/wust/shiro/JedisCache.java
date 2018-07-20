package com.wust.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public class JedisCache<K,V> implements Cache<K, V> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JedisCache.class);

	private static final String REDIS_SHIRO_CACHE = "REDIS_SHIRO_CACHE:";

	private long globExpire = 30;

	private String cacheKey;

	private RedisTemplate<K, V> redisTemplate;

	@SuppressWarnings("rawtypes")
	public JedisCache(String name, RedisTemplate client) {
		this.cacheKey = REDIS_SHIRO_CACHE + name + ":";
		this.redisTemplate = client;
	}

	@Override
	public V get(K key) throws CacheException {
		redisTemplate.boundValueOps(getCacheKey(key)).expire(globExpire, TimeUnit.MINUTES);
		return redisTemplate.boundValueOps(getCacheKey(key)).get();
	}

	@Override
	public V put(K key, V value) throws CacheException {
		V old = get(key);
		redisTemplate.boundValueOps(getCacheKey(key)).set(value);
		return old;
	}

	@Override
	public V remove(K key) throws CacheException {
		V old = get(key);
		redisTemplate.delete(getCacheKey(key));
		return old;
	}

	@Override
	public void clear() throws CacheException {
		redisTemplate.delete(keys());
	}

	@Override
	public int size() {
		return keys().size();
	}

	@Override
	public Set<K> keys() {
		return redisTemplate.keys(getCacheKey("*"));
	}

	@Override
	public Collection<V> values() {
		Set<K> set = keys();
		List<V> list = new ArrayList<>();
		for (K s : set) {
			list.add(get(s));
		}
		return list;
	}

	private K getCacheKey(Object k) {
		return (K) (this.cacheKey + k);
	}
}