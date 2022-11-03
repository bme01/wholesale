package edu.teamv.transactions;

import edu.teamv.utils.datasource.impl.PgDataSource;
import edu.teamv.utils.datasource.impl.YugabyteDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Transaction {
    private String[] parameters;

    private Connection connection;

    public Transaction(String[] parameters) throws SQLException, IOException, ClassNotFoundException {
        this.parameters = parameters;
        connection = PgDataSource.getConnection();
        connection.setAutoCommit(false);
    }

    public Transaction() {
    }

    public abstract void execute() throws SQLException;

    public Connection getConnection() {
        return connection;
    }
}
