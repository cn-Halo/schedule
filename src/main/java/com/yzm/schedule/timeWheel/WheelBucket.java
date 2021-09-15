package com.yzm.schedule.timeWheel;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/7/15.
 *
 * @author yzm
 */
public class WheelBucket {

    /**
     * key 代表圈数 round
     * Queue  代表任务
     */
    private Map<Long, Queue<Runnable>> roundQueue = new HashMap<>();
    private final long tickDurationMillis;

    public WheelBucket(long tickDurationMillis) {
        this.tickDurationMillis = tickDurationMillis;
    }

    public void addTask(long round, Runnable task) {
        Queue<Runnable> queue = roundQueue.get(round);
        if (queue == null) {
            Queue q = new LinkedList();
            q.offer(task);
            roundQueue.put(round, q);
        } else {
            queue.offer(task);
        }
    }


    public void run(long round) {
        final long startMillisTime = System.currentTimeMillis();
        Queue<Runnable> queue = roundQueue.get(round);
        Runnable task = null;
        while (queue != null && (task = queue.poll()) != null) {
            task.run();//todo 使用线程池来执行任务，来保障时间轮的正常走动
        }
        long endMillisTime = System.currentTimeMillis();
        long remainTime;
        //执行的时间不够补足时间

        while ((remainTime = endMillisTime - startMillisTime - tickDurationMillis) < 0) {

            try {
                TimeUnit.MILLISECONDS.sleep(Math.abs(remainTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            endMillisTime = System.currentTimeMillis();
        }
//        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        System.out.println("当前圈数: " + round + " 消耗的时间：" + ((endMillisTime - startMillisTime) / 1000) + " 当前时间：" + currentTime);

    }


}
