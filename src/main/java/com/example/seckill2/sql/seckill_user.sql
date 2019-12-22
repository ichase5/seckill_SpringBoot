create table `seckill_user`(
`id` bigint(20) not null comment '用户Id,即手机号码',
`nickname` varchar(256) not null comment '用户昵称',
`password` varchar(32) default null comment 'MD5( MD5(pass明文+固定salt) + 随机salt )',
`salt` varchar(10) default null comment '服务器端随机salt',
`head` varchar(128) default null comment '头像，云存储的id',
`register_date` datetime default null comment '注册时间',
`last_login_date` datetime default null comment '上次登录时间',
`login_count` int(11) default 0 comment '登录次数',
primary key(`id`)
)engine=InnoDB;

--插入一行
insert into seckill_user(id,nickname,password,salt,register_date,last_login_date,login_count)
values(15682894319,"leo","42573cbbf0b62e9b9eead56dc61289ee","xxy",'2019-12-01 00:00:00','2019-12-15 00:00:00',1);

insert into seckill_user(id,nickname,password,salt,register_date,last_login_date,login_count)
values(18092796597,"yingzi","42573cbbf0b62e9b9eead56dc61289ee","xxy",'2019-12-01 00:00:00','2019-12-15 00:00:00',1);

insert into seckill_user(id,nickname,password,salt,register_date,last_login_date,login_count)
values(13012345678,"testUser","42573cbbf0b62e9b9eead56dc61289ee","xxy",'2019-12-01 00:00:00','2019-12-15 00:00:00',1);
--
select * from seckill_user;