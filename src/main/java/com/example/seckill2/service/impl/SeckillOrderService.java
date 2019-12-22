package com.example.seckill2.service.impl;

import com.example.seckill2.dao.SeckillOrderDao;
import com.example.seckill2.domain.SecKillOrder;
import com.example.seckill2.domain.SeckillOrderInfo;
import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.redis.RedisService;
import com.example.seckill2.redis.prefix.OrderKeyPrefix;
import com.example.seckill2.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author 28614
 * @date 2019/12/18 16:55
 */
@Service
public class SeckillOrderService {

    @Resource
    SeckillOrderDao seckillOrderDao;

    @Autowired
    RedisService redisService;


    //此方法可用于判断重复秒杀，或者用来判断是否已经秒杀成功
    public SecKillOrder getOrderBy_UserId_goodId(Long uid, Long goodsId) {
        //生成订单的时候，就写入Redis缓存。设置永不失效
        //这里要注意，你必须保证服务启动时，Redis里就包含所有已下单信息。Redis持久化非常重要
        return  redisService.get(OrderKeyPrefix.getOrderByuidgid ,":"+uid+":"+goodsId, SecKillOrder.class);
    }


    //数据库层面的订单创建
    @Transactional//订单创建必须是事务性的
    public SeckillOrderInfo createOrder(SeckillUser seckillUser, GoodsVo goodsVo) {

        /*
        先生成订单详情，再生成订单。
        是因为订单详情表的主键，即订单id时自增长的
         */

        //插入seckill_order_info表中
        SeckillOrderInfo orderInfo = new SeckillOrderInfo();//秒杀订单详情
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getSeckillPrice());//秒杀价格
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(seckillUser.getId());
        seckillOrderDao.insertSeckillOrderInfo(orderInfo);//dao必须加上@SelectKey主键

        //插入order info数据库后，orderInfo对象中就有了id属性（mybatis自动完成）

        //插入seckill_order表  seckill_order表中有唯一的联合索引。 一旦出现重复秒杀，就会自动回滚
        SecKillOrder secKillOrder = new SecKillOrder();
        secKillOrder.setGoodsId(goodsVo.getId());
        secKillOrder.setUserId(seckillUser.getId());
        secKillOrder.setOrderId(orderInfo.getId());//订单编号
        seckillOrderDao.insertSeckillOrder(secKillOrder);//订单录入数据库

        //将生成的订单加入Redis缓存
        redisService.set(OrderKeyPrefix.getOrderByuidgid ,":"+seckillUser.getId()+":"+goodsVo.getId(), secKillOrder);

        return orderInfo;
    }

    //获取订单详情（秒杀成功后需要查看订单详情）
    public SeckillOrderInfo getOrderByOId(long orderId) {
        return seckillOrderDao.getOrderByOId(orderId);
    }

}
