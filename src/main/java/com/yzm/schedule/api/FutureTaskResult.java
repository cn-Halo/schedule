package com.yzm.schedule.api;

/**
 * Created on 2021/9/13.
 *
 * @author yzm
 */
public interface FutureTaskResult {

    boolean success();

    Object data();

    /**
     * 用户无需设置，保存的这一次的任务
     *
     * @param <T>
     * @return
     */
    default <T extends RetryTask> T task() {
        return null;
    }

    default FutureTaskResult attach() {
        return this;
    }

}
