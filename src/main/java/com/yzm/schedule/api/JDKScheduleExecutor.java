package com.yzm.schedule.api;



import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/9/14.
 *
 * @author yzm
 */
public class JDKScheduleExecutor extends AbstractScheduleExecutor {

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors() + (Runtime.getRuntime().availableProcessors() + 3) / 4,
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(new ThreadGroup("delay-task-thread-pool"),r);
                        }
                    });

    @Override
    protected <V extends FutureTaskResult> Future<V> submit(RetryTask<V> command, long delay, TimeUnit unit) {
        System.out.println("delay "+ delay + unit.toString());
        return scheduledThreadPoolExecutor.schedule(command,delay,unit);
    }
}
