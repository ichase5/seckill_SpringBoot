package com.example.seckill2.dao;

import com.example.seckill2.domain.SecKillOrder;
import com.example.seckill2.domain.SeckillOrderInfo;
import org.apache.ibatis.annotations.*;

/*
mybatis中，如果只有一个参数，不需要加@Param注解，自动可以进行
如果有多个参数，需要加上@Param明确指定
 */

@Mapper
public interface SeckillOrderDao {


    /*
    插入订单详情表
     */
    @Insert("insert into seckill_order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date) " +
            " values( #{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")//必须有，这样java对象才能得到自增长的值
    long insertSeckillOrderInfo(SeckillOrderInfo orderInfo);

    /*
    插入订单表
     */
    @Insert("insert into seckill_order (user_id, goods_id, order_id) values(#{userId}, #{goodsId}, #{orderId})")
    int insertSeckillOrder(SecKillOrder secKillOrder);

    /*
    查询订单详情
     */
    @Select("select * from seckill_order_info where id = #{orderId}")
    SeckillOrderInfo getOrderByOId(@Param("orderId") long orderId);
}
