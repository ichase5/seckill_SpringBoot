package com.example.seckill2.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author 28614
 * @date 2019/12/17 20:11
 */

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {MobileFormatValidator.class}) //校验器
public @interface IsMobile {

    boolean required() default true;//是否一定要填写手机号

    //校验失败后，会抛出BindExeption,由GlobalExceptionHandler负责处理
    //这就是出错时的错误信息
    String message() default "手机号格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};



}
