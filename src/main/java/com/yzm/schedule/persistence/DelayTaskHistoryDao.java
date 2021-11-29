package com.yzm.schedule.persistence;

import com.yzm.schedule.api.DelayTask;
import io.github.halo.snowFlake.SnowFlakeUtil;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author yzm
 * @date 2021/11/28 22:37
 */
public class DelayTaskHistoryDao {

    private JdbcTemplate jdbcTemplate;
    private String executorName;
    private static final String TABLE_NAME = "sch_delay_task_his";
    private static final String CREATE_TABLE_SQl = "create table " + TABLE_NAME +
            "(id varchar(255) primary key, executorName varchar(255),taskId varchar(255) , delayTimes varchar(255) , " +
            "timeUnit varchar(255), dtIndex int,createTime varchar(255) ,methodReturnType varchar(255) ," +
            "methodName varchar(255) ,taskClassName varchar(255) )";
    private static final String INSERT_SQL_FORMAT = "insert into " + TABLE_NAME +
            "(id,executorName,taskId,delayTimes,timeUnit,dtIndex,createTime,methodReturnType,methodName,taskClassName) " +
            "values('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');";

    private static final String CREATE_INDEX_SQL = "CREATE INDEX index_taskId ON " + TABLE_NAME + " (taskId)";


    public DelayTaskHistoryDao(String executorUniqueName, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.executorName = executorUniqueName;
        initTable();
    }

    public void initTable() {
        boolean rs = jdbcTemplate.isTableExist(TABLE_NAME);
        if (!rs) {
            jdbcTemplate.execute(CREATE_TABLE_SQl);
            System.out.println("sql："+CREATE_TABLE_SQl);
            jdbcTemplate.execute(CREATE_INDEX_SQL);
            System.out.println("sql："+CREATE_INDEX_SQL);
        }
    }

    public void save(DelayTask task) {
        String sqlFormat = "select * from " + TABLE_NAME + " where executorName = '%s' and taskId = '%s' and  dtIndex = '%s' ";
        String querySql = String.format(sqlFormat, executorName, task.taskId(), task.dtIndex());
        List queryList = jdbcTemplate.queryForListMap(querySql);
        if (queryList.size() > 0) {
            return;
        }

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

    public List<Map<String, Object>> queryByTaskId(String taskId) {
        String sqlFormat = "select * from " + TABLE_NAME + " where  executorName = '%s' and taskId = '%s' order by id asc";
        String sql = String.format(sqlFormat, executorName, taskId);
        return jdbcTemplate.queryForListMap(sql);
    }


}
