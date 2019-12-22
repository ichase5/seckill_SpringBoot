package com.example.seckill2.controller;

import com.example.seckill2.webConfig.access.AccessLimit;
import com.example.seckill2.domain.SecKillOrder;
import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.rabbitmq.MQSender;
import com.example.seckill2.rabbitmq.SeckillMessage;
import com.example.seckill2.redis.RedisService;
import com.example.seckill2.redis.prefix.GoodsKeyPrefix;
import com.example.seckill2.result.CodeMsg;
import com.example.seckill2.result.Result;
import com.example.seckill2.service.impl.SeckillGoodsService;
import com.example.seckill2.service.impl.SeckillOrderService;
import com.example.seckill2.service.impl.SeckillExecuteService;
import com.example.seckill2.vo.GoodsVo;
import com.example.seckill2.webConfig.access.SeckillUserContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 28614
 * @date 2019/12/18 16:40
 */
@Controller
@RequestMapping("/seckill") //  /seckill下面的接口不是页面接口，而是服务接口
public class SeckillController implements InitializingBean {


    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisService redisService;

    @Autowired
    SeckillExecuteService seckillExecuteService;

    @Autowired
    MQSender mqSender;

    //真实环境中，商品数量很有限，参加活动的人很多，因此大量秒杀操作注定失败
    private Map<Long,Boolean> goodsStockEmpty = new HashMap<>();//商品是否已经没有库存了

