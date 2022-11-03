package edu.teamv.transactions.impl;

import edu.teamv.pojo.Order;
import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryTransaction extends Transaction {

    private final Integer warehouseID;
    private final Integer carrierID;

    private final Connection connection;

    public DeliveryTransaction(String[] parameters) throws SQLException, IOException, ClassNotFoundException {
        super(parameters);
        warehouseID = Integer.parseInt(parameters[0]);
        carrierID = Integer.parseInt(parameters[1]);
        connection = super.getConnection();
    }

    @Override
    public void execute() {
        try {
            List<Order> orders = findOrders();
            for (Order order : orders) {
                updateCarrier(order);
                updateOrderLines(order);
                updateCustomer(order);
                updateNtd(order);
            }

            // connection.commit();
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
    private List<Order> findOrders() throws SQLException {

        String getOrderInfoSql = "select o_id, o_c_id from wholesale.order\n" +
        "where o_w_id = ? and o_d_id = ?\n" +
                "and o_id = (select ntd_o_id from wholesale.next_to_deliver_order \n" +
                "where ntd_w_id = ? and ntd_d_id = ?);";

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
                order.setDistrictID(i);
                order.setOrderID(resultSet.getInt(1));
                order.setCustomerID(resultSet.getInt(2));
                orders.add(order);
                // System.out.println(order);
            }
        }
        preparedStatement.close();
        return orders;
    }

    // set carrier for each order
    private void updateCarrier(Order order) throws SQLException {
        String updateCarrierSql = "update wholesale.order set o_carrier_id = ? \n" +
                "where o_w_id = ? and o_d_id = ? and o_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(updateCarrierSql);
        preparedStatement.setInt(1, carrierID);
        preparedStatement.setInt(2, warehouseID);
        preparedStatement.setInt(3, order.getDistrictID());
        preparedStatement.setInt(4, order.getOrderID());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    // update timestamp of order lines for each order
    private void updateOrderLines(Order order) throws SQLException {
        String updateOrderLineTimestampSql = "update wholesale.order_line set ol_delivery_d = ? \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ?;";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement preparedStatement = connection.prepareStatement(updateOrderLineTimestampSql);
        preparedStatement.setTimestamp(1, timestamp);
        preparedStatement.setInt(2, warehouseID);
        preparedStatement.setInt(3, order.getDistrictID());
        preparedStatement.setInt(4, order.getOrderID());
        preparedStatement.executeUpdate();
        preparedStatement.close();

    }

    // update customer info for each order
    private void updateCustomer(Order order) throws SQLException {
        // calculate the total amount of the order
        String selectSumOfAmountSql = "select sum(ol_amount) from wholesale.order_line \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ?;";

        PreparedStatement preparedStatement1 = connection.prepareStatement(selectSumOfAmountSql);
        preparedStatement1.setInt(1, warehouseID);
        preparedStatement1.setInt(2, order.getDistrictID());
        preparedStatement1.setInt(3, order.getOrderID());

        ResultSet resultSet = preparedStatement1.executeQuery();
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        if (resultSet.next()) {
            totalAmount = resultSet.getBigDecimal(1);
        }
        preparedStatement1.close();
        // System.out.println("total amount is " + totalAmount);

        // update customer
        String updateCustomer = "update wholesale.customer set c_balance = c_balance + ?, c_delivery = c_delivery + 1 \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?;";


        PreparedStatement preparedStatement2 = connection.prepareStatement(updateCustomer);
        preparedStatement2.setBigDecimal(1, totalAmount);
        preparedStatement2.setInt(2, warehouseID);
        preparedStatement2.setInt(3, order.getDistrictID());
        preparedStatement2.setInt(4, order.getCustomerID());
        preparedStatement2.executeUpdate();
        preparedStatement2.close();
    }


    private void updateNtd(Order order) throws SQLException {

        String updateNtdSql = "update wholesale.next_to_deliver_order \n" +
                "set ntd_o_id =ntd_o_id + 1 \n" +
                "where ntd_w_id = ? and ntd_d_id = ? and ntd_o_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(updateNtdSql);
        preparedStatement.setInt(1, warehouseID);
        preparedStatement.setInt(2, order.getDistrictID());
        preparedStatement.setInt(3, order.getOrderID());
    }
}
