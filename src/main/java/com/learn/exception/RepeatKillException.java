package com.learn.exception;

/**
 * @author ZixiangHu
 * @create 2020-05-06  19:45
 * @description 重复秒杀异常
 */
public class RepeatKillException extends SeckillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
