package com.yzm.schedule.api;

/**
 * Created on 2021/9/13.
 *
 * @author yzm
 */
public interface FutureTaskResult {

    boolean success();
    Object data();
    default <T extends RetryTask> T task(){
        return null;
    }
    default FutureTaskResult attach(){
        return this;
    }

}
