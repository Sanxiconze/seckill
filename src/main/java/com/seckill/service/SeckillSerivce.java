package com.seckill.service;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExcution;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessKilled;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;

import java.util.List;

/**
 * 站在使用者的角度设计接口
 * 1.方法定义粒度（不要太繁琐抽象），参数，返回类型（return 类型/异常）
**/
public interface SeckillSerivce {
    //    查询所有秒杀记录
    List<Seckill> getSeckillList();

    //    查询单个秒杀记录
    Seckill getById( long seckillId );

    //    秒杀开始时输出秒杀接口地址，
    //    否则输出系统时间和秒杀时间
    Exposer exportSeckillUrl( long seckillId );

    //    执行秒杀操作
    SeckillExcution executeSeckill( long seckillId, long userPhone, String md5 )
            throws SeckillException, RepeatKillException, SeckillCloseException;

    //    执行秒杀操作
    SeckillExcution executeSeckillByProcedure( long seckillId, long userPhone, String md5 );


}
