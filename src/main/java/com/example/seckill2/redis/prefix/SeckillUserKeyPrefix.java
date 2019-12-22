package com.example.seckill2.redis.prefix;

/**
 * @author 28614
 * @date 2019/12/18 10:10
 */
public class SeckillUserKeyPrefix  extends BaseKeyPrefix{

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;//2天

    private SeckillUserKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    private SeckillUserKeyPrefix(String prefix) {
        super(prefix);
    }

    //分布式session,有效期两天。每次浏览器发送token都会重新开始两天的计时
    public static SeckillUserKeyPrefix token = new SeckillUserKeyPrefix(TOKEN_EXPIRE, "tk");

    //通过缓存来判断这个用户是否是注册用户（永久有效）
    public static SeckillUserKeyPrefix getById = new SeckillUserKeyPrefix("id");
}
