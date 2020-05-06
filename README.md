## 利用SSM构建简单的秒杀项目
---
### Day01知识点
1. 在利用SQL构建表的时候尽量加上注释，这样能够方便过了很久或者其他人使用是查看
2. mysql的默认引擎是INNODB，可以在创建表时指定
```
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
```
3. 温习了Mybatis联合查询association的使用  
例如在SuccessKilled实例中有一个Seckill属性（Seckill本身也是一个javabean），此时可以使用联合查询来对Seckill进行赋值或者使用联表查询，在这里我使用的是association联合查询
```
<!--    SuccessKilled queryByIdWithSeckill(long seckillId,long userPhone);-->
    <resultMap id="queryByIdWithSeckillMap" type="com.learn.bean.SuccessKilled">
        <id column="seckill_id" property="seckillId"/>
        <result column="user_phone" property="userPhone"/>
        <result column="create_time" property="createTime"/>
        <result column="state" property="state"/>
        <association property="seckill" select="com.learn.dao.SeckillDao.queryById" column="seckill_id"/>
    </resultMap>
    <select id="queryByIdWithSeckill" resultMap="queryByIdWithSeckillMap">
        select  seckill_id,user_phone,create_time,state from success_killed
        where seckill_id = #{seckillId} and user_phone=#{userPhone}
    </select>
```
4. 温习Mybatis和Spring的整合配置
```
<!--配置SqlSessionFactory-->
<bean class="org.mybatis.spring.SqlSessionFactoryBean" id="sqlSessionFactory">
    <property name="dataSource" ref="dataSource"/>
    <property name="configLocation" value="classpath:mybatis-config.xml"/>
    <property name="mapperLocations" value="classpath:mapper/*.xml"/>
</bean>
<!--配置扫描DAO接口包-->
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    <property name="basePackage" value="com.learn.dao"/>
<!--        <property name="annotationClass" value="org.springframework.stereotype.Repository"/>-->
</bean>
```
---
### Day02知识点
完成了Service层的设计，Service层重点需要掌握两个方法   
* Exposer exportSeckillUrl(long seckillId);

这个方法是暴露秒杀地址，当传入要秒杀的id时，首先在方法内部判断该id在数据库中是否存在，如果不存在则暴露url失败；如果传入的id在数据库中存在，则进一步判断秒杀时间是否在该商品的秒杀时间间隔内，如果是，则将id进行md5编码，并成功返回暴露的url
* SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;

这个方法是对指定的商品执行秒杀操作，该方法的调用应该是在上一个方法调用的后面。当上一个方法调用并且成功暴露url后才能调用这个方法。传入秒杀商品的id和用户手机号的同时将md5编码传入，验证md5是否与暴露出来的md5编码一致，如果一致则执行减库存操作。如果减库存操作成功，则向数据库中插入一条秒杀记录并返回秒杀结果  
除了Service层的设计，在Day02中的业务逻辑也更加复杂，不仅涉及到了事务的处理，还涉及到了自定义异常处理和日志记录等  
关于日志记录，使用的是SLF4J和Logback的组合，因此除了引入这两个的定位资源外，还需要配置Logback的xml配置文件。在类路径下命名一个logback.xml的文件，内容如下
```
<?xml version="1.0" encoding="UTF-8"?>
<!--http://logback.qos.ch/manual/configuration.html-->
<configuration debug="true">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```
其中，关于Logback更多的使用参照[Logback官方使用手册](http://logback.qos.ch/manual/configuration.html)  

总结：Service层的代码并不算多，但是其业务逻辑相比DAO层更复杂，要想理清这里的业务关系需要搞明白每一步的目的，这里是整个项目的重难点之一