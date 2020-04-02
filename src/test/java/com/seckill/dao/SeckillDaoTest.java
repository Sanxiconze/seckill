package com.seckill.dao;

import com.seckill.entity.Seckill;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
        int updateNumber = seckillDao.reduceNumber(1000L,new Date());
        System.out.println(updateNumber);
    }

    @Test
    public void queryById() {

        long id = 1000;
        System.out.println(seckillDao);
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
        List<Seckill> seckillList = seckillDao.queryAll(0,100);
        System.out.println(seckillList);
    }
}