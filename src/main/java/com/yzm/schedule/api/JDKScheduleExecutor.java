package com.yzm.schedule.api;


import com.yzm.schedule.persistence.DelayTaskPersistor;
import io.github.halo.jdbc.JdbcTemplate;

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

    public JDKScheduleExecutor() {
    }

    public JDKScheduleExecutor(String executorName, JdbcTemplate jdbcTemplate) {
        super(executorName, jdbcTemplate);
        delayTaskPersistor.loadIncompleteTaskWhenApplicationRestart();
    }

    private DelayTaskPersistor delayTaskPersistor;

    @Override
    protected void setTaskPersistor(DelayTaskPersistor delayTaskPersistor) {
        this.delayTaskPersistor = delayTaskPersistor;
    }


    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors() + (Runtime.getRuntime().availableProcessors() + 3) / 4,
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(new ThreadGroup("delay-task-thread-pool"), r);
                        }
                    });

    @Override
    protected <V extends FutureTaskResult> Future<V> submit(RetryTask<V> command, long delay, TimeUnit unit) {
//        System.out.println("delay "+ delay + unit.toString());
        return scheduledThreadPoolExecutor.schedule(command, delay, unit);
    }
}
