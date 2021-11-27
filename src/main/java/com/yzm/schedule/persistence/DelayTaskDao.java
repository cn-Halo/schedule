package com.yzm.schedule.persistence;

//import javax.persistence.Entity;
//import javax.sql.DataSource;

import com.yzm.schedule.api.DelayTask;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 2021/11/26.
 *
 * @author yzm
 */
public class DelayTaskDao {
    private JdbcTemplate jdbcTemplate;

    public DelayTaskDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        init();
    }

    public void init() {
        String sql = "create table sch_delay_task( taskId varchar(255) primary key, delayTimes varchar(255) , timeUnit varchar(255), dtIndex int )";
        jdbcTemplate.execute(sql);
    }

    public void save(List<DelayTask> tasks) {
        String sqlFormat = "insert into sch_delay_task(taskId,delayTimes,timeUnit,dtIndex) values( '%s','%s','%s','%s');";
        String[] sqls = new String[tasks.size()];

        for (int i = 0; i < tasks.size(); i++) {
            DelayTask task = tasks.get(i);
            StringBuffer dtBuffer = new StringBuffer();
            for (long delayTime : task.delayTimes()) {
                dtBuffer.append(",").append(delayTime);
            }
            String sql = String.format(sqlFormat, task.taskId(), dtBuffer.toString().substring(1), task.timeUnit().toString(), task.dtIndex());
            sqls[i] = sql;
        }
        jdbcTemplate.executeBatch(sqls);
    }


    public void save(DelayTask task) {
        save(Arrays.asList(task));
    }

    public void delete(String taskId) {
        String sql = String.format("delete from sch_delay_task where taskId = '%s'", taskId);
        jdbcTemplate.execute(sql);
    }


}
