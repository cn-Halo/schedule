package test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.github.halo.jdbc.JdbcTemplate;

import java.beans.PropertyVetoException;
import java.util.List;

/**
 * Created on 2021/11/27.
 *
 * @author yzm
 */
public class JdbcTemplateTest {


    public static void main(String[] args) throws PropertyVetoException {

        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://192.168.1.209:13306/event?useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("123");


        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        List list = jdbcTemplate.queryForListMap("select * from t_outbox");

        System.out.println(list);

        List list2 = jdbcTemplate.queryForListMap("select * from t_order");

        System.out.println(list2);


    }
}
