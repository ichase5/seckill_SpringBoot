package com.example.seckill2.redis.prefix;

/**
 * @author 28614
 * @date 2019/12/20 19:34
 */
public class SeckillResultKeyPrefix extends BaseKeyPrefix {

    public SeckillResultKeyPrefix(String prefix) {
        super(prefix);
    }

    //秒杀失败
    //Redis+rabbitmq，异步下单，用户提交秒杀请求后，一直在等待秒杀结果。
    //如果成功，返回订单详情。
    //如果失败，将秒杀失败信息放入Redis缓存中，客户端从Redis中得知秒杀失败。
    //永久有效（其实有效期一段时间也就够了）
    public static KeyPrefix seckill_fail = new SeckillResultKeyPrefix("seckill_fail");
}
