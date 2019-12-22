package com.example.seckill2.exception;

import com.example.seckill2.result.CodeMsg;
import com.example.seckill2.result.Result;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import java.util.List;

/**
 * @author 28614
 * @date 2019/12/17 20:46
 * 异常拦截器
 */
@ControllerAdvice//拦截所有带有@RequestMapping注解的方法
@ResponseBody//当参数校验不通过的时候，输出是Result JSON,传给前端用于前端显示获取处理
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result<String> exceptionHandler(HttpServletRequest httpServletRequest,Exception e){

        e.printStackTrace();//很重要，方便调试！否则出了错没法找错误了

        if(e instanceof GlobalException){//业务异常
            GlobalException ex = (GlobalException)e;
            CodeMsg codeMsg = ex.getCodeMsg();
            return Result.error(codeMsg);
        }
        else if(e instanceof BindException){//参数校验异常（手机号格式不对，手机号为空）
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String errorMsg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(errorMsg));
        }
        else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

}
