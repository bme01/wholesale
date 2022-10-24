package edu.teamv.transactions.impl;

import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.sql.SQLException;

public class StockLevelTransaction extends Transaction {
    public StockLevelTransaction(String[] parameters)
            throws SQLException, IOException, ClassNotFoundException {
        super(parameters);
    }

    @Override
    public void execute() {
        System.out.println("StockLevelTransaction");
    }
}
