package com.example.seckill2.service.impl;


import com.example.seckill2.domain.SecKillOrder;
import com.example.seckill2.domain.SeckillOrderInfo;
import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.redis.RedisService;
import com.example.seckill2.redis.prefix.SeckillAddressKeyPrefix;
import com.example.seckill2.redis.prefix.SeckillResultKeyPrefix;
import com.example.seckill2.redis.prefix.SeckillVerifyKeyPrefix;
import com.example.seckill2.util.MD5Util;
import com.example.seckill2.util.UUIDUtil;
import com.example.seckill2.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author 28614
 * @date 2019/12/18 17:03
 * 执行秒杀的service
 */
@Service
public class SeckillExecuteService {

    /*
    注意这里引入的是service,通常不引入其它类的dao
    因为其它的service不一定是直接调用了dao,因为其它service有可能（通常都是）会使用缓存
     */

    @Resource
    SeckillGoodsService seckillGoodsService;

    @Autowired
    SeckillOrderService seckillOrderService;

    @Autowired
    RedisService redisService;

    @Transactional//事务
    public SeckillOrderInfo doSeckill(SeckillUser seckillUser, GoodsVo goodsVo) {

        //减库存
        boolean success = seckillGoodsService.reduceCount(goodsVo);

        if(success){//减库存成功
            //写入订单 (seckill_order和seckill_order_info两个表)
            return seckillOrderService.createOrder(seckillUser,goodsVo);
        }

        //说明秒杀失败，使用redis记录失败信息（用户点击秒杀后一直在等待结果）
        markSeckillFail(seckillUser.getId(),goodsVo.getId());//标记用户对此商品秒杀失败
        return null;
    }


    /*查询秒杀执行结果
    秒杀不论成功或是失败，都会在redis中进行记录（永久有效）
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    public long getSeckillResult(Long uid, long goodsId) {
        SecKillOrder seckillOrder = seckillOrderService.getOrderBy_UserId_goodId(uid, goodsId);
        if(seckillOrder != null) {//秒杀成功
            return seckillOrder.getOrderId();
        }
        else{
            boolean isFail = isSeckillFail(uid,goodsId);//是否秒杀失败（从redis查询）
            if(isFail){
                return -1;
            }
            else{//秒杀还在排队中
                return 0;
            }
        }

    }

    private void markSeckillFail(Long uid, Long goodsId) {
        redisService.set(SeckillResultKeyPrefix.seckill_fail,":"+uid+":"+goodsId,true);
    }

    private boolean isSeckillFail(Long uid, Long goodsId) {
        return redisService.exists(SeckillResultKeyPrefix.seckill_fail,":" + uid + ":"+goodsId);
    }

    //验证秒杀地址的正确性
    public boolean checkAddress(SeckillUser user, Long goodsId, String address) {
        if(user==null || address==null){
            return false;
        }
        //请求地址在Redis里有缓存（产生地址时就加入Redis）
        String correct_address = redisService.get(SeckillAddressKeyPrefix.getSeckillAddress,":"+user.getId()+":"+goodsId,String.class);
        return address.equals(correct_address);
    }

    //产生秒杀地址
    public String createSeckillAddress(SeckillUser user, long goodsId) {

        if(user==null || goodsId<=0){
            return null;
        }
        //随机的md5加密后的串作为address
        String address = MD5Util.md5(UUIDUtil.uuid()+"9adsdfs");//md5加密
        //将秒杀地址加入Redis缓存（60秒有效期）
        redisService.set(SeckillAddressKeyPrefix.getSeckillAddress,":"+user.getId()+":"+goodsId, address);
        return address;
    }

    //产生图形验证码
    public BufferedImage createVerifyCode(SeckillUser user, long goodsId) {
        if(user==null || goodsId<=0){
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        int result = calculateResult(verifyCode);//验证码的计算结果
        //把验证码正确答案缓存到redis中（有效期300s)
        redisService.set(SeckillVerifyKeyPrefix.getSeckillVerifyCode, ":"+user.getId()+":"+goodsId, result);
        //输出图片
        return image;
    }

    //计数验证码的答案
    private int calculateResult(String expression) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(expression);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //10以内的 加减乘 操作
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = operations[rdm.nextInt(3)];
        char op2 = operations[rdm.nextInt(3)];
        String expression = ""+ num1 + op1 + num2 + op2 + num3;
        return expression;
    }

    //常量：运算符
    private static char[] operations = new char[] {'+', '-', '*'};

    //判断用户输入的验证码是否正确
    public boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode) {
        if(user==null || goodsId<=0){
            return false;
        }
        Integer result = redisService.get(SeckillVerifyKeyPrefix.getSeckillVerifyCode, ":"+user.getId()+":"+goodsId, Integer.class);
        if(result==null) return false;
        //每次检查用户输入的验证码是否过期时，就将这个验证码无效化
        redisService.delete(SeckillVerifyKeyPrefix.getSeckillVerifyCode, ":"+user.getId()+":"+goodsId);//删除Redis中的验证码结果
        return verifyCode-result==0;
    }
}
