package com.example.seckill2.vo;

import com.example.seckill2.domain.SeckillUser;


// VO（ View Object）：表现层对象，是Web向模板渲染引擎（thymeleaf)传输的对象

/**
 * @author 28614
 * @date 2019/12/20 15:09
 * 用这个对象的信息来填充商品详情静态页面
 */
public class GoodsDetailVo {

    private int remainSeconds = 0;
    private GoodsVo goodsVo ;
    private SeckillUser seckillUser;

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public SeckillUser getSeckillUser() {
        return seckillUser;
    }

    public void setSeckillUser(SeckillUser seckillUser) {
        this.seckillUser = seckillUser;
    }
}
