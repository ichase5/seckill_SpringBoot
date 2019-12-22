package com.example.seckill2.result;

/**
 * @author 28614
 * @date 2019/12/16 19:15
 * 包含错误码和错误信息
 */
public class CodeMsg {

    private int code;
    private String msg;

    //通用的错误码
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "请求非法");
    public static CodeMsg ACCESS_LIMIT_REACHED= new CodeMsg(500103, "访问太频繁！");
    public static final CodeMsg VERIVY_CODE_ERROR = new CodeMsg(500104, "验证码输入错误！");
    public static final CodeMsg VERIFY_CODE_EMPTY = new CodeMsg(500105, "请输入验证码！");

    //登录模块 5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500211, "手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500212, "密码错误");

    //商品模块 5003XX

    //订单模块 5004XX
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");

    //秒杀模块 5005XX
    public static CodeMsg NO_STOCK_ERROR = new CodeMsg(500500, "没有库存了");
    public static CodeMsg REPEATE_SECKILL = new CodeMsg(500501, "不能重复秒杀");
    public static CodeMsg SECKILL_FAIL = new CodeMsg(500502, "秒杀失败");

    //不允许使用构造函数
    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //错误码是格式化的带参数的形式，需要按照字符串格式填充参数
    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
