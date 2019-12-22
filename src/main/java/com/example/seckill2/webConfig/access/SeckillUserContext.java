package com.example.seckill2.webConfig.access;

import com.example.seckill2.domain.SeckillUser;

/**使用ThredLocal存储session信息
 * @author 28614
 * @date 2019/12/21 11:06
 */
public class SeckillUserContext {

    private static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<>();

    public static void setUser(SeckillUser user) {
        userHolder.set(user);
    }

    public static SeckillUser getUser() {
        return userHolder.get();
    }

    public static void remove() {
        userHolder.remove();
    }
}
