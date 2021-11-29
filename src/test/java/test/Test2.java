package test;

import com.yzm.schedule.api.DelayTask;

/**
 * @author yzm
 * @date 2021/11/28 13:08
 */
public class Test2 {

    public static void main(String[] args) throws Exception {
        DelayTask delayTask = null;
        delayTask.call();
        delayTask.getClass().getMethod("",null).getName();

//        System.out.println(DelayTask.class.toString());
    }
}
