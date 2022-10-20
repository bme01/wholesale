package edu.teamv;

import edu.teamv.datasource.impl.PgDataSource;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class ConnectionTest {

    @Test
    public void PgJdbcTest() throws IOException, SQLException {
        Properties properties = new Properties();
        // System.out.println(System.getProperty("user.dir"));
        properties.load(new FileInputStream("src/test/resources/postgresql.properties"));
        System.out.println(properties.getProperty("user") + properties.getProperty("password"));
        Connection connection = DriverManager.getConnection(properties.getProperty("jdbcurl"), properties);
        String sql = "select * from wholesale.warehouse";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            int id = resultSet.getInt(1);
            String name = resultSet.getString(2);
            System.out.println("id: " + id + " " + "name: " + name);
        }
    }

    @Test
    public void DataSourceTest() throws SQLException, IOException, ClassNotFoundException {
        Connection connection = PgDataSource.getConnection();
        String sql = "select * from wholesale.warehouse";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            int id = resultSet.getInt(1);
            String name = resultSet.getString(2);
            System.out.println("id: " + id + " " + "name: " + name);
        }
    }
}
