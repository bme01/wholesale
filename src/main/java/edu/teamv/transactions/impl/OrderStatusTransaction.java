package edu.teamv.transactions.impl;

import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.sql.SQLException;

public class OrderStatusTransaction extends Transaction {

    public OrderStatusTransaction(String[] customerIdentify)
            throws SQLException, IOException, ClassNotFoundException {
        super(customerIdentify);
    }

    @Override
    public void execute() {
        System.out.println("OrderStatusTransaction");
    }
}
