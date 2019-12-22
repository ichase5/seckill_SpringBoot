package com.example.seckill2.webConfig.cookie;

import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.service.impl.SeckillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这个类处理cookie(无论是请求头url还是请求体中的），然后从分布式session中取出user对象。
 * 在controller层的接口都可以直接获取user对象参数，就像获取request和response参数那样自然
 * @author 28614
 * @date 2019/12/18 14:03
 */

//Controller的拦截器
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private SeckillUserService seckillUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz== SeckillUser.class;
    }

    @Override
    //对于supportsParameter，才会进行resolveArgument
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken = request.getParameter(SeckillUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKI_NAME_TOKEN);

        //根据请求参数或请求Cookie中的token参数，获取token, 返回对应的session
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return "login";
        }
        String token = paramToken==null?cookieToken:paramToken;// param Token优先于cookie Token
        return seckillUserService.getByToken(response, token);
    }

    //取出http请求参数中的cookie token
    private String getCookieValue(HttpServletRequest request, String cookiNameToken) {
        Cookie[] cookies = request.getCookies();
        if(cookies==null || cookies.length<=0){
            return null;
        }
        //遍历cookie属性对比查找
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(cookiNameToken)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
