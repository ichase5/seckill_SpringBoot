package com.example.seckill2.vo;


import com.example.seckill2.domain.SeckillOrderInfo;

/*
包含秒杀订单详情和商品详情
 */
public class OrderDetailVo {

	private GoodsVo goodsVo;
	private SeckillOrderInfo seckillOrderInfo;

	public GoodsVo getGoodsVo() {
		return goodsVo;
	}

	public void setGoodsVo(GoodsVo goodsVo) {
		this.goodsVo = goodsVo;
	}

	public SeckillOrderInfo getSeckillOrderInfo() {
		return seckillOrderInfo;
	}

	public void setSeckillOrderInfo(SeckillOrderInfo seckillOrderInfo) {
		this.seckillOrderInfo = seckillOrderInfo;
	}
}