    //系统初始化,将秒杀商品库存数量放入Redis中
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = seckillGoodsService.getAllGoodsVo();
        if(goodsVoList==null){
            return;
        }
        for(GoodsVo goodsVo : goodsVoList){
            redisService.set(GoodsKeyPrefix.goodsStock,":"+goodsVo.getId(),goodsVo.getStockCount());
            goodsStockEmpty.put(goodsVo.getId(),false);
        }
    }

    /*
    加上address有两个作用：
     1 没有随机address的话，用户通过浏览器可以看到秒杀地址，可以跳过登录，直接向这个秒杀地址发送大量请求
      有了随机address，避免了大量的恶意攻击
     2 公平。防止秒杀程序提前准备好了URL，时间一到就可以进行秒杀，而用户还需要点击秒杀按钮再跳转一下。
     */
    @RequestMapping(value = "/{address}/doSeckill",method = RequestMethod.POST)//POST方法不具有幂等性
    @ResponseBody
    public Result<Integer> doSeckill(@RequestParam("goodsId") Long goodsId, @PathVariable("address") String address){

            SeckillUser seckillUser = SeckillUserContext.getUser();
            if(seckillUser==null){
                return Result.error(CodeMsg.SESSION_ERROR);
            }

            //验证address
            boolean isLegalAddress = seckillExecuteService.checkAddress(seckillUser,goodsId,address);
            if(!isLegalAddress){
                return Result.error(CodeMsg.REQUEST_ILLEGAL);
            }

            //内存标记
            //java缓存判断秒杀商品是否卖完了，为后续的redis--rabbitmq--mysql减轻压力
            //秒杀商品是有限的，大部分人注定抢不到
            boolean noMoreGoods = goodsStockEmpty.get(goodsId);
            if(noMoreGoods){//为Redis减压
                return Result.error(CodeMsg.NO_STOCK_ERROR);
            }

            /*
            重复秒杀的判断放在Redis预减库存之前，避免少卖！
            如果预减库存在判断重复秒杀之前进行，那么可能发生： 少量用户一直在进行重复秒杀，导致redis库存没了，但其实数据库中的库存还有
             */

            //判断重复秒杀,为数据库减轻压力
            //注意这里只是为了给数据库减轻压力，并不能防止重复秒杀，防止重复秒杀的唯一办法是通过数据库
            //我们的数据库中，seckill_order表有一个唯一联合索引，它可以保证不可能重复下单。重复下单，插入失败，会自动回滚
            SecKillOrder secKillOrder = seckillOrderService.getOrderBy_UserId_goodId(seckillUser.getId(),goodsId);
            if(secKillOrder!=null){//重复秒杀
                return Result.error(CodeMsg.REPEATE_SECKILL);//跳转到秒杀失败页面
            }

            //Redis预减库存
            long stockCount = redisService.decr( GoodsKeyPrefix.goodsStock,":"+goodsId );
            //为数据库减轻压力
            if(stockCount<0){
                //但是注意，这里不是线程安全的（虽然redis的decr操作本身是线程安全的，但是check and act是不安全的）
                //超卖的唯一解决办法还是数据库层面的
                goodsStockEmpty.put(goodsId,true);//标记此商品已经秒杀完了，接下来的请求不会再处理了
                return Result.error(CodeMsg.SECKILL_FAIL);
            }


            //向rabbit mq发送秒杀消息（削峰） 能进入消息队列的请求已经是全部请求的很小一部分了
            SeckillMessage seckillMessage = new SeckillMessage();
            seckillMessage.setGoodsId(goodsId);
            seckillMessage.setSeckillUser(seckillUser);
            mqSender.sendSeckillMessage(seckillMessage);

            return Result.success(0);//已提交至rabbitmq
    }


    /**
     * 查询秒杀结果
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(@RequestParam("goodsId")long goodsId) {

        SeckillUser seckillUser = SeckillUserContext.getUser();
        if(seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = seckillExecuteService.getSeckillResult(seckillUser.getId(), goodsId);
        return Result.success(result);
    }


    //生成秒杀地址(需要检查验证码的正确性）
    @AccessLimit(seconds=5,maxCount=5,needLogin=true) //全局限流拦截器
    @RequestMapping(value="/address", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> seckillAddress(@RequestParam("goodsId")long goodsId, @RequestParam("verifyCode") Integer verifyCode) {//用户输入的验证码计算结果

        SeckillUser seckillUser = SeckillUserContext.getUser();
        if(seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //检查用户输入的验证码是否正确
        if(verifyCode==null){
            return Result.error(CodeMsg.VERIFY_CODE_EMPTY);
        }

        boolean verifyCorrect = seckillExecuteService.checkVerifyCode(seckillUser,goodsId,verifyCode);
        if(!verifyCorrect){
            return Result.error(CodeMsg.VERIVY_CODE_ERROR);
        }

        String address = seckillExecuteService.createSeckillAddress(seckillUser,goodsId);
        return Result.success(address);
    }


    //生成图形验证码
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCod(HttpServletResponse response, @RequestParam("goodsId")long goodsId) {

        SeckillUser seckillUser = SeckillUserContext.getUser();
        if(seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = seckillExecuteService.createVerifyCode(seckillUser, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            //验证码已经通过response.getOutputStream()返回了
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }


/*####################################################################################*/
    /*
    用来压测的接口，没有address
     */
    @RequestMapping(value = "/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill2(@RequestParam("goodsId") Long goodsId){

        SeckillUser seckillUser = SeckillUserContext.getUser();
        if(seckillUser==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //内存标记
        //java缓存判断秒杀商品是否卖完了，为后续的redis--rabbitmq--mysql减轻压力
        //秒杀商品是有限的，大部分人注定抢不到
        boolean noMoreGoods = goodsStockEmpty.get(goodsId);
        if(noMoreGoods){//为Redis减压
            return Result.error(CodeMsg.NO_STOCK_ERROR);
        }

        /*
        重复秒杀的判断放在Redis预减库存之前，避免少卖！
        如果预减库存在判断重复秒杀之前进行，那么可能发生： 少量用户一直在进行重复秒杀，导致redis库存没了，但其实数据库中的库存还有
         */

        //判断重复秒杀,为数据库减轻压力
        //注意这里只是为了给数据库减轻压力，并不能防止重复秒杀，防止重复秒杀的唯一办法是通过数据库
        //我们的数据库中，seckill_order表有一个唯一联合索引，它可以保证不可能重复下单。重复下单，插入失败，会自动回滚
        SecKillOrder secKillOrder = seckillOrderService.getOrderBy_UserId_goodId(seckillUser.getId(),goodsId);
        if(secKillOrder!=null){//重复秒杀
            return Result.error(CodeMsg.REPEATE_SECKILL);//跳转到秒杀失败页面
        }

        //Redis预减库存
        long stockCount = redisService.decr( GoodsKeyPrefix.goodsStock,":"+goodsId );
        //为数据库减轻压力
        if(stockCount<0){
            //但是注意，这里不是线程安全的（虽然redis的decr操作本身是线程安全的，但是check and act是不安全的）
            //超卖的唯一解决办法还是数据库层面的
            goodsStockEmpty.put(goodsId,true);//标记此商品已经秒杀完了，接下来的请求不会再处理了
            return Result.error(CodeMsg.SECKILL_FAIL);
        }


        //向rabbit mq发送秒杀消息（削峰） 能进入消息队列的请求已经是全部请求的很小一部分了
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setGoodsId(goodsId);
        seckillMessage.setSeckillUser(seckillUser);
        mqSender.sendSeckillMessage(seckillMessage);

        return Result.success(0);//已提交至rabbitmq
    }


}
