package edu.teamv;

import edu.teamv.datasource.impl.PgDataSource;
import edu.teamv.pojo.Order;
import org.junit.Test;
import org.postgresql.jdbc.PgConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PojoTest {
    private Connection connection = PgDataSource.getConnection();
    @Test
    public void OderTest() throws SQLException {
        String sql = "select * from wholesale.order limit 1";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Order order = new Order();
        while (resultSet.next()) {
            order.setWareHouseID(resultSet.getInt(1));
            order.setDistrictID(resultSet.getInt(2));
            order.setOrderID(resultSet.getInt(3));
            order.setCustomerID(resultSet.getInt(4));
            order.setCarrierID(resultSet.getInt(5));
            order.setOrderLineCount(resultSet.getInt(6));
            order.setOrderStatus(resultSet.getInt(7));
            order.setOrderEntry(resultSet.getTimestamp(8));
        }
        System.out.println(order.toString());

    }

    public PojoTest() throws SQLException, IOException, ClassNotFoundException {

    }
}
