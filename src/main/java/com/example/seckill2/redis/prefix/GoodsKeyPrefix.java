package com.example.seckill2.redis.prefix;

/**
 * @author 28614
 * @date 2019/12/17 13:50
 */
public class GoodsKeyPrefix extends BaseKeyPrefix {

    public GoodsKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    public GoodsKeyPrefix(String prefix) {
        super(prefix);
    }

    //商品列表html页面的缓存(有效期60秒)
    public static GoodsKeyPrefix goodsListPrefix = new GoodsKeyPrefix(60,"goodsList");

    //商品库存（Redis预减库存，商品库存从系统启动就必须一直存在于Redis中，永久有效）
    public static KeyPrefix goodsStock = new GoodsKeyPrefix("goodsStock");

}
