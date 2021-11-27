package com.yzm.schedule.api;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/11/27.
 *
 * @author yzm
 */
public class TaskUtils {

    public static RetryTask buildTask(RetryTask task) {
        if (task == null) {
            return null;
        }
        long[] delayTimes = task.delayTimes();
        int dtIndex = task.dtIndex();
        //已到达数组末尾 ，则不再执行
        if (dtIndex >= delayTimes.length - 1) {
            return null;
        }

        return new RetryTask() {

            @Override
            public Object call() throws Exception {
                RetryTask newTask = this;
                FutureTaskResult futureTaskResult = (FutureTaskResult) task.call();
                return new FutureTaskResult() {
                    @Override
                    public boolean success() {
                        return futureTaskResult.success();
                    }

                    @Override
                    public Object data() {
                        return futureTaskResult.data();
                    }

                    @Override
                    public <T extends RetryTask> T task() {
                        return (T) newTask;
                    }

                    @Override
                    public FutureTaskResult attach() {
                        return futureTaskResult.attach();
                    }
                };
            }

            @Override
            public long[] delayTimes() {
                return task.delayTimes();
            }

            @Override
            public TimeUnit timeUnit() {
                return task.timeUnit();
            }

            @Override
            public int dtIndex() {
                return task.dtIndex() + 1;
            }

            @Override
            public Object attach() {
                return task.attach();
            }
        };
    }


}
