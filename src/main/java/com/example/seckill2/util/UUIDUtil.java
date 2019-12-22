package com.example.seckill2.util;

import java.util.UUID;

/**
 * @author 28614
 * @date 2019/12/18 9:49
 */
public class UUIDUtil {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");//去掉java提供的uuid中的'-'
    }
}
