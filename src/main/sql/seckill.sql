-- 秒杀执行存储过程
DELIMITER $$ -- console; 转换为$$

-- 定义存储过程
-- 参数： in 输入参数 ； out 输出参数

-- count_count(); 返回上一条修改类型sql(delete,insert,update)的影响行数；
-- row_count: 0：未修改    >0 ：修改的行数  <0 : sql错误/未执行sql
CREATE PROCEDURE `seckill`.`execute_seckill`
    (in v_seckill_id bigint, in v_phone bigint,
        in v_kill_time timestamp, out r_result int)
BEGIN
    DECLARE insert_count int DEFAULT 0;

    -- 开启事务
    START TRANSACTION;

    -- 插入秒杀成功
    insert ignore into success_killed
        (seckill_id,user_phone,create_time)
        values(v_seckill_id,v_phone,v_kill_time);

    select row_count() into insert_count;

    -- 插入失败则回滚
    IF(insert_count = 0) THEN
        ROLLBACK;
        set r_result = -1;
    ELSEIF(insert_count < 0) THEN
        ROLLBACK;
        set r_result = -2;
    ELSE
    -- 插入成功则更新number
        UPDATE seckill
        set number = number - 1
        where seckill_id = v_seckill_id
            and end_time > v_kill_time
            and start_time < v_kill_time
            and number > 0;
        select  row_count() into insert_count;

        -- 更新number失败则回滚
        IF (insert_count = 0) THEN
            ROLLBACK ;
            set r_result = 0;
        ELSEIF(insert_count < 0) THEN
            ROLLBACK ;
            set r_result = -2;
        ELSE
        -- 更新成功就提交
            COMMIT;
            set r_result = 1;
        END IF;
    END IF;
END;
$$
-- 存储过程定义结束

DELIMITER ;
set @r_result = -3;

-- 执行存储过程
call execute_seckill(1003,13131313131,now(),@r_result);

-- 获取结果
select @r_result;