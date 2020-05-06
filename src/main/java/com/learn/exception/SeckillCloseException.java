package com.learn.exception;

/**
 * @author ZixiangHu
 * @create 2020-05-06  19:47
 * @description 秒杀关闭异常
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeckillCloseException(String message) {
        super(message);
    }
}
