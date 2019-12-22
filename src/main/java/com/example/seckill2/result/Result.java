package com.example.seckill2.result;

/**
 * @author 28614
 * @date 2019/12/16 19:07
 * 接口返回JSON，使用Result封装
 * {
 *     code:
 *     msg:
 *     data:
 * }
 */
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    //注意private,不允许通过构造方法新建对象
    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }
    private Result(CodeMsg codeMsg){
        if(codeMsg==null) return;
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    /*
    成功时的Result
     */
    public static <T> Result<T> success(T data){
        return new Result<>(data);
    }

    /*
    失败时的Result
    */
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<>(codeMsg);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
