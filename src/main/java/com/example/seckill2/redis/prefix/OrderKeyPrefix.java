package com.example.seckill2.redis.prefix;

/**
 * @author 28614
 * @date 2019/12/17 13:50
 */
public class OrderKeyPrefix extends BaseKeyPrefix {

    public OrderKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //用于判断重复下单，使用缓存判断可以为数据库减轻压力
    public static KeyPrefix getOrderByuidgid = new OrderKeyPrefix(0,"order_uid_gid");

}
