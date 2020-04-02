package com.seckill.dao;

import com.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangtc on 2020年3月27日
 */
public interface SeckillDao {

    /**
     * 减库存
     */
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     */

    List<Seckill> queryAll(@Param("offset") int offet, @Param("limit") int limit);

    /**
     * 存储过程
     * @param paramMap
     */
    void killByProcedure( Map<String,Object> paramMap);
}
