package test;

import com.yzm.schedule.api.JDKScheduleExecutor;
import com.yzm.schedule.api.ScheduleExecutor;
import io.github.halo.jdbc.C3p0DataSourceMetaData;
import io.github.halo.jdbc.JdbcTemplate;

import java.beans.PropertyVetoException;

/**
 * Created on 2021/11/26.
 *
 * @author yzm
 */
public class PersistTest {


    public static void main(String[] args) throws PropertyVetoException {

//        EntityManagerFactory sessionFactory = Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa");
//        System.out.println(sessionFactory);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(new C3p0DataSourceMetaData() {
            @Override
            public String driverClassName() {
                return "com.mysql.jdbc.Driver";
            }

            @Override
            public String jdbcUrl() {
//                return "jdbc:mysql://localhost:3306/schedule?useUnicode=true&characterEncoding=UTF-8";
                return "jdbc:mysql://192.168.1.209:13306/schedule?useUnicode=true&characterEncoding=UTF-8";

            }

            @Override
            public String username() {
                return "root";
            }

            @Override
            public String password() {
                return "123";
            }
        });

        ScheduleExecutor executor = new JDKScheduleExecutor("yzm", jdbcTemplate);

        MyTask myTask = new MyTask();
        executor.execute(myTask);

//        executor.execute(new RetryTask<FutureTaskResult>() {
//            @Override
//            public long[] delayTimes() {
//                return new long[]{2l, 4l, 6l};
//            }
//
//            @Override
//            public TimeUnit timeUnit() {
//                return TimeUnit.SECONDS;
//            }
//
//            @Override
//            public int dtIndex() {
//                return 0;
//            }
//
//            @Override
//            public Object attach() {
//                return null;
//            }
//
//            @Override
//            public FutureTaskResult call() {
//                boolean b = true;
////                if (b) {
////                    throw new RuntimeException("测试");
////                }
//                System.out.println("当前时间 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                return new FutureTaskResult() {
//                    @Override
//                    public boolean success() {
//                        double d = Math.random();
//                        System.out.println(d);
//                        return d > 0.8 ? true : false;
//                    }
//
//                    @Override
//                    public Object data() {
//                        return null;
//                    }
//
//                };
//            }
//        });


    }


}
