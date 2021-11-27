package com.yzm.schedule.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created on 2021/11/26.
 *
 * @author yzm
 */
public interface ConnectionCallback<T> {


    T doInConnection(Connection connection) throws SQLException;



}
