package test;

import com.yzm.schedule.api.DelayTask;

import java.util.Arrays;

/**
 * Created on 2021/11/27.
 *
 * @author yzm
 */
public class TaskUtil {


    public static String delayTaskToString(DelayTask delayTask) {
        String ss = "{ \n\t" + Arrays.asList(delayTask.delayTimes()).stream().map(o -> o + ",").toString()
                + "\n\t " + delayTask.timeUnit().name()
                + "\n\t" + delayTask.dtIndex()
                + "\n\t" + delayTask.attach()
                + "}";

        return ss;

    }


}
