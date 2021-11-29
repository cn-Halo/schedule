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


    /**
     * 一组延迟时间
     *
     * @return
     */
    long[] delayTimes();

    /**
     * 延迟时间的单位
     *
     * @return
     */
    TimeUnit timeUnit();

    /**
     * delayTimes的开始下标
     *
     * @return
     */
    int dtIndex();

    /**
     * 附件
     *
     * @return
     */
    Object attach();


    /**
     * 以下系统设置 如果任务失败，重新添加到队列中保存上一个任务的引用。
     */

//    default DelayTask lastTask() {
//        return null;
//    }


    /**
     * 系统设置 持有原始任务
     *
     * @return
     */
    default DelayTask originTask() {
        return this;
    }


}
