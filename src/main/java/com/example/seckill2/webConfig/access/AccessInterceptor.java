package com.example.seckill2.webConfig.access;

import com.alibaba.fastjson.JSON;
import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.redis.RedisService;
import com.example.seckill2.redis.prefix.AccessKeyPrefix;
import com.example.seckill2.result.CodeMsg;
import com.example.seckill2.result.Result;
import com.example.seckill2.service.impl.SeckillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 限流拦截器
 * @author 28614
 * @date 2019/12/21 10:50
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;


    @Override
    //服务器端接口完成服务返回后，注销thred local中的session
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        SeckillUserContext.remove();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //请求到达服务器端的服务接口时，使用thread local保存session信息
        SeckillUser user = getUser(request, response);
        SeckillUserContext.setUser(user);//thread local保存session

        if(handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod)handler;
            //获取注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null) {//没有注解
                return true;
            }
            //seconds秒内不能访问超过maxCount次
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin) {
                if(SeckillUserContext.getUser() == null) {
                    //告知客户，需要登录再操作
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key = ":" + SeckillUserContext.getUser().getId() + ":" + key;//real_key = "prefix:user:url"
            }
            AccessKeyPrefix accessKeyPrefix = AccessKeyPrefix.withExpire(seconds);//设置prefix的有效期
            Integer count = redisService.get(accessKeyPrefix, key, Integer.class);
            if(count == null) {
                redisService.set(accessKeyPrefix, key,1);
            }
            else if(count < maxCount){
                redisService.incr(accessKeyPrefix, key);//incr不改变有效期
            }
            else{
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }

    //response
    private void render(HttpServletResponse response, CodeMsg codeMsg)throws Exception {
        //指定输出的编码格式，避免乱码
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(Result.error(codeMsg));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    //从cookie中获取user
    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(SeckillUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return seckillUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
