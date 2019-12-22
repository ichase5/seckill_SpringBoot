package com.example.seckill2.service.impl;

import com.example.seckill2.dao.SeckillUserDao;
import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.exception.GlobalException;
import com.example.seckill2.redis.RedisService;
import com.example.seckill2.redis.prefix.SeckillUserKeyPrefix;
import com.example.seckill2.result.CodeMsg;
import com.example.seckill2.util.MD5Util;
import com.example.seckill2.util.UUIDUtil;
import com.example.seckill2.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 28614
 * @date 2019/12/17 16:43
 */
@Service
public class SeckillUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Resource
    private SeckillUserDao seckillUserDao;

    @Autowired
    private RedisService redisService;

    /*
    这个函数是用来判断数据库中有没有这个用户，即验证这个用户是否是注册用户
    通过缓存实现
     */
    public SeckillUser getById(long id){
        //尝试取缓存
        SeckillUser seckillUser = redisService.get(SeckillUserKeyPrefix.getById,":"+id,SeckillUser.class);
        if(seckillUser!=null){//缓存命中
            return seckillUser;
        }
        //缓存未命中，查询数据库
        seckillUser =  seckillUserDao.getById(id);
        //加入缓存
        redisService.set(SeckillUserKeyPrefix.getById,":"+id,seckillUser);

        return seckillUser;
    }


    //登录后，服务端产生session,返回给客户端（含有cookie）
    //之后，浏览器发送到请求中就会带有cookie（token)了
    //同时，redis中建立了token与seckill_user的键值对，有效期也是两天
    public String login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String phone = loginVo.getMobile();
        String password = loginVo.getPassword();//客户端固定MD5加密后的密码
        //判断手机号是否在服务器中存在:
        SeckillUser seckillUser = getById(Long.valueOf(phone));
        if(seckillUser==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码是否正确
        String serverCode = seckillUser.getPassword();//服务器端的两次md5后的密码
        String salt = seckillUser.getSalt();//服务器端的随机salt

        if(!MD5Util.serverEncoding(password,salt).equals(serverCode)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);//密码错误
        }

        //每次登录都重新生成一个生成Cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,seckillUser);//将cookie加入redis中
        return token;
    }

    //分布式Session是通过Redis实现的,利用缓存统一管理session
    //每次获取时，重新计时token的有效期
    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        SeckillUser seckillUser =  redisService.get(SeckillUserKeyPrefix.token,token,SeckillUser.class);
        //每次客户端发送了token，就延长token有效期
        if(seckillUser != null) {
            addCookie(response, token, seckillUser);//session与cookie有效期同步延长
        }
        return seckillUser;
    }


    private void addCookie(HttpServletResponse response, String token, SeckillUser user) {
        //将用户token加入redisService缓存，有效期两天
        redisService.set(SeckillUserKeyPrefix.token, token, user);

        //设置cookie的有效期，与session有效期一致
        //token返回给客户端的，同样的，告知浏览器其token有效期为两天。之后，浏览器发送的请求中的cookie中就包含着token
        // 客户端在随后的访问中，都在cookie中上传这个token，然后服务端拿到这个token之后，就根据这个token来去缓存中取得对应的session信息（即用户信息）
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKeyPrefix.token.expireSeconds());
        cookie.setPath("/");//可在同一应用服务器内共享
        response.addCookie(cookie);
    }


}
