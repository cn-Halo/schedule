package test;

import com.yzm.schedule.api.FutureTaskResult;
import com.yzm.schedule.api.RetryTask;
import io.github.halo.snowFlake.SnowFlakeUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author yzm
 * @date 2021/11/28 15:57
 */
@Data
public class MyTask implements RetryTask<FutureTaskResult> {

    private String taskId;

    public MyTask() {
        this.taskId = String.valueOf(SnowFlakeUtil.nextId());
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
    public FutureTaskResult call() throws Exception {
        boolean b = true;
//                if (b) {
//                    throw new RuntimeException("测试");
//                }
        System.out.println("任务执行的当前时间 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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

    @Override
    public String taskId() {
        return this.taskId;
    }
}
