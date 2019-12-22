package com.example.seckill2.redis.prefix;

/*
    这里使用的是一个设计模式：模板模式
    即按照接口，抽象类，实现类顺序进行设计
 */
public interface KeyPrefix {

    //缓存有效期
    public int expireSeconds();

    //前缀
    public String getPrerix();

}
