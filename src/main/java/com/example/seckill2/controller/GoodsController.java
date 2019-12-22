package com.example.seckill2.controller;

import com.example.seckill2.domain.SeckillUser;
import com.example.seckill2.redis.RedisService;
import com.example.seckill2.redis.prefix.GoodsKeyPrefix;
import com.example.seckill2.result.Result;
import com.example.seckill2.service.impl.SeckillGoodsService;
import com.example.seckill2.vo.GoodsDetailVo;
import com.example.seckill2.vo.GoodsVo;
import com.example.seckill2.webConfig.access.SeckillUserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

// VO（ View Object）：表现层对象，是Web向模板渲染引擎（thymeleaf)传输的对象
/**
 * @author 28614
 * @date 2019/12/18 10:28
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


    @Autowired
    private SeckillGoodsService seckillGoodsService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //页面缓存
    @RequestMapping(value="/to_list",produces = "text/html")
    @ResponseBody//直接返回HTML代码作为页面
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model){//参数里可以加上user,但没有用。只要有token,都会先解析出user,再由这个方法处理

        String html = null;

        //先尝试从缓存中取出页面
        html = redisService.get(GoodsKeyPrefix.goodsListPrefix,"",String.class);
        if(!StringUtils.isEmpty(html)){
            logger.info(html);
            return html;//缓存命中
        }

        logger.info("page not from redis cache");

        //缓存中没有，手动渲染
        List<GoodsVo> goodsVoList = seckillGoodsService.getAllGoodsVo();//查询商品列表
        model.addAttribute("seckillGoodsList",goodsVoList);
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",webContext);
        if(!StringUtils.isEmpty(html)){
            //加入缓存中（有效期60秒）
            redisService.set(GoodsKeyPrefix.goodsListPrefix,"",html);
        }
        return html;
    }


    /*
    商品详情页面做 页面静态化
    */
    @RequestMapping(value="/to_detail/{GoodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(@PathVariable("GoodsId") long goodsId){

        SeckillUser seckillUser = SeckillUserContext.getUser();

        //查询商品
        GoodsVo goodsVo = seckillGoodsService.getGoodsVoById(goodsId);//商品id

        //判断秒杀是否开始，是否结束
        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();
        int remainSeconds = 0;//距离秒杀开始的时间（秒）
        if(nowTime<startTime){//秒杀未开始，倒计时
            remainSeconds = (int) (startTime-nowTime)/1000;
        }
        else if(nowTime>endTime){//秒杀已结束
            remainSeconds = -1;
        }
        else{//秒杀活动进行中
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsVo(goodsVo);
        goodsDetailVo.setSeckillUser(seckillUser);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        return Result.success(goodsDetailVo);

    }

}
