package edu.teamv.transactions.impl;

import edu.teamv.pojo.Order;
import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Delivery extends Transaction {

    private final Integer warehouseID;
    private final Integer carrierID;

    private final Connection connection;

    public Delivery(String[] parameters) throws SQLException, IOException, ClassNotFoundException {
        super(parameters);
        warehouseID = Integer.parseInt(parameters[0]);
        carrierID = Integer.parseInt(parameters[1]);
        connection = super.getConnection();
    }

    @Override
    public void execute() {
        try {
            List<Order> orders = selectOrders();
            int count = 1;
            for (Order order : orders) {
                System.out.println("Order detail: " + order);
                updateCarrier(order);
                updateOrderLines(order);
                updateCustomer(order);
                System.out.println("Order " + count + " has been delivered at " + new Timestamp(System.currentTimeMillis()));
                System.out.println("________________________________");
                count++;
            }
            connection.commit();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // select orders of each of the 10 districts from wholesale.order
    private List<Order> selectOrders() throws SQLException {

        String getOrderInfoSql = "select o_id, o_c_id from wholesale.order\n" +
                "where o_w_id = ? and o_d_id = ?\n" +
                "and o_id = (select MIN(o_id) from wholesale.order \n" +
                "where o_w_id = ? and o_d_id = ?\n" +
                "and \"order\".o_carrier_id is NULL);";

        PreparedStatement preparedStatement = connection.prepareStatement(getOrderInfoSql);
        List<Order> orders = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            preparedStatement.setInt(1, warehouseID);
            preparedStatement.setInt(2, i);
            preparedStatement.setInt(3, warehouseID);
            preparedStatement.setInt(4, i);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Order order = new Order();
                order.setWarehouseID(warehouseID);
                order.setDistrictID(i);
                order.setOrderID(resultSet.getInt(1));
                order.setCustomerID(resultSet.getInt(2));
                order.setCarrierID(carrierID);
                orders.add(order);
                // System.out.println("looping....");
            }
        }
        // System.out.println("loop ended.");
        preparedStatement.close();
        return orders;
    }

    // set carrier for each order
    private void updateCarrier(Order order) throws SQLException {
        String updateCarrierSql = "update wholesale.order set o_carrier_id = ? \n" +
                "where o_w_id = ? and o_d_id = ? and o_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateCarrierSql);
        preparedStatement.setInt(1, order.getCarrierID());
        preparedStatement.setInt(2, order.getWarehouseID());
        preparedStatement.setInt(3, order.getDistrictID());
        preparedStatement.setInt(4, order.getOrderID());
        preparedStatement.executeUpdate();
        System.out.println("updateCarrierSql is executed at " + new Timestamp(System.currentTimeMillis()));
        preparedStatement.close();
    }

    // update timestamp of order lines for each order
    private void updateOrderLines(Order order) throws SQLException {
        String updateOrderLineTimestampSql = "update wholesale.order_line set ol_delivery_d = ? \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ?";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement preparedStatement = connection.prepareStatement(updateOrderLineTimestampSql);
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setInt(2, order.getWarehouseID());
        preparedStatement.setInt(3, order.getDistrictID());
        preparedStatement.setInt(4, order.getOrderID());
        preparedStatement.executeUpdate();
        System.out.println("updateOrderLineTimestampSql is executed at " + new Timestamp(System.currentTimeMillis()));
        preparedStatement.close();

    }

    // update customer info for each order
    private void updateCustomer(Order order) throws SQLException {
        // calculate the total amount of the order
        String selectSumOfAmountSql = "select sum(ol_amount) from wholesale.order_line \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(selectSumOfAmountSql);
        preparedStatement.setInt(1, order.getWarehouseID());
        preparedStatement.setInt(2, order.getDistrictID());
        preparedStatement.setInt(3, order.getOrderID());

        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println("selectSumOfAmountSql is executed at " + new Timestamp(System.currentTimeMillis()));
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        if (resultSet.next()) {
            totalAmount = resultSet.getBigDecimal(1);
        }
        System.out.println("total amount is " + totalAmount);

        // update customer
        String updateCustomer = "update wholesale.customer set c_balance = c_balance + ?, c_payment_cnt = c_payment_cnt + 1 \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?";

        preparedStatement = connection.prepareStatement(updateCustomer);
        preparedStatement.setBigDecimal(1, totalAmount);
        preparedStatement.setInt(2, order.getWarehouseID());
        preparedStatement.setInt(3, order.getDistrictID());
        preparedStatement.setInt(4, order.getCustomerID());
        preparedStatement.executeUpdate();
        System.out.println("updateCustomer is executed at " + new Timestamp(System.currentTimeMillis()));
        preparedStatement.close();
    }
}
