package edu.teamv;

import edu.teamv.utils.DatabaseStateUtil;
import edu.teamv.utils.LoadDataUtil;
import edu.teamv.utils.datasource.impl.PgDataSource;
import edu.teamv.utils.datasource.impl.YugabyteDataSource;
import edu.teamv.transactions.Transaction;
import edu.teamv.transactions.impl.DeliveryTransaction;
import edu.teamv.transactions.impl.PaymentTransaction;
import edu.teamv.utils.PerformanceMeasurementUtil;
import edu.teamv.utils.PreparedStatementUtil;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UtilTest {
    @Test
    public void test1() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp.toString());

    }

    @Test
    public void preparedStatementTest() throws SQLException, IOException, ClassNotFoundException {
        String getOrderInfoSql = "select o_id, o_c_id from wholesale.order\n" +
                "where o_w_id = ? and o_d_id = ?\n" +
                "and o_id = (select MIN(o_id) from wholesale.order \n" +
                "where o_w_id = ? and o_d_id = ?\n" +
                "and order.o_carrier_id is NULL);";
        List<Object> list = new ArrayList<>();
        Integer o_w_id = 1;
        Integer o_d_id = 1;
        list.add(o_w_id);
        list.add(o_d_id);
        list.add(o_w_id);
        list.add(o_d_id);

        Connection connection = PgDataSource.getConnection();
        PreparedStatement preparedStatement = PreparedStatementUtil.getPreparedStatement(connection, getOrderInfoSql, list);
        System.out.println(preparedStatement.toString());
    }

    @Test
    public void performanceMeasurementUtilTest() throws SQLException, IOException, ClassNotFoundException {
        Transaction deliveryTransaction = new DeliveryTransaction(new String[]{"1", "1"});
        Transaction paymentTransaction = new PaymentTransaction(new String[]{"1", "1", "1", "100.98"});
        // PerformanceMeasurementUtil.run(deliveryTransaction::execute);
        // PerformanceMeasurementUtil.run(paymentTransaction::execute);
        PerformanceMeasurementUtil.performanceTest(deliveryTransaction);
        PerformanceMeasurementUtil.performanceTest(paymentTransaction);
        PerformanceMeasurementUtil.detailedReport();
        PerformanceMeasurementUtil.report();
    }

    @Test
    public void resourceTest() throws IOException {
        InputStream inputStream = new YugabyteDataSource().getClass().getClassLoader().getResourceAsStream("app.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        System.out.println(properties);
    }

    @Test
    public void loadDataTest () throws SQLException, IOException, ClassNotFoundException {
        LoadDataUtil.loadDataToNTD();
    }

    @Test
    public void dbStateTest () throws SQLException, IOException {
        DatabaseStateUtil.reportFinalState();
    }
}
