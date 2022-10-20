package edu.teamv.datasource.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.teamv.datasource.DataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
/*
 * data source for pg db
 * */

public class PgDataSource implements DataSource {
    public static Connection getConnection() throws IOException, SQLException, ClassNotFoundException {
        databaseProperties.load(new FileInputStream("src/main/resources/postgresql.properties"));
        System.out.println(databaseProperties.toString());

        poolProperties.setProperty("dataSourceClassName", databaseProperties.getProperty("driver"));
        poolProperties.setProperty("jdbcUrl", databaseProperties.getProperty("jdbcurl"));
        // The pool will create  10 connections to the servers
        poolProperties.setProperty("maximumPoolSize", String.valueOf(MAXIMUM_POOL_SIZE));
        poolProperties.setProperty("dataSource.serverName", databaseProperties.getProperty("servername"));
        poolProperties.setProperty("dataSource.portNumber", databaseProperties.getProperty("port"));
        poolProperties.setProperty("dataSource.databaseName", databaseProperties.getProperty("dbname"));
        poolProperties.setProperty("dataSource.user", databaseProperties.getProperty("user"));
        poolProperties.setProperty("dataSource.password", databaseProperties.getProperty("password"));
        System.out.println(poolProperties.toString());

        HikariConfig hikariconfig = new HikariConfig(poolProperties);
        hikariconfig.validate();

        HikariDataSource hikariDataSource = new HikariDataSource(hikariconfig);
        return hikariDataSource.getConnection();
    }
}
