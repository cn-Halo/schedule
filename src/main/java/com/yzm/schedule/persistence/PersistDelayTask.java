package com.yzm.schedule.persistence;

import com.yzm.schedule.api.FutureTaskResult;
import com.yzm.schedule.api.RetryTask;

import java.lang.invoke.MethodHandle;
import java.util.concurrent.TimeUnit;

/**
 * @author yzm
 * @date 2021/11/28 20:39
 */
public class PersistDelayTask implements RetryTask<FutureTaskResult> {

    private long[] delayTimes;
    private TimeUnit timeUnit;
    private int dtIndex;
    private String taskId;
    private MethodHandle methodHandle;

    public PersistDelayTask() {

    }

    public PersistDelayTask(String taskId, long[] delayTimes, TimeUnit timeUnit, int dtIndex, MethodHandle methodHandle) {
        this.delayTimes = delayTimes;
        this.timeUnit = timeUnit;
        this.dtIndex = dtIndex;
        this.methodHandle = methodHandle;
        this.taskId = taskId;
    }

    public void setDelayTimes(long[] delayTimes) {
        this.delayTimes = delayTimes;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setDtIndex(int dtIndex) {
        this.dtIndex = dtIndex;
    }


    @Override
    public String taskId() {
        return this.taskId;
    }

    @Override
    public long[] delayTimes() {
        return this.delayTimes;
    }

    @Override
    public TimeUnit timeUnit() {
        return this.timeUnit;
    }

    @Override
    public int dtIndex() {
        return this.dtIndex;
    }

    @Override
    public Object attach() {
        return null;
    }

    @Override
    public FutureTaskResult call() throws Exception {
        try {
            //todo 当执行是已经从数据库加载过一次的任务，那么methodHandle将丢失。需要保留原始任务的方法句柄。
            return (FutureTaskResult) methodHandle.invoke();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
