package com.yzm.schedule.api;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/9/13.
 *
 * @author yzm
 */
public interface DelayTask<V> extends Callable<V> {

    /**
     * 全局唯一，建议使用UUID
     *
     * @return
     */
    String taskId();


    long[] delayTimes();

    TimeUnit timeUnit();

    int dtIndex();

    Object attach();


    /**
     * 以下系统设置 如果任务失败，重新添加到队列中保存上一个任务的引用。
     */

    default DelayTask lastTask() {
        return null;
    }


    default DelayTask originTask() {
        return this;
    }


}
