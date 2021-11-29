package com.yzm.schedule.persistence;

import com.yzm.schedule.api.DelayTask;
import com.yzm.schedule.api.FutureTaskResult;
import com.yzm.schedule.api.RetryTask;
import com.yzm.schedule.api.ScheduleExecutor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created on 2021/11/27.
 *
 * @author yzm
 */
public class DelayTaskPersistor {

    private DelayTaskDao delayTaskDao;
    private DelayTaskHistoryDao delayTaskHistoryDao;
    private ScheduleExecutor executor;

    public DelayTaskPersistor(ScheduleExecutor executor, String executorName, JdbcTemplate jdbcTemplate) {
        delayTaskDao = new DelayTaskDao(executorName, jdbcTemplate);
        delayTaskHistoryDao = new DelayTaskHistoryDao(executorName, jdbcTemplate);
        this.executor = executor;
    }

    /**
     * 应用重启之后加载未完成的持久化任务
     */
    public void loadIncompleteTaskWhenApplicationRestart() {
        List<RetryTask> list = delayTaskDao.loadTask();
        for (RetryTask retryTask : list) {
            executor.execute(retryTask);
        }
    }


    public void add(Object o) {
        //说明是JDKScheduleExecutor
        if (o instanceof FutureTask) {
            FutureTask<FutureTaskResult> future = (FutureTask<FutureTaskResult>) o;
            try {
                Field field = FutureTask.class.getDeclaredField("callable");
                field.setAccessible(true);
                Callable callable = (Callable) field.get(future);
                //todo task需要形成链表 记录上一个失败的task 。FutureTask会在任务完成后将callable设置为null
                //todo submit 不能直接返回原生Future，不然只能通过反射去取FutureTask的callable字段。
                if (!future.isDone()) {
                    DelayTask delayTask = (DelayTask) callable;
                    delayTaskDao.save(delayTask);
                    delayTaskHistoryDao.save(delayTask);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    public void remove(Object o) {
        Future<FutureTaskResult> future = (Future<FutureTaskResult>) o;
        try {
            FutureTaskResult futureTaskResult = future.get();
            delayTaskDao.delete(futureTaskResult.task().taskId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
