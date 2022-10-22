package edu.teamv.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class PreparedStatementUtil {

    public static PreparedStatement getPreparedStatement(Connection connection, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 1; i <= parameters.size(); i++) {
            Object parameter = parameters.get(i - 1);

            if (parameter instanceof Integer) {
                preparedStatement.setInt(i, (Integer) parameter);
            }

            if (parameter instanceof String) {
                preparedStatement.setString(i, (String) parameter);
            }

            if (parameter instanceof BigDecimal) {
                preparedStatement.setBigDecimal(i, (BigDecimal) parameter);
            }

            if (parameter instanceof Float) {
                preparedStatement.setFloat(i, (Float) parameter);
            }

            if (parameter instanceof Timestamp) {
                preparedStatement.setTimestamp(i, (Timestamp) parameter);
            }
        }
        return preparedStatement;
    }
}
