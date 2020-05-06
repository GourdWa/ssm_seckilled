package com.learn.exception;

/**
 * @author ZixiangHu
 * @create 2020-05-06  19:48
 * @description 秒杀相关异常
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
