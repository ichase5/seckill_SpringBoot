package com.example.seckill2.rabbitmq;

import com.example.seckill2.domain.SeckillUser;

/**
 * @author 28614
 * @date 2019/12/20 14:15
 */
public class SeckillMessage {

    private SeckillUser seckillUser;
    private long goodsId;

    public SeckillUser getSeckillUser() {
        return seckillUser;
    }

    public void setSeckillUser(SeckillUser seckillUser) {
        this.seckillUser = seckillUser;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
