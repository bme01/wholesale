package edu.teamv.transactions;

import com.zaxxer.hikari.HikariDataSource;
import edu.teamv.datasource.impl.PgDataSource;
import edu.teamv.datasource.impl.YugabyteDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Transaction {
    private String[] parameters;

    private Connection connection;

    public Transaction(String[] parameters) throws SQLException, IOException, ClassNotFoundException {
        this.parameters = parameters;
        connection = YugabyteDataSource.getConnection();
        connection.setAutoCommit(false);
    }

    public Transaction() {
    }

    public abstract void execute() throws SQLException;

    public Connection getConnection() {
        return connection;
    }
}
