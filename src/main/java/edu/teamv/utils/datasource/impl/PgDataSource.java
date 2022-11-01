package edu.teamv.utils.datasource.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.teamv.utils.datasource.DataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
/*
 * data source for pg db
 * */

public class PgDataSource implements DataSource {
    public static Connection getConnection() throws IOException, SQLException, ClassNotFoundException {

        jdbcProperties.load(new FileInputStream("src/main/resources/postgresql.properties"));

        poolProperties.setProperty("dataSourceClassName", jdbcProperties.getProperty("driver"));
        poolProperties.setProperty("jdbcUrl", jdbcProperties.getProperty("jdbcurl"));
        // The pool will create  10 connections to the servers
        poolProperties.setProperty("maximumPoolSize", String.valueOf(MAXIMUM_POOL_SIZE));
        poolProperties.setProperty("dataSource.serverName", jdbcProperties.getProperty("servername"));
        poolProperties.setProperty("dataSource.portNumber", jdbcProperties.getProperty("port"));
        poolProperties.setProperty("dataSource.databaseName", jdbcProperties.getProperty("dbname"));
        poolProperties.setProperty("dataSource.user", jdbcProperties.getProperty("user"));
        poolProperties.setProperty("dataSource.password", jdbcProperties.getProperty("password"));

        HikariConfig hikariconfig = new HikariConfig(poolProperties);
        hikariconfig.validate();

        HikariDataSource hikariDataSource = new HikariDataSource(hikariconfig);
        return hikariDataSource.getConnection();
    }
}
