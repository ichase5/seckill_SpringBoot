package com.example.seckill2.dao;

import com.example.seckill2.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;


/*
mybatis中，如果只有一个参数，不需要加@Param注解，自动可以进行
如果有多个参数，需要加上@Param明确指定
 */



@Mapper
public interface SeckillGoodsDao {

    @Select("select goods.*, seckill_goods.seckill_price,seckill_goods.stock_count,seckill_goods.stock_count,seckill_goods.start_date,seckill_goods.end_date from seckill_goods left join goods on seckill_goods.goods_id=goods.id")
    List<GoodsVo> getAllGoodsVo();

    @Select("select goods.*, seckill_goods.seckill_price,seckill_goods.stock_count,seckill_goods.stock_count,seckill_goods.start_date,seckill_goods.end_date from seckill_goods left join goods on seckill_goods.goods_id=goods.id where goods.id=#{goodsId}")
    GoodsVo getGoodsVoById(@Param("goodsId") long goodsId);

    //避免超卖，所以不能为负 数据库操作本身是保证原子性的
    @Update("update seckill_goods set stock_count = stock_count-1 where goods_id = #{goodsId} and stock_count>0")
    int reduceCount(@Param("goodsId") long goodsId);
}
