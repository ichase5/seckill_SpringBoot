package com.example.seckill2.controller;

import com.example.seckill2.domain.SeckillOrderInfo;
import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.redis.RedisService;
import com.example.seckill2.result.CodeMsg;
import com.example.seckill2.result.Result;
import com.example.seckill2.service.impl.SeckillGoodsService;
import com.example.seckill2.service.impl.SeckillOrderService;
import com.example.seckill2.service.impl.SeckillUserService;
import com.example.seckill2.vo.GoodsVo;
import com.example.seckill2.vo.OrderDetailVo;
import com.example.seckill2.webConfig.access.SeckillUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	SeckillUserService seckillUserService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	SeckillOrderService seckillOrderService;
	
	@Autowired
	SeckillGoodsService seckillGoodsService;
	
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(@RequestParam("orderId") long orderId) {
		SeckillUser seckillUser = SeckillUserContext.getUser();
    	if(seckillUser == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	/*
    	mysql数据库生成订单后，就会加入Redis缓存，永不失效
    	这里可以从Redis缓存快速取出订单详情
    	 */
    	SeckillOrderInfo seckillOrderInfo = seckillOrderService.getOrderByOId(orderId);
    	if(seckillOrderInfo == null){
    		return Result.error(CodeMsg.ORDER_NOT_EXIST);
    	}
    	long goodsId = seckillOrderInfo.getGoodsId();
    	GoodsVo goodsVo = seckillGoodsService.getGoodsVoById(goodsId);
    	OrderDetailVo orderDetailVo = new OrderDetailVo();
		orderDetailVo.setSeckillOrderInfo(seckillOrderInfo);
		orderDetailVo.setGoodsVo(goodsVo);

    	return Result.success(orderDetailVo);
    }
    
}
