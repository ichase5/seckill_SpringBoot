package com.example.seckill2.util;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 28614
 * @date 2019/12/17 16:26
 */

public class PhoneValidatorUtil {

    //正则表达式
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}"); // 1开头的11位数字

    public static boolean isMobile(String phone){
        if(StringUtils.isEmpty(phone)){
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(phone);
        return matcher.matches();
    }

}
