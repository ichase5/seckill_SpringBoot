package com.example.seckill2.service.impl;

import com.example.seckill2.dao.SeckillGoodsDao;
import com.example.seckill2.domain.SecKillGoods;
import com.example.seckill2.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 28614
 * @date 2019/12/18 15:07
 */
@Service
public class SeckillGoodsService {


    @Resource
    SeckillGoodsDao seckillGoodsDao;

    public List<GoodsVo> getAllGoodsVo(){
        return seckillGoodsDao.getAllGoodsVo();
    }


    public GoodsVo getGoodsVoById(long goodsId) {
        return seckillGoodsDao.getGoodsVoById(goodsId);
    }

    public boolean reduceCount(GoodsVo goodsVo) {
        seckillGoodsDao.reduceCount(goodsVo.getId());
        return true;
    }
}
