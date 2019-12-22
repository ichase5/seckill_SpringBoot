package com.example.seckill2.rabbitmq;

import com.example.seckill2.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 28614
 * @date 2019/12/19 20:45
 */
@Service
public class MQSender {

    private static final Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RedisService redisService;


    public void sendSeckillMessage(SeckillMessage seckillMessage) {
        //rabbitmq中只能发送字符串，SeckillMessage对象需要先序列化
        String msg = redisService.bean2Str(seckillMessage);//序列化
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE_NAME, msg);
        logger.info("sent message: ",msg);
    }
}
