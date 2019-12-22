package com.example.seckill2.vo;

import com.example.seckill2.domain.Goods;

import java.util.Date;

// VO（ View Object）：表现层对象，是Web向模板渲染引擎（thymeleaf)传输的对象
/**
 * @author 28614
 * @date 2019/12/18 15:09
 * 这里我们查数据库的时候，不只是查找的商品的信息，
 * 我们同时想把商品的秒杀信息也一起查出来，但是这两个不同数据在两个表里面，
 * 我们就想办法封装一个GoodsVo，将商品信息和它的秒杀信息封装到一个对象中
 */
public class GoodsVo extends Goods {

    //秒杀信息
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
