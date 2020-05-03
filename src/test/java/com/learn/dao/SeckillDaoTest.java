package com.learn.dao;

import com.learn.bean.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ZixiangHu
 * @create 2020-05-03  15:37
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-dao.xml"})
public class SeckillDaoTest {

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
        int reduceNumber = seckillDao.reduceNumber(1000, new Date());
        System.err.println(reduceNumber);
    }

    @Test
    public void queryById() {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillDao.queryAll(1, 2);
        seckills.forEach(System.out::println);
    }
}