package com.example.seckill2.redis;

import com.alibaba.fastjson.JSON;
import com.example.seckill2.redis.prefix.KeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**Redis业务层实现
 * @author 28614
 * @date 2019/12/17 10:41
 */
@Component
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String real_key = prefix.getPrerix() + key;
            String value = jedis.get(real_key);
            return str2Bean(value,clazz);//反序列化
        }
        finally {//释放资源
            jedis.close();
        }
    }

    public <T> boolean set(KeyPrefix prefix,String key,T data){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = bean2Str(data);//序列化
            if(value==null || value.length()<=0){
                return false;
            }
            String real_key = prefix.getPrerix() + key;
            int expires = prefix.expireSeconds();
            if(expires<=0){//永不过期
                jedis.set(real_key,value);
            }
            else{//有过期时间
                jedis.setex(real_key,expires,value);
            }
            return true;
        }
        finally {//释放资源
            if(jedis!=null) jedis.close();
        }
    }

    public boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String real_key = prefix.getPrerix() + key;
            return jedis.exists(real_key);
        }
        finally {//释放资源
            if(jedis!=null) jedis.close();
        }
    }

    public Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String real_key = prefix.getPrerix() + key;
            return jedis.incr(real_key);//原子性操作
        }
        finally {//释放资源
            if(jedis!=null) jedis.close();
        }
    }

    public Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String real_key = prefix.getPrerix() + key;
            return jedis.decr(real_key);//原子性操作
        }
        finally{//释放资源
            if(jedis!=null) jedis.close();
        }
    }

    public boolean delete(KeyPrefix prefix,String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix + key;
            long delCount = jedis.del(realKey);//原子性操作
            return delCount>0;
        }
        finally{//释放资源
            if(jedis!=null) jedis.close();
        }
    }


    /*
    这里我们使用的是Redis的五种数据结构中，最简单的一种，即String类型。
    对象必须序列化为String,才能存入Redis缓存中。
    这里我们没有采用java原生的序列化和反序列化工具，是因为原生的序列号太慢，且占用空间大。
    这里目前采用的是阿里巴巴开发的fast-json工具。
     */

    //序列化
    public <T> String bean2Str(T data) { //在rabbitmq部分也会使用这个方法,因此访问修饰符为public，而不是private
        if(data==null) return null;
        Class<?> clazz = data.getClass();
        if(clazz==int.class || clazz==Integer.class){
            return String.valueOf(data);
        }
        else if (clazz==long.class || clazz==Long.class){
            return String.valueOf(data);
        }
        else if(clazz==String.class){
            return (String)data;
        }
        else{
            return JSON.toJSONString(data);
        }
    }

    //反序列化
    public <T> T str2Bean(String value,Class<T> clazz) { //在rabbitmq部分也会使用这个方法，因此访问修饰符为public，而不是private
        if(value==null || value.length()<=0 || clazz==null) return null;

        if(clazz==int.class || clazz==Integer.class){
            return (T) Integer.valueOf(value);
        }
        else if (clazz==long.class || clazz==Long.class){
            return (T) Long.valueOf(value);
        }
        else if(clazz==String.class){
            return (T)value;
        }
        else{
            return JSON.toJavaObject(JSON.parseObject(value),clazz);
        }
    }

}
