package com.example.seckill2.redis.prefix;

/**
 * @author 28614
 * @date 2019/12/20 21:28
 */

public class SeckillAddressKeyPrefix extends BaseKeyPrefix {

    public SeckillAddressKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //秒杀地址缓存（有效期60秒）
    public static KeyPrefix getSeckillAddress = new SeckillAddressKeyPrefix(60,"seckillAddress");

}
