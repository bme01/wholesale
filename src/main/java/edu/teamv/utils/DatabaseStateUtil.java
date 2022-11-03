package edu.teamv.utils;

import edu.teamv.utils.datasource.impl.YugabyteDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseStateUtil {

    private static Connection connection = null;

    public static void reportFinalState() throws SQLException, IOException {
        connection = YugabyteDataSource.getConnection();

        String warehouseSql = "select sum(w_ytd) from wholesale.warehouse;";

        PreparedStatement w = connection.prepareStatement(warehouseSql);
        ResultSet resultSetW = w.executeQuery();
        if (resultSetW.next()) {
            System.out.println(resultSetW.getInt(1));
        }
        w.close();


        String districtSql = "select sum(d_ytd), sum(d_next_oid) from wholesale.district;";

        PreparedStatement d = connection.prepareStatement(districtSql);
        ResultSet resultSetD = d.executeQuery();
        if (resultSetD.next()) {
            System.out.println(resultSetD.getInt(1));
            System.out.println(resultSetD.getInt(2));
        }
        d.close();

        String customerSql = "select sum(c_balance), sum(c_ytd_payment), sum(c_payment_cnt), sum(c_delivery) from wholesale.customer;";

        PreparedStatement c = connection.prepareStatement(customerSql);
        ResultSet resultSetC = c.executeQuery();
        if (resultSetC.next()) {
            System.out.println(resultSetC.getBigDecimal(1));
            System.out.println(resultSetC.getBigDecimal(2));
            System.out.println(resultSetC.getInt(3));
            System.out.println(resultSetC.getInt(4));
        }
        c.close();

        String orderSql = "select max(o_id), sum(o_ol_cnt) from wholesale.order;";

        PreparedStatement o = connection.prepareStatement(orderSql);
        ResultSet resultSetO = o.executeQuery();
        if (resultSetO.next()) {
            System.out.println(resultSetO.getInt(1));
            System.out.println(resultSetO.getInt(2));
        }
        o.close();

        String orderLineSql = "select sum(ol_amount), sum(ol_quantity) from wholesale.order_line;";
        PreparedStatement oL = connection.prepareStatement(orderLineSql);
        ResultSet resultSetOL = oL.executeQuery();
        if (resultSetOL.next()) {
            System.out.println(resultSetOL.getBigDecimal(1));
            System.out.println(resultSetOL.getInt(2));
        }
        oL.close();

        String stockSql = "select sum(s_quantity), sum(s_ytd), sum(s_ordercnt), sum(s_remote_cnt) from wholesale.stock;";

        PreparedStatement s = connection.prepareStatement(stockSql);
        ResultSet resultSetS = s.executeQuery();
        if (resultSetS.next()) {
            System.out.println(resultSetS.getInt(1));
            System.out.println(resultSetS.getBigDecimal(2));
            System.out.println(resultSetS.getInt(3));
            System.out.println(resultSetS.getInt(4));
        }
        c.close();
    }

}
