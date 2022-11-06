package edu.teamv.transactions.impl;

import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderStatusTransaction extends Transaction {
    private final Integer customerWarehouseID;
    private final Integer customerDistrictID;
    private final Integer customerID;

    Connection connection;

    public OrderStatusTransaction(String[] customerIdentify)
            throws SQLException, IOException, ClassNotFoundException {
        super(customerIdentify);
        customerWarehouseID = Integer.parseInt(customerIdentify[0]);
        customerDistrictID = Integer.parseInt(customerIdentify[1]);
        customerID = Integer.parseInt(customerIdentify[2]);
        connection = super.getConnection();

    }

    @Override
    public void execute() throws SQLException {
        try {
            System.out.println("======Order Status Transaction======");
            printCustomerInfo();
            Integer orderId = printAndGetOrderInfo();
            printOrderLine(orderId);

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new SQLException();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }




    }


    private void printCustomerInfo() throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String getCustomerInfoSql = "select c_first, c_middle, c_last, c_balance from wholesale.customer \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?";
        preparedStatement = connection.prepareStatement(getCustomerInfoSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.setInt(3, customerID);
        resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            System.out.println("Customer’s name " + resultSet.getString(1) +
                    " " + resultSet.getString(2) +
                    " " + resultSet.getString(3) +
                    ", balance: " + resultSet.getBigDecimal(4));
        }
        preparedStatement.close();
    }


    private Integer printAndGetOrderInfo() throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String getCustomerInfoSql = "select o_id, o_entry_d, o_carrier_id from wholesale.\"order\" \n" +
                "where o_w_id = ? and o_d_id = ? and o_c_id = ? \n" +
                "and o_id = (select max(o_id) from wholesale.\"order\" \n" +
                " where o_w_id = ? and o_d_id = ? and o_c_id = ?)";
        preparedStatement = connection.prepareStatement(getCustomerInfoSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.setInt(3, customerID);
        preparedStatement.setInt(4, customerWarehouseID);
        preparedStatement.setInt(5, customerDistrictID);
        preparedStatement.setInt(6, customerID);
        resultSet = preparedStatement.executeQuery();

        if(resultSet.next()){
            System.out.println("Order number " + resultSet.getInt(1) +
                    ", Entry date and time " + resultSet.getTimestamp(2) +
                    ", Carrier identifier " + resultSet.getObject(3));
            Integer res = resultSet.getInt(1);
            preparedStatement.close();
            return res;
        }
        preparedStatement.close();
        return 0;
    }

    private void printOrderLine(Integer orderId) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String getCustomerInfoSql = "select ol_i_id, ol_supply_w_id, ol_quantity, ol_amount, ol_delivery_d" +
                " from wholesale.order_line \n" +
                " where ol_w_id = ? and ol_d_id = ? and ol_o_id = ? ";
        preparedStatement = connection.prepareStatement(getCustomerInfoSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.setInt(3, orderId);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println(" Item number " + resultSet.getInt(1) +
                    ",  Supplying warehouse number " + resultSet.getInt(2) +
                    ",  Quantity ordered " + resultSet.getInt(3) +
                    ",  Total price for ordered item " + resultSet.getInt(4) +
                    ",  Data and time of delivery " + resultSet.getObject(5));
        }
        preparedStatement.close();
    }

}
