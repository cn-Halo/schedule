package test;

import com.yzm.schedule.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/9/14.
 *
 * @author yzm
 */
public class Test {

    public static void main(String[] args) {


        ScheduleExecutor executor = new JDKScheduleExecutor();
        executor.execute(new RetryTask<FutureTaskResult>() {
            @Override
            public String taskId() {
                return null;
            }

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
                boolean b = true;
//                if (b) {
//                    throw new RuntimeException("测试");
//                }
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


    }

}
