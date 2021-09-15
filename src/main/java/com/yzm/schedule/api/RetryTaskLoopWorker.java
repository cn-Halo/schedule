package com.yzm.schedule.api;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/9/14.
 *
 * @author yzm
 */
public class RetryTaskLoopWorker implements Runnable {

    private final  BlockingQueue<Future<FutureTaskResult>> queue;
    private final AbstractScheduleExecutor executor;

    public RetryTaskLoopWorker(BlockingQueue<Future<FutureTaskResult>> queue, AbstractScheduleExecutor executor){
        this.queue = queue;
        this.executor = executor;
    }
    @Override
    public void run() {
        for (;;){
            //如果为空，则阻塞等待队列不为空
            if(queue.isEmpty()){
                try {
                    Future future =  queue.take();
                    queue.add(future);//使用add抛出异常可见
                } catch (InterruptedException e) {
                    Thread.interrupted();//不响应中断
                }
            }
            //loop
            Iterator<Future<FutureTaskResult>> iterator = queue.iterator();
            while (iterator.hasNext()){
                Future<FutureTaskResult> future = iterator.next();
                try {
                    if(future.isDone()){
                        FutureTaskResult result = future.get();
                        //任务失败
                        if(!result.success()){
                            RetryTask task = buildTask(result.task());
                            if(task != null){
                                //绝对超时时间，在换算成相对的延迟时间
                                long[] delayTimes = task.delayTimes();
                                int dtIndex = task.dtIndex();
                                long absoluteTimeMillis = delayTimes[dtIndex];
                                long currentTimeMillis = System.currentTimeMillis();
                                long delayTime = absoluteTimeMillis - currentTimeMillis < 0 ? 0: absoluteTimeMillis -currentTimeMillis;
                                Future futureResult = executor.submit(task,delayTime,task.timeUnit());
                                queue.add(futureResult);
                            }
                        }
                        //任务完成则移除队列
                        queue.remove(future);
                    }
                } catch (InterruptedException e) {
                    Thread.interrupted();//不响应中断
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private RetryTask buildTask(RetryTask task){
        if(task == null){
            return null;
        }
        long[] delayTimes = task.delayTimes();
        int dtIndex = task.dtIndex();
        //已到达数组末尾 ，则不再执行
        if(dtIndex >= delayTimes.length-1){
            return null;
        }

       return new RetryTask(){

            @Override
            public Object call() throws Exception {
                RetryTask newTask=  this ;
                FutureTaskResult  futureTaskResult = (FutureTaskResult)  task.call();
                return new FutureTaskResult(){
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
                return task.dtIndex()+1;
            }

            @Override
            public Object attach() {
                return task.attach();
            }
        };
    }
}
