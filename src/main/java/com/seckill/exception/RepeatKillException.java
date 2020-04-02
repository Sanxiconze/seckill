package com.seckill.exception;

/**
 * 重复秒杀异常
 * Created by wangtc on 2020年3月29日
 */
public class RepeatKillException extends SeckillException{
    public RepeatKillException( String message ) {
        super(message);
    }

    public RepeatKillException( String message, Throwable cause ) {
        super(message, cause);
    }
}
