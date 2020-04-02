package com.seckill.dao;

import com.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * Created by wangtc on 2020年3月27日
 */
public interface SuccessKilledDao {
    /*
     * 插入购买明细，可过滤重复
     */
    int insertSuccessKilled ( @Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

    /*
     * 根据id查询SuccessKilled并携带秒杀商品对象实体
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId ,@Param("userPhone") long userPhone);
}
