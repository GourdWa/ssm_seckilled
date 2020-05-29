package com.learn.service.impl;

import com.learn.bean.Seckill;
import com.learn.bean.SuccessKilled;
import com.learn.dao.RedisDao;
import com.learn.dao.SeckillDao;
import com.learn.dao.SuccessKilledDao;
import com.learn.dto.Exposer;
import com.learn.dto.SeckillExecution;
import com.learn.enums.SeckillStateEnum;
import com.learn.exception.RepeatKillException;
import com.learn.exception.SeckillCloseException;
import com.learn.exception.SeckillException;
import com.learn.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * @author ZixiangHu
 * @create 2020-05-06  19:51
 * @description
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;
    //md5盐值字符串，用于混淆md5
    private final String slat = "fegeg1g1w25449u54i274e320$#^%$Ytrtyhrt65";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //缓存优化
        //访问Redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //访问数据库
            seckill = seckillDao.queryById(seckillId);
            //如果查询的秒杀项不存在
            if (seckill == null)
                return new Exposer(false, seckillId);
            else {
                //放入Redis
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowDate = new Date();
        //如果秒杀未开启或者秒杀已经结束
        if (nowDate.getTime() < startTime.getTime() ||
                nowDate.getTime() > endTime.getTime())
            return new Exposer(false, seckillId, nowDate.getTime(), startTime.getTime(), endTime.getTime());

        //转换特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存+记录购买行为
        Date nowTime = new Date();
        try {
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //插入记录失败
            if (insertCount == 0)
                throw new RepeatKillException("seckill repeated");
            else {
                //减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录,意味着秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //如果插入成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译器异常转化为运行前异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
