package com.example.seckill2.exception;

import com.example.seckill2.result.CodeMsg;

/**
 * 自定义的业务异常
 * @author 28614
 * @date 2019/12/17 21:09
 */
public class GlobalException extends RuntimeException{

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
