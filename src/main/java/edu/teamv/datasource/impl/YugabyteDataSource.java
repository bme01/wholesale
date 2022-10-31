package edu.teamv.datasource.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.teamv.datasource.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

/*
 * data source for yugabyte db
 * */

public class YugabyteDataSource implements DataSource {

    private static final HikariDataSource hikariDataSource;


    static {
        try {
            InputStream inputStream = YugabyteDataSource.class.getClassLoader().getResourceAsStream("app.properties");
            jdbcProperties.load(inputStream);

            poolProperties.setProperty("dataSourceClassName", jdbcProperties.getProperty("driver"));
            // The pool will create  10 connections to the servers
            poolProperties.setProperty("maximumPoolSize", jdbcProperties.getProperty("poolSize"));
            poolProperties.setProperty("dataSource.serverName", jdbcProperties.getProperty("host"));
            poolProperties.setProperty("dataSource.portNumber", jdbcProperties.getProperty("port"));
            poolProperties.setProperty("dataSource.databaseName", jdbcProperties.getProperty("dbName"));
            poolProperties.setProperty("dataSource.user", jdbcProperties.getProperty("dbUser"));
            poolProperties.setProperty("dataSource.password", jdbcProperties.getProperty("dbPassword"));
            poolProperties.setProperty("dataSource.additionalEndpoints", jdbcProperties.getProperty("additionalEndPoints"));

            HikariConfig hikariconfig = new HikariConfig(poolProperties);
            hikariconfig.validate();

            hikariDataSource = new HikariDataSource(hikariconfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // @Override
    public static Connection getConnection() throws IOException, SQLException {


        return hikariDataSource.getConnection();
    }
}
