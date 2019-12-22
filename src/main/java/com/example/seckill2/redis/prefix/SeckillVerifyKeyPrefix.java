package com.example.seckill2.redis.prefix;

/**
 * @author 28614
 * @date 2019/12/20 22:16
 */
public class SeckillVerifyKeyPrefix extends BaseKeyPrefix{

    public SeckillVerifyKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //将图形验证码的正确答案进行缓存（有效期300秒）
    //每次刷新后也会重新缓存
    public static KeyPrefix getSeckillVerifyCode = new SeckillVerifyKeyPrefix(300,"seckill_verifyCode");
}
