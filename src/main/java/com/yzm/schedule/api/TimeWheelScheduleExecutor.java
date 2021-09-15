package com.yzm.schedule.api;



import com.yzm.schedule.timeWheel.WheelTimer;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/9/15.
 *
 * @author yzm
 */
public class TimeWheelScheduleExecutor extends AbstractScheduleExecutor {

    WheelTimer wheelTimer = new WheelTimer();

    @Override
    protected <V extends FutureTaskResult> Future<V> submit(RetryTask<V> command, long delay, TimeUnit unit) {
        return null;
    }
}
