package test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import sun.misc.Unsafe;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

/**
 * Created on 2021/11/27.
 *
 * @author yzm
 */
public class C3p0Test {


    public static void main(String[] args) throws PropertyVetoException, SQLException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://192.168.1.209:13306/event?useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("123");

        System.out.println(dataSource.getConnection());

        Unsafe unsafe = Unsafe.getUnsafe();
        System.out.println(unsafe);
    }


}
