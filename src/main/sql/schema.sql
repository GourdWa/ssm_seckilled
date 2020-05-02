-- 数据库初始化脚本
CREATE DATABASE seckill;
-- 使用数据库
use seckill;
-- 创建秒杀库存表
CREATE TABLE seckill(
  `seckill_id` BIGINT NOT NUll AUTO_INCREMENT COMMENT '商品库存ID',
  `name` VARCHAR(120) NOT NULL COMMENT '商品名称',
  `number` int NOT NULL COMMENT '库存数量',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `start_time` TIMESTAMP  NOT NULL COMMENT '秒杀开始时间',
  `end_time`   TIMESTAMP   NOT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (seckill_id),
  key idx_start_time(start_time),
  key idx_end_time(end_time),
  key idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- 初始化数据
insert into seckill(name,number,start_time,end_time)
values
('1000元秒杀华为P40Pro',10,'2020-05-05 00:00:00','2020-05-06 00:00:00'),
('500元秒杀ipad mini',50,'2020-05-05 00:00:00','2020-05-06 00:00:00'),
('100元秒杀荣耀智慧屏',100,'2020-05-05 00:00:00','2020-05-06 00:00:00'),
('200元秒杀魅族17',30,'2020-05-05 00:00:00','2020-05-06 00:00:00');

-- 设计秒杀成功明细表
-- 用户登录认证相关的信息
create table success_killed(
    `seckill_id` bigint not null COMMENT '秒杀商品id',
    `user_phone` bigint not null COMMENT '用户手机号',
    `state` tinyint not null default -1 COMMENT '状态：-1：无效 0：成功 1：已付款 2：已发货',
    `create_time` timestamp not null COMMENT '创建时间',
    primary key(seckill_id,user_phone),/*联合主键*/
    key idx_create_time(create_time)
)engine=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- 连接数据库控制台
mysql -uroot -proot


