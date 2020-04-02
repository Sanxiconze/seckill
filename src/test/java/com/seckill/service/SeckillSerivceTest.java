package com.seckill.service;

import com.seckill.dao.SeckillDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExcution;
import com.seckill.entity.Seckill;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
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
 * Created by wangtc on 2020/3/29
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillSerivceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillSerivce seckillSerivce;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillSerivce.getSeckillList();
        logger.info("list = {}",list);
    }

    @Test
    public void getById() {
        long id = 1000L;
        Seckill seckill = seckillSerivce.getById(id);
        logger.info("seckill = {}",seckill);
    }

    @Test
    public void exportSeckillUrlAndEcecuteSeckill() {
        long id = 1002L;
        long phone = 12345122901L;
        Exposer exposer = seckillSerivce.exportSeckillUrl(id);
        logger.info("exposer = {}" ,exposer);
        if(exposer.isExposed()){
            try{
  //              SeckillExcution seckillExcution = seckillSerivce.executeSeckill(id,phone,exposer.getMd5());
                SeckillExcution seckillExcution = seckillSerivce.executeSeckillByProcedure(id,phone,exposer.getMd5());

                logger.info("seckillExcution = {}" , seckillExcution);
            }catch (RepeatKillException e){
                logger.info("repeat",e.getMessage());
            }catch (SeckillCloseException e1){
                logger.info("close", e1.getMessage());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            logger.warn("还没有到秒杀时间");
        }

        /**
         * c.seckill.service.SeckillSerivceTest -
         * exposer = Exposer{
         * exposed=true,
         * md5='beb8fa97730869634ac3939849d0d87c',
         * seckillId=1000, now=0, start=0, end=0}
         */
    }


    @Test
    public void executeSeckillByProcedure() {
        long seckillId = 1003;
        long phone = 13680111011L;
        Exposer exposer = seckillSerivce.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExcution excution = seckillSerivce.executeSeckillByProcedure(seckillId, phone, md5);
            logger.info(excution.getStateInfo());
        }
    }
}