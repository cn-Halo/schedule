package com.yzm.schedule.api;

import com.yzm.schedule.persistence.DelayTaskPersistor;
import io.github.halo.jdbc.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/9/14.
 *
 * @author yzm
 */
public abstract class AbstractScheduleExecutor implements ScheduleExecutor {

    private final BlockingQueue<Future<FutureTaskResult>> queue;

    private Thread thread;

    public AbstractScheduleExecutor() {
        queue = new LinkedBlockingQueueProxy();
        init();
    }

    public AbstractScheduleExecutor(String executorName, JdbcTemplate jdbcTemplate) {
        LinkedBlockingQueueProxy queue = new LinkedBlockingQueueProxy(this, executorName, jdbcTemplate);
        DelayTaskPersistor delayTaskPersistor = queue.getDelayTaskPersistor();
        this.queue = queue;
        init();
        setTaskPersistor(delayTaskPersistor);
    }

    private void init() {
        thread = new Thread(new RetryTaskLoopWorker(queue, this), "delay-task-loop-thread");
        thread.start();
    }

    protected abstract void setTaskPersistor(DelayTaskPersistor delayTaskPersistor);

    protected abstract <V extends FutureTaskResult> Future<V> submit(RetryTask<V> command, long delay,
                                                                     TimeUnit unit);

    @Override
    public <V> Future<V> execute(DelayTask<V> command) {
        return null;
    }

    @Override
    public <V extends FutureTaskResult> Future<V> execute(RetryTask<V> command) {
        //得到当前时间的毫秒数，以防止接下来的校验参数，排序等耗时多，导致相对延迟时间计算的不准确。
        long currentTimeMillis = System.currentTimeMillis();
        //校验参数
        checkParam(command);
        //拷贝数据防止篡改 delayTime 排序并换算成绝对时间
        final RetryTask task = copyAndSort(command, currentTimeMillis);
        //计算出相对的延迟时间
        long[] delayTimes = task.delayTimes();
        int dtIndex = task.dtIndex();
        long delayTime = delayTimes[dtIndex] - currentTimeMillis < 0 ? 0 : delayTimes[dtIndex] - currentTimeMillis;
        Future futureResult = submit(task, delayTime, task.timeUnit());
        queue.add(futureResult);
        return futureResult;
    }

    @Override
    public void execute(Runnable command) {

    }

    private void checkParam(DelayTask command) {
        long[] delayTime = command.delayTimes();
        int dtIndex = command.dtIndex();
        if (Objects.isNull(delayTime)) {
            throw new NullPointerException("delayTime is null");
        }
        if (delayTime.length == 0) {
            throw new NullPointerException("delayTime is empty");
        }
        if (dtIndex < 0 || dtIndex >= delayTime.length) {
            throw new ArrayIndexOutOfBoundsException("dtIndex is error, dtIndex is " + dtIndex + ", delayTime length is " + delayTime.length);
        }
    }

    private RetryTask copyAndSort(RetryTask command, long currentTimeMillis) {
        long[] delayTimes = command.delayTimes();
        TimeUnit timeUnit = command.timeUnit();
        //升序排序
        Arrays.sort(delayTimes);
        //当前时间毫秒书
        long startTimeMillis = currentTimeMillis;
        //绝对超时时间的集合
        List<Long> absoluteDelayTimeList = new ArrayList<>();
        //换算成绝对时间 todo i可以设置成dtIndex
        for (int i = 0; i < delayTimes.length; i++) {
            long delayTime = delayTimes[i];
            //如果delayTime是负数 ，则会被重置为0；
            if (delayTime < 0) {
                delayTime = 0;
            }
            //延迟的毫秒数
            long delayMillis = timeUnit.toMillis(delayTime);
            long absoluteDelayTimeMillis = startTimeMillis + delayMillis;
            //剔除掉重复的延迟时间。
            if (!absoluteDelayTimeList.contains(absoluteDelayTimeMillis)) {
                absoluteDelayTimeList.add(absoluteDelayTimeMillis);
            }
        }

        long[] absoluteDelayTimes = new long[absoluteDelayTimeList.size()];
        for (int i = 0; i < absoluteDelayTimeList.size(); i++) {
            absoluteDelayTimes[i] = absoluteDelayTimeList.get(i);
        }

        return new RetryTask() {

            @Override
            public Object call() throws Exception {
                RetryTask newTask = this;
                FutureTaskResult futureTaskResult = (FutureTaskResult) command.call();
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
                        return futureTaskResult;
                    }
                };

            }

            @Override
            public long[] delayTimes() {
                return absoluteDelayTimes;
            }

            @Override
            public TimeUnit timeUnit() {
                return TimeUnit.MILLISECONDS;
            }

            @Override
            public int dtIndex() {
                //todo 因为剔除了之后dtIndex没有处理，加载数据库中的任务可能是超时的。 下标会越界。
                //todo 剔除重复元素之后，修复dtIndex的算法需要优化.
                int i = command.delayTimes().length - absoluteDelayTimes.length;
                return command.dtIndex() - i < 0 ? 0 : command.dtIndex() - i;
//                return command.dtIndex();
            }

            @Override
            public Object attach() {
                return command.attach();
            }

            @Override
            public String taskId() {
                return command.taskId();
            }

            @Override
            public DelayTask originTask() {
                return command;
            }
        };

    }
}
