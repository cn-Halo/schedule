package com.yzm.schedule.persistence;

//import javax.persistence.Entity;
//import javax.sql.DataSource;

import com.yzm.schedule.api.DelayTask;
import com.yzm.schedule.api.RetryTask;
import io.github.halo.snowFlake.SnowFlakeUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/11/26.
 *
 * @author yzm
 */
public class DelayTaskDao {
    private JdbcTemplate jdbcTemplate;
    private String executorName;
    private static final String TABLE_NAME = "sch_delay_task";
    private static final String CREATE_TABLE_SQl = "create table " + TABLE_NAME +
            "(id varchar(255) primary key, executorName varchar(255),taskId varchar(255) , delayTimes varchar(255) , " +
            "timeUnit varchar(255), dtIndex int,createTime varchar(255) ,methodReturnType varchar(255) ," +
            "methodName varchar(255) ,taskClassName varchar(255) )";
    private static final String INSERT_SQL_FORMAT = "insert into " + TABLE_NAME +
            "(id,executorName,taskId,delayTimes,timeUnit,dtIndex,createTime,methodReturnType,methodName,taskClassName) " +
            "values('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');";


    public DelayTaskDao(String executorUniqueName, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.executorName = executorUniqueName;
        initTable();
    }


    /**
     * 加载任务中断的持久化任务。
     *
     * @return
     */
    public List<RetryTask> loadTask() {
        String sql = "select * from  " + TABLE_NAME;
        List<Map<String, Object>> list = jdbcTemplate.queryForListMap(sql);
        List<RetryTask> delayTaskList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> data = list.get(i);

            String[] delayTimes = String.valueOf(data.get("delayTimes")).split(",");
            long[] dts = new long[delayTimes.length];
            long timestamp = new Date().getTime();
            for (int i1 = 0; i1 < delayTimes.length; i1++) {
                long dt = Long.valueOf(delayTimes[i1]) - timestamp < 0 ? 0 : Long.valueOf(delayTimes[i1]) - timestamp;
                dts[i1] = dt;
            }

            String taskId = String.valueOf(data.get("taskId"));
            String timeUnit = String.valueOf(data.get("timeUnit"));
            int dtIndex = Integer.valueOf(String.valueOf(data.get("dtIndex")));
            try {
                Class taskClass = Class.forName(String.valueOf(data.get("taskClassName")));
                Class methodReturnType = Class.forName(String.valueOf(data.get("methodReturnType")));
                String methodName = String.valueOf(data.get("methodName"));
                MethodType methodType = MethodType.methodType(methodReturnType);
                MethodHandle callMethodHandle = MethodHandles.lookup().findVirtual(taskClass, methodName, methodType).bindTo(taskClass.newInstance());

                PersistDelayTask persistDelayTask = new PersistDelayTask(taskId, dts, TimeUnit.valueOf(timeUnit), dtIndex, callMethodHandle);
                delayTaskList.add(persistDelayTask);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return delayTaskList;
    }


    public void initTable() {
        boolean rs = jdbcTemplate.isTableExist(TABLE_NAME);
        if (!rs) {
            jdbcTemplate.execute(CREATE_TABLE_SQl);
        }

    }

//    public void save(List<DelayTask> tasks) {
//
//        String[] sqls = new String[tasks.size()];
//
//        for (int i = 0; i < tasks.size(); i++) {
//            DelayTask task = tasks.get(i);
//            StringBuffer dtBuffer = new StringBuffer();
//            for (long delayTime : task.delayTimes()) {
//                dtBuffer.append(",").append(delayTime);
//            }
//            String sql = String.format(INSERT_SQL_FORMAT, executorName, task.taskId(), dtBuffer.toString().substring(1), task.timeUnit().name(), task.dtIndex(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            System.out.println("sql：" + sql);
//            sqls[i] = sql;
//        }
//        jdbcTemplate.executeBatch(sqls);
//    }


    public void save(DelayTask task) {
        DelayTask originTask = task.originTask();
        Method method = null;
        String methodReturnType = null;
        String methodName = null;
        String taskClassName = null;
        try {
            method = originTask.getClass().getMethod("call");
            methodReturnType = method.getReturnType().getName();
            methodName = method.getName();
            taskClassName = originTask.getClass().getName();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        StringBuffer dtBuffer = new StringBuffer();
        for (long delayTime : task.delayTimes()) {
            dtBuffer.append(",").append(delayTime);
        }
        String sql = String.format(INSERT_SQL_FORMAT, SnowFlakeUtil.nextId(), executorName, task.taskId(), dtBuffer.toString().substring(1),
                task.timeUnit().name(), task.dtIndex(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                , methodReturnType, methodName, taskClassName);
        System.out.println("sql：" + sql);

        jdbcTemplate.executeBatch(sql);
    }

    public void delete(String taskId) {
        String sql = String.format("delete from %s where executorName = '%s' and  taskId = '%s'", TABLE_NAME, executorName, taskId);
        System.out.println("sql：" + sql);
        jdbcTemplate.execute(sql);
    }


}
