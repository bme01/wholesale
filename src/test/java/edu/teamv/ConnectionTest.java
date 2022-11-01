package edu.teamv;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.teamv.utils.datasource.impl.PgDataSource;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class ConnectionTest {

    @Test
    public void pgJdbcTest() throws IOException, SQLException {
        Properties properties = new Properties();
        // System.out.println(System.getProperty("user.dir"));
        properties.load(new FileInputStream("src/test/resources/postgresql.properties"));
        System.out.println(properties.getProperty("user") + properties.getProperty("password"));
        Connection connection = DriverManager.getConnection(properties.getProperty("jdbcurl"), properties);
        String sql = "select * from wholesale.warehouse";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String name = resultSet.getString(2);
            System.out.println("id: " + id + " " + "name: " + name);
        }
    }

    @Test
    public void dataSourceTest() throws SQLException, IOException, ClassNotFoundException {
        Connection connection = PgDataSource.getConnection();
        String sql = "select * from wholesale.warehouse";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String name = resultSet.getString(2);
            System.out.println("id: " + id + " " + "name: " + name);
        }
    }

    // @Test
    // public void yugabyteDataSourseTest1() {
    //     try {
    //         Connection connection = YugabyteDataSource.getConnection();
    //         String sql = "select * from wholesale.warehouse";
    //         Statement statement = connection.createStatement();
    //         ResultSet resultSet = statement.executeQuery(sql);
    //         while (resultSet.next()) {
    //             int id = resultSet.getInt(1);
    //             String name = resultSet.getString(2);
    //             System.out.println("id: " + id + " " + "name: " + name);
    //         }
    //
    //     } catch (IOException | SQLException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    @Test
    public void yugabyteDataSourseTest2() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("dataSource.databaseName", "cs5424_ysql");
        Properties poolProperties = new Properties();
        poolProperties.setProperty("dataSourceClassName", "com.yugabyte.ysql.YBClusterAwareDataSource");
        poolProperties.setProperty("maximumPoolSize", String.valueOf(10));
        poolProperties.setProperty("dataSource.serverName", "192.168.51.10");
        poolProperties.setProperty("dataSource.portNumber", "5434");
        poolProperties.setProperty("dataSource.databaseName", properties.getProperty("dataSource.databaseName"));
        poolProperties.setProperty("dataSource.user", "yugabyte");
        poolProperties.setProperty("dataSource.password", "yugabyte");
// If you want to provide additional end points
        String additionalEndpoints = "192.168.51.11:5434,192.168.51.9:5434, 192.168.51.8:5434";
        poolProperties.setProperty("dataSource.additionalEndpoints", additionalEndpoints);
        System.out.println(poolProperties);
// If you want to load balance between specific geo locations using topology keys
//         String geoLocations = "cloud1.region1.zone1,cloud1.region2.zone2";
//         poolProperties.setProperty("dataSource.topologyKeys", geoLocations);

        HikariConfig config = new HikariConfig(poolProperties);
        config.validate();
        HikariDataSource ds = new HikariDataSource(config);

        Connection connection = ds.getConnection();
        String sql = "select * from wholesale.warehouse";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String name = resultSet.getString(2);
            System.out.println("id: " + id + " " + "name: " + name);
        }
    }
}
