package com.yzm.schedule.api;

import java.util.concurrent.TimeUnit;

/**
 * @author yzm
 * @date 2021/11/28 15:26
 */
public class RetryTaskImpl<V extends FutureTaskResult> implements RetryTask<V> {

    /**
     * 用户参数
     */
    private long[] delayTimes;
    private TimeUnit timeUnit;
    private int dtIndex;
    private Object attach;

    /**
     * 系统参数
     */
    private DelayTask lastTask;
    private RetryTask originTask;
    private String taskId;

    public RetryTaskImpl() {

    }

    public RetryTaskImpl(long[] delayTimes, TimeUnit timeUnit,
                         int dtIndex, Object attach,
                         RetryTask retryTask) {

    }

    @Override
    public String taskId() {
        return null;
    }

    @Override

    public long[] delayTimes() {
        return new long[0];
    }

    @Override
    public TimeUnit timeUnit() {
        return null;
    }

    @Override
    public int dtIndex() {
        return 0;
    }

    @Override
    public Object attach() {
        return null;
    }

    @Override
    public V call() throws Exception {
        return null;
    }

    @Override
    public DelayTask lastTask() {
        return null;
    }
}
