package edu.teamv.utils;

import edu.teamv.utils.datasource.impl.PgDataSource;
import edu.teamv.utils.datasource.impl.YugabyteDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoadDataUtil {
    public static void loadDataToNTD() throws SQLException, IOException, ClassNotFoundException {
        String sql = "insert into wholesale.next_to_deliver_order \n" +
                "select o_w_id, o_d_id, o_id from wholesale.order \n" +
                "where o_w_id = ? and o_d_id = ? and o_carrier_id is null \n" +
                "order by o_id \n" +
                "limit 1";
        Connection connection = YugabyteDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <=10; j++) {
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, j);
                preparedStatement.execute();
            }
        }
        preparedStatement.close();
        connection.close();
    }

}
