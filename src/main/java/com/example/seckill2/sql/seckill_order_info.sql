
-- 按照数据库范式，这里不应该直接包含seckill_order表的数据，而是去做表的联合查询。
-- 从而得到user和goods的具体信息
-- 但是实际业务中，为了方便，并没有按照数据库范式规定，而是直接将user和goods信息字段加入了这个表中

DROP TABLE IF EXISTS `seckill_order_info`;
CREATE TABLE `seckill_order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'order_id',
  `user_id` bigint(20) DEFAULT NULL,
  `goods_id` bigint(20) DEFAULT NULL,
  `delivery_addr_id` bigint(20) DEFAULT NULL,
  `goods_name` varchar(30) DEFAULT NULL,
  `goods_count` int(11) DEFAULT NULL,
  `goods_price` decimal(10,2) DEFAULT NULL,
  `order_channel` tinyint(4) DEFAULT NULL COMMENT '订单渠道，1在线，2android，3ios',
  `status` tinyint(4) DEFAULT NULL COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date` datetime DEFAULT NULL,
  `pay_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;