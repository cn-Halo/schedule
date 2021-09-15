package com.yzm.schedule.timeWheel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/7/15.
 *
 * @author yzm
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {

//        int i = 60 * 60 * 24*365;
//        System.out.println(Long.MAX_VALUE / i + "年");


        long startTime = System.currentTimeMillis();
        now();
        WheelTimer wheelTimer = new WheelTimer();
        wheelTimer.addTask(3, TimeUnit.SECONDS, new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                now();
                System.out.println((time - startTime) / 1000 + "s后被执行了");
            }
        });

        TimeUnit.SECONDS.sleep(5);
        System.out.println("=========================================");
        long startTime12 = System.currentTimeMillis();
        now();
        wheelTimer.addTask(3, TimeUnit.SECONDS, new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                now();
                System.out.println((time - startTime12) / 1000 + "s后被执行了");
//                System.out.println((time - startTime) / 1000 + "s后被执行了 开始");
            }
        });
    }


    public static String now() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("当前时间： " + date);
        return date;
    }


}
