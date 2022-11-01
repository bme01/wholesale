package edu.teamv.utils.datasource;

import java.util.Properties;

public interface DataSource {
    Properties jdbcProperties = new Properties();
    Properties poolProperties = new Properties();
    static int MAXIMUM_POOL_SIZE = 10;
    // Connection getConnection() throws IOException, SQLException;
}
