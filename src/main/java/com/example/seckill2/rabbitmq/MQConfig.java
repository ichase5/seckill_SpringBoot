package com.example.seckill2.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 28614
 * @date 2019/12/19 20:46
 */
@Configuration
public class MQConfig {


    public static final String SECKILL_QUEUE_NAME = "seckill_queue";

    @Bean
    public Queue seckillQueue(){
        //direct模式
        return new Queue(SECKILL_QUEUE_NAME,true);
    }


}
