package com.yzm.schedule.api;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * Created on 2021/9/13.
 *
 * @author yzm
 */
public interface ScheduleExecutor extends Executor {


    /**
     * 按照给定的一组delayTime时间，顺序延迟执行
     * @param command
     * @param <V>
     * @return
     */
    <V> Future<V> execute(DelayTask<V> command);


    /**
     * 带有失败重试机制，成功或者到最大次数(delayTimes.length)则不再重试。否则到 delayTime[dtIndex+1] 延迟后重试
     * 一组延迟时间是相对与启始时间，而不是相对于上一个延迟时间
     * 如果delayTimes中有负数，则会被重置为0，并且剔除掉延迟时间相同的值
     * @param command
     * @param <V>
     * @return
     */

    <V extends FutureTaskResult>  Future<V> execute(RetryTask<V> command);

}
