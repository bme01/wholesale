package edu.teamv.transactions.impl;

import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class NewOrderTransaction extends Transaction {
    private final Integer customerWarehouseID;

    private final Integer customerDistrictID;

    private final Integer customerID;

    private final Integer numberOfItems;
    ArrayList<String[]> itemsInfoList;
    Connection connection;

    public NewOrderTransaction(String[] customerIdentify, ArrayList<String[]> itemsInfoList)
            throws SQLException, IOException, ClassNotFoundException {
        super(customerIdentify);
        this.customerID = Integer.parseInt(customerIdentify[0]);
        this.customerWarehouseID = Integer.parseInt(customerIdentify[1]);
        this.customerDistrictID = Integer.parseInt(customerIdentify[2]);
        this.numberOfItems = Integer.parseInt(customerIdentify[3]);

        this.itemsInfoList = itemsInfoList;
        this.connection = super.getConnection();
    }

    @Override
    public void execute() throws SQLException {
        System.out.println("NewOrderTransaction");
        connection.close();
    }
}
