package com.learn.bean;

import java.util.Date;

/**
 * @author ZixiangHu
 * @create 2020-05-03  13:59
 * @description 秒杀成功的订单
 */
public class SuccessKilled {
    //秒杀的商品id
    private long seckillId;
    //参与秒杀的用户手机号
    private long userPhone;
    //秒杀状态
    private short state;
    //订单创建时间
    private Date createTime;
    //每个订单编号都对应了一个商品
    private Seckill seckill;

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                ", seckill=" + seckill +
                '}';
    }
}
