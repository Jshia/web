package org.example.redis;

import org.apache.ibatis.cache.Cache;
import org.apache.log4j.Logger;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MybatisRedisCache implements Cache {
    private static Logger logger= Logger.getLogger(MybatisRedisCache.class);
    private final String id;
    private final ReadWriteLock readWriteLock=new ReentrantReadWriteLock();

    private static JedisConnectionFactory jedisConnectionFactory;

    public static void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory){
        MybatisRedisCache.jedisConnectionFactory=jedisConnectionFactory;
    }

    public MybatisRedisCache(final String id){
        if(null == id || "".equals(id)){
            throw new IllegalArgumentException("mybatis redis cache id error");
        }
        this.id = id;
        logger.debug("mybatis redis cache id:"+id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        if(null == key){
            return;
        }
        logger.debug("mybatis redis cache put key:"+key+" value:"+value);
        RedisConnection redisConnection = null;
        try {
            redisConnection=jedisConnectionFactory.getConnection();
            RedisSerializer serializer = new JdkSerializationRedisSerializer();
            redisConnection.set(serializer.serialize(key), serializer.serialize(value));
            redisConnection.lPush(serializer.serialize(id), serializer.serialize(key));
        } catch (Exception e) {
            logger.error("mybatis redis cache putObject error", e);
        } finally {
            if(null != redisConnection) {
                redisConnection.close();
            }
        }
    }

    @Override
    public Object getObject(Object key) {
        if(null == key){
            return null;
        }
        logger.debug("mybatis redis cache get key:"+key);
        RedisConnection redisConnection = null;
        Object result = null;
        try {
            redisConnection = jedisConnectionFactory.getConnection();
            RedisSerializer serializer = new JdkSerializationRedisSerializer();
            result = serializer.deserialize((redisConnection.get(serializer.serialize(key))));
        } catch (Exception e) {
           logger.error("mybatis redis cache getObject error", e);
        } finally {
            if(null != redisConnection) {
                redisConnection.close();
            }
        }
        return result;
    }

    @Override
    public Object removeObject(Object key) {
        if(null == key) {
            return null;
        }
        logger.debug("mybatis redis cache remove key:"+key);
        RedisConnection redisConnection = null;
        Object result = null;
        try {
            redisConnection = jedisConnectionFactory.getConnection();
            RedisSerializer serializer = new JdkSerializationRedisSerializer();
            result = redisConnection.expireAt(serializer.serialize(key), 0);
            redisConnection.lRem(serializer.serialize(id), 0, serializer.serialize(key));
        } catch (Exception e) {
            logger.error("mybatis redis cache removeObject error", e);
        } finally {
            if(null != redisConnection) {
                redisConnection.close();
            }
        }
        return result;
    }

    @Override
    public void clear() {
        logger.debug("mybatis redis cache clear");
        RedisConnection redisConnection = null;
        try {
            redisConnection = jedisConnectionFactory.getConnection();
            RedisSerializer serializer = new JdkSerializationRedisSerializer();
            Long length = redisConnection.lLen(serializer.serialize(id));
            if(0 == length) {
                return;
            }
            List<byte[]> keyList = redisConnection.lRange(serializer.serialize(id), 0, length-1);
            for (byte[] key: keyList) {
                redisConnection.expireAt(key, 0);
            }
            redisConnection.expireAt(serializer.serialize(id), 0);
            keyList.clear();
        } catch (Exception e) {
            logger.error("mybatis redis cache clear error", e);
        } finally {
            if(null != redisConnection){
                redisConnection.close();
            }
        }
    }

    @Override
    public int getSize() {
        int result = 0;
        try {
            RedisConnection redisConnection = jedisConnectionFactory.getConnection();
            result = Math.toIntExact(redisConnection.dbSize());
        } catch (Exception e) {
            logger.error("mybatis redis cache getSize error", e);
        }
        return result;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }
}
