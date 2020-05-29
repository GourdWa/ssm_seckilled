package com.learn.dao;

import com.learn.bean.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author ZixiangHu
 * @create 2020-05-03  14:02
 * @description
 */
public interface SeckillDao {

    /**
     * 减库存
     * 秒杀成功后减库存，秒杀成功的条件是库存大于0，且秒杀的时间在秒杀开始和秒杀结束之间
     *
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     *
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     *
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(int offset, int limit);

}
