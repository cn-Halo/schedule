package com.yzm.schedule.timeWheel;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/7/15.
 *
 * @author yzm
 */
public class WheelTimer {
    /**
     * 按秒执行
     */

    /**
     * 时间轮
     * <p>
     * 组成结构：
     * 轮子：数组
     * 每个等分的执行时间：每个Bucket的执行时间
     * 轮子等分数：一个轮子的Bucket数
     */

    private WheelBucket[] wheel;
    private final long tickDurationSecond; //转换为纳秒
    private int mask;
    private volatile long round = 0;//圈数 表示时间轮已经走了第几圈了 只能由workerThread线程去更改

    private final Worker worker = new Worker();
    private final Thread workerThread = new Thread(worker);
    private final long startTimeMillis = System.currentTimeMillis();

    /**
     * @param ticksPerWheel 一个轮子有多少等分
     * @param tickDuration  一个等分执行多长时间
     * @param timeUnit      执行时间的单位
     */
    public WheelTimer(int ticksPerWheel, long tickDuration, TimeUnit timeUnit) {
        this.tickDurationSecond = timeUnit.toSeconds(tickDuration);
        this.wheel = new WheelBucket[ticksPerWheel];
        for (int i = 0; i < ticksPerWheel; i++) {
            wheel[i] = new WheelBucket(timeUnit.toMillis(tickDuration));
        }
        workerThread.start();
        this.mask = wheel.length - 1;
    }


    public WheelTimer() {
        this(8, 1, TimeUnit.SECONDS);
    }

    public void addTask(long delaySecond, Runnable task) {
        addTask(delaySecond, TimeUnit.SECONDS, task);
    }

    public void addTask(long delay, TimeUnit unit, Runnable task) {
        long deadline = System.currentTimeMillis() + unit.toMillis(delay) - startTimeMillis;
        if (deadline < 0) {
            deadline = 0;
        }
        //算出圈数和格子数
        long totalTimePerRound = wheel.length * tickDurationSecond;//一圈花费总耗时
//        System.out.println("一圈花费总耗时: " + totalTimePerRound + "s");
//        System.out.println("deadlineMillis: " + deadline);
        deadline = TimeUnit.MILLISECONDS.toSeconds(deadline);
        System.out.println("deadline: " + deadline);
        long round = deadline / totalTimePerRound;
        int tickIndex = (int) (deadline % wheel.length);// todo 不会算index
        System.out.println("round = " + round + " tickIndex = " + tickIndex);
        WheelBucket bucket = wheel[tickIndex];
        bucket.addTask(round, task);
    }

    class Worker implements Runnable {

        //走过的滴答数，即走过的秒数
        long tick = 0;

        @Override
        public void run() {
            while (!workerThread.isInterrupted()) {
                int index = (int) (tick % wheel.length);
                if (tick != 0 && index == 0) {
                    round++;
                }
                //表明已经走了一圈了
                WheelBucket bucket = wheel[index];
                bucket.run(round);
                tick++;
            }
        }
    }

}
