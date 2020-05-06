package com.learn.service;

import com.learn.bean.Seckill;
import com.learn.dto.Exposer;
import com.learn.dto.SeckillExecution;
import com.learn.exception.RepeatKillException;
import com.learn.exception.SeckillCloseException;
import com.learn.exception.SeckillException;

import java.util.List;

/**
 * @author ZixiangHu
 * @create 2020-05-06  19:30
 * @description
 */

public interface SeckillService {
    /**
     * 查询所有的秒杀记录
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口的地址，
     * 否则输出系统时间和秒杀时间
     *
     * @param seckillId
     * @return dto
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;
}
