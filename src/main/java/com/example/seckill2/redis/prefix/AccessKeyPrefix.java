package com.example.seckill2.redis.prefix;

/**
 * @author 28614
 * @date 2019/12/21 11:04
 */
public class AccessKeyPrefix extends BaseKeyPrefix {

    public AccessKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //记录某个用户对该url的访问次数(用来限制恶意用户的频繁访问）
    public static AccessKeyPrefix withExpire(int seconds) {
        return new AccessKeyPrefix(seconds, "AccessURL");
    }
}
