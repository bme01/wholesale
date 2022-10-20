package edu.teamv.datasource;

import com.zaxxer.hikari.HikariConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public interface DataSource {
    Properties databaseProperties = new Properties();
    Properties poolProperties = new Properties();
    static int MAXIMUM_POOL_SIZE = 10;
    // Connection getConnection() throws IOException, SQLException;
}
