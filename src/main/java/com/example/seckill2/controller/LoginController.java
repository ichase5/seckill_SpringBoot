package com.example.seckill2.controller;

import com.example.seckill2.result.Result;
import com.example.seckill2.service.impl.SeckillUserService;
import com.example.seckill2.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author 28614
 * @date 2019/12/17 15:22
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillUserService seckillUserService;

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login"; //跳转到登录页面
    }

    @RequestMapping("/do_login")
    @ResponseBody//返回JSON，否则默认跳转页面
    //@Valid为jsr303参数校验，表明需要对LoginVo类中的一些valid 注解进行检查
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        logger.info(loginVo.toString());//方便调试
        //登录，验证手机号是否是注册账户，以及密码是否正确
        String token = seckillUserService.login(response, loginVo);//若登录条件不满足，直接抛出异常
        return Result.success(token);//token没用，登录成功就直接跳转商品列表页了
    }
}
