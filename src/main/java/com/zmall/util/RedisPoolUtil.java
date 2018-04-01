package com.zmall.util;

import com.zmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Created by ztian
 */
@Slf4j
public class RedisPoolUtil {


    /**
     * 设置key的有效期，单位是秒
     *
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key, int exTime) {
        Jedis jedis = RedisPool.getJedis();
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * @param key
     * @param value
     * @param exTime key有效时间，单位：秒
     * @return
     */
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = RedisPool.getJedis();
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 存储key,value
     *
     * @param key
     * @param value
     * @return
     */
    public static String set(String key, String value) {
        Jedis jedis = RedisPool.getJedis();
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        Jedis jedis = RedisPool.getJedis();
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 根据key删除数据
     *
     * @param key
     * @return
     */
    public static Long del(String key) {
        Jedis jedis = RedisPool.getJedis();
        Long result = null;
        try {
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * key不存在，设置key、value
     *
     * @param key
     * @return 设置key、value,若key不存在设置成功返回1，否则返回0
     */
    public static Long setnx(String key, String value) {
        Jedis jedis = RedisPool.getJedis();
        Long result = null;
        try {
            result = jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("setnx error key:{} value:{}", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 设置新的key、value，返回旧的value
     * @param key
     * @return 旧的value值，若key不存在返回null
     */
    public static String getset(String key, String value) {
        Jedis jedis = RedisPool.getJedis();
        ;
        String result = null;
        try {
            result = jedis.getSet(key, value);
        } catch (Exception e) {
            log.error("getset error  key:{}  value:{}", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        RedisPoolUtil.set("key1", "value1");
        RedisPoolUtil.setEx("key2", "value2", 2 * 60);
        RedisPoolUtil.expire("key1", 5 * 60);
        RedisPoolUtil.del("key1");
    }


}
