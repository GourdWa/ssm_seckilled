package com.learn.service;

import com.learn.bean.Seckill;
import com.learn.dto.Exposer;
import com.learn.dto.SeckillExecution;
import com.learn.exception.RepeatKillException;
import com.learn.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ZixiangHu
 * @create 2020-05-06  21:49
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-dao.xml", "classpath:spring-service.xml"})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("seckillList={}", seckillList);
    }

    @Test
    public void getById() {
        Seckill seckill = seckillService.getById(1000);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillLogic() {
        long id = 1001L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            //        exposer=Exposer{exposed=true, md5='19ef085b8ea812a566eb118d5384f314', seckillId=1000, now=0, start=0, end=0}
            logger.info("exposer={}", exposer);
            long phone = 18281613106L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("seckillExecution={}", seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }
        }else {
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void executeSeckill() {
        long id = 1000L;
        long phone = 18281613107L;
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, "19ef085b8ea812a566eb118d5384f314");
            logger.info("seckillExecution={}", seckillExecution);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        }

    }
}