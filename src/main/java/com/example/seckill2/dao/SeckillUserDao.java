package com.example.seckill2.dao;

import com.example.seckill2.domain.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/*
mybatis中，如果只有一个参数，不需要加@Param注解，自动可以进行
如果有多个参数，需要加上@Param明确指定
 */

@Mapper
public interface SeckillUserDao {

    @Select("select * from seckill_user where id = #{id}")
    SeckillUser getById(@Param("id") long id);

}
