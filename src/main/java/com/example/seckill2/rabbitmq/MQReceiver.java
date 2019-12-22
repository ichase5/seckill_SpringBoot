package com.example.seckill2.rabbitmq;

import com.example.seckill2.domain.SecKillOrder;
import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.redis.RedisService;
import com.example.seckill2.result.CodeMsg;
import com.example.seckill2.service.impl.SeckillExecuteService;
import com.example.seckill2.service.impl.SeckillGoodsService;
import com.example.seckill2.service.impl.SeckillOrderService;
import com.example.seckill2.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 28614
 * @date 2019/12/19 20:46
 */
@Service
public class MQReceiver {

    private static final Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RedisService redisService;

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    SeckillOrderService seckillOrderService;

    @Autowired
    SeckillExecuteService seckillExecuteService;


    @RabbitListener(queues = MQConfig.SECKILL_QUEUE_NAME)
    public void receiveDirect(String msg){
        logger.info("received direct message: ",msg);
        //反序列化得到SeckillMessage
        SeckillMessage seckillMessage = redisService.str2Bean(msg,SeckillMessage.class);
        SeckillUser seckillUser = seckillMessage.getSeckillUser();
        long goodsId = seckillMessage.getGoodsId();

        //查询商品库存
        GoodsVo goodsVo = seckillGoodsService.getGoodsVoById(goodsId);
        int stockCount = goodsVo.getStockCount();
        if(stockCount<=0){ //注意这里是典型的check and act,多线程并发时仍会出现超卖现象
            return;
        }

        //判断重复秒杀(减轻数据库压力）
        SecKillOrder secKillOrder = seckillOrderService.getOrderBy_UserId_goodId(seckillUser.getId(),goodsId);
        if(secKillOrder!=null){//重复秒杀
            return;
        }

        //在数据库层面真正的执行秒杀
        seckillExecuteService.doSeckill(seckillUser,goodsVo);


    }

}
