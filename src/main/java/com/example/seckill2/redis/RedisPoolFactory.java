package com.example.seckill2.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**Redis连接池
 * @author 28614
 * @date 2019/12/17 11:28
 */
@Repository
public class RedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    /*
    类似数据库连接池
     */
    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
        JedisPool jedisPool = new JedisPool(poolConfig,redisConfig.getHost(),redisConfig.getPort(),redisConfig.getTimeout()*100);
        return jedisPool;
    }

}
