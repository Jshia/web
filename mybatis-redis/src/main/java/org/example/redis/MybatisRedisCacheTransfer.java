package org.example.redis;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

public class MybatisRedisCacheTransfer {
    public void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory){
        MybatisRedisCache.setJedisConnectionFactory(jedisConnectionFactory);
    }
}

