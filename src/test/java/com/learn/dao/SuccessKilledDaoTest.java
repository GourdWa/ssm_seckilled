package com.learn.dao;

import com.learn.bean.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author ZixiangHu
 * @create 2020-05-03  16:16
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-dao.xml")
public class SuccessKilledDaoTest {

    @Autowired
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() {
        int i = successKilledDao.insertSuccessKilled(1001L, 18224479926L);
        System.err.println(i);
    }

    @Test
    public void queryByIdWithSeckill() {
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1000L, 18224479926L);
        System.err.println(successKilled);
    }
}