package com.example.seckill2.vo;

import com.example.seckill2.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

// VO（ View Object）：表现层对象，是Web向模板渲染引擎（thymeleaf)传输的对象
/**
 * login Value Object
 * @author 28614
 * @date 2019/12/17 15:52
 */
public class LoginVo {

    /*
    浏览器前端，jquery已经做了输入格式的验证。这里不做也可以的。
     */

    @NotNull
    @IsMobile//自己实现的注解
    private String mobile;

    @NotNull
    @Length(min=32)
    private String password;//客户端固定salt MD5后的密码

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
