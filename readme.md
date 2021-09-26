## 支持一组延迟时间，并带有失败重试机制延迟执行器
### 具有以下功能
- 失败重试
- 一组延迟执行


## 组件

### RetryTask
> 带有失败重试机制的延迟任务 
#### delayTimes 
- 一个long类型的数组，支持设置一组延迟时间，数组中的每个延迟时间是相对于第一次的添加任务时间而言的。
- 数组的长度代表了最大的执行次数，当某一次任务的结果FutureTaskResult.success()返回true。则代表任务执行成功，后续的延迟将不在执行。
- 添加任务时，会对原任务进行拷贝，防止执行过程中任务数据被篡改。原任务可以从FutureTaskResult.attach()附件中获取。
### DelayTask
普通的延迟队列任务

## 用法
```
   ScheduleExecutor executor = new JDKScheduleExecutor();
        executor.execute(new RetryTask<FutureTaskResult>() {
            @Override
            public long[] delayTimes() {
                return new long[]{2l, 4l, 6l};
            }

            @Override
            public TimeUnit timeUnit() {
                return TimeUnit.SECONDS;
            }

            @Override
            public int dtIndex() {
                return 0;
            }

            @Override
            public Object attach() {
                return null;
            }

            @Override
            public FutureTaskResult call() {
                System.out.println("当前时间 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                return new FutureTaskResult() {
                    @Override
                    public boolean success() {
                        double d = Math.random();
                        System.out.println(d);
                        return d > 0.8 ? true : false;
                    }

                    @Override
                    public Object data() {
                        return null;
                    }

                };
            }
        });

```


## 后续将支持任务的持久化和监控   
## BUG 提交获取到的future只是第一次提交任务的时候获取到的


## 2021-09-26
### call()方法中抛出异常 会导致死循环抛出异常
```
    
FutureTaskResult result = future.get();
抛出的执行异常被捕获并未处理，导致queue中一直存在这个任务，轮询中不断的执行future.get()抛出异常

```