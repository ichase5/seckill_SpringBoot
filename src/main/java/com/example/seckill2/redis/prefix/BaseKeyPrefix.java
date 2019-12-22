package com.example.seckill2.redis.prefix;

/**
 * 使用不同的key prefix,避免不同模块在Redis中的数据的key的冲突和覆盖
 * @author 28614
 * @date 2019/12/17 13:43
 */
public abstract class BaseKeyPrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    /*
    抽象类的构造函数，被子类继承
     */

    //不会失效的key
    public BaseKeyPrefix(String prefix){
        this(0,prefix);
    }

    //带有效期的key
    public BaseKeyPrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;//默认为0，代表永不过期
    }

    @Override
    /**
     * 不同类型数据使用不同的key，避免键的重复
     */
    public String getPrerix() {
        String className = getClass().getSimpleName();
        return className+":"+prefix;
    }
}
