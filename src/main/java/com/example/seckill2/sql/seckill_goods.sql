--秒杀商品表

-- 秒杀商品表和商品表分开
-- 原因：每次秒杀活动都会添加新的字段，如果直接修改商品表，那么商品表将变得十分巨大且难以维护

DROP TABLE IF EXISTS `seckill_goods`;
CREATE TABLE `seckill_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀商品id',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `seckill_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
  `stock_count` int(11) DEFAULT NULL COMMENT '库存数量',
  `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
  `version` int(11) DEFAULT NULL COMMENT '并发版本控制',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO `seckill_goods` VALUES ('1', '1', '0.01', '8', '2018-05-22 17:22:52', '2018-05-22 18:23:00', '0');
INSERT INTO `seckill_goods` VALUES ('2', '2', '0.01', '8', '2018-04-29 22:56:10', '2018-05-01 22:56:15', '0');