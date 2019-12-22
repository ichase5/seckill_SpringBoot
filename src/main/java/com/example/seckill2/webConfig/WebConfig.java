package com.example.seckill2.webConfig;

import com.example.seckill2.webConfig.access.AccessInterceptor;
import com.example.seckill2.webConfig.cookie.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import java.util.List;

/**
 * @author 28614
 * @date 2019/12/18 13:56
 */

@Configuration//配置类
public class WebConfig extends WebMvcConfigurerAdapter {

//    //已经使用threadlocal存储了session信息，不再需要ArgumentResolver了
//    @Autowired
//    UserArgumentResolver userArgumentResolver;

//    //已经使用threadlocal存储了session信息，不再需要ArgumentResolver了
//    @Override
//    //自动处理token，从session中获取user对象，使得controller层可以直接获取user对象参数
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        argumentResolvers.add(userArgumentResolver);
//    }


    @Autowired
    AccessInterceptor accessInterceptor;

    @Override
    //限流拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}
