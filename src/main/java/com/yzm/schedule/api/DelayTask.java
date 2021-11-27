package com.yzm.schedule.api;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/9/13.
 *
 * @author yzm
 */
public interface DelayTask<V> extends Callable<V>, Task {

    long[] delayTimes();

    TimeUnit timeUnit();

    int dtIndex();

    Object attach();

}
