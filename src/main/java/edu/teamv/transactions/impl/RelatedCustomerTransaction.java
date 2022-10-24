package edu.teamv.transactions.impl;

import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.sql.SQLException;

public class RelatedCustomerTransaction extends Transaction {
    public RelatedCustomerTransaction(String[] customerIdentify)
            throws SQLException, IOException, ClassNotFoundException {
        super(customerIdentify);
    }

    @Override
    public void execute() {
        System.out.println("RelatedCustomerTransaction");
    }
}
