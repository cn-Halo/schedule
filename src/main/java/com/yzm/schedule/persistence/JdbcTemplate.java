package com.yzm.schedule.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2021/11/26.
 *
 * @author yzm
 */
public class JdbcTemplate {

    private DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JdbcTemplate(C3p0DataSourceMetaData metaData) throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(metaData.driverClassName());
        dataSource.setJdbcUrl(metaData.jdbcUrl());
        dataSource.setUser(metaData.username());
        dataSource.setPassword(metaData.password());
        this.dataSource = dataSource;
    }

    public <T> T execute(ConnectionCallback<T> callback) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return callback.doInConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

    }


    public <T> T execute(StatementCallback<T> callback) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            T t = callback.doInStatement(statement);
            connection.commit();
            return t;
        } catch (SQLException e) {
            e.printStackTrace();
            Exception ee = e;
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    ee = throwables;
                }
            }
            throw new RuntimeException(ee);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

    }

    public List<Map<String, Object>> queryForListMap(String sql) {
        return execute(new StatementCallback<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> doInStatement(Statement statement) throws SQLException {
                ResultSet rs = statement.executeQuery(sql);


                ResultSetMetaData rsmd = rs.getMetaData();
                String[] names = new String[rsmd.getColumnCount()];
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
//                    rsmd.getColumnType(i + 1);
                    String name = rsmd.getColumnName(i + 1);
                    names[i] = name;
                }

                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map map = new HashMap();
                    for (int i = 0; i < names.length; i++) {
                        map.put(names[i], rs.getObject(i + 1));
                    }
                    list.add(map);
                }
                return list;
            }
        });
    }


    public boolean execute(String sql) {
        return execute(new StatementCallback<Boolean>() {
            @Override
            public Boolean doInStatement(Statement statement) throws SQLException {
                return statement.execute(sql);
            }
        });
    }

    public ResultSet executeQuery(String sql) {
        return execute(new StatementCallback<ResultSet>() {
            @Override
            public ResultSet doInStatement(Statement statement) throws SQLException {
                ResultSet rs = statement.executeQuery(sql);
                return rs;
            }
        });
    }

    public int[] executeBatch(final String... sql) {
        return execute(new StatementCallback<int[]>() {
            @Override
            public int[] doInStatement(Statement statement) throws SQLException {
                for (String s : sql) {
                    statement.addBatch(s);
                }
                int[] r = statement.executeBatch();
                return r;
            }
        });
    }


    public boolean isTableExist(String tableName) {
        return execute(new ConnectionCallback<Boolean>() {
            @Override
            public Boolean doInConnection(Connection connection) throws SQLException {
                ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null);
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }

            }
        });
    }


}
