package edu.teamv.transactions.impl;

import edu.teamv.pojo.Customer;
import edu.teamv.pojo.Order;
import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PopularItemTransaction extends Transaction {

    private final Integer warehouseID;

    private final Integer districtID;

    private final Integer lastN;

    private final Connection connection;

    public PopularItemTransaction(String[] parameters) throws SQLException, IOException, ClassNotFoundException {
        super(parameters);
        warehouseID = Integer.parseInt(parameters[0]);
        districtID = Integer.parseInt(parameters[1]);
        lastN = Integer.parseInt(parameters[2]);
        connection = super.getConnection();
    }

    private class PopularItem {
        public String itemName;
        public Integer quantity;
    }

    @Override
    public void execute() {
        try {
            Integer nextOrderID = findNextOrderID();
            List<Order> lastNOrders = findOrders(nextOrderID);
            List<Customer> customers = findCustomers(lastNOrders);
            Map<Order, List<PopularItem>> orderPopularItemMap = findPopularItems(lastNOrders);

            // find frequency for each popular item
            Map<String, Integer> itemCount = new HashMap<>();
            for (Map.Entry<Order, List<PopularItem>> orderListEntry : orderPopularItemMap.entrySet()) {
                List<PopularItem> popularItems = (List<PopularItem>) ((Map.Entry<?, ?>) orderListEntry).getValue();
                for (PopularItem popularItem : popularItems) {
                    if (itemCount.containsKey(popularItem.itemName)) {
                        itemCount.put(popularItem.itemName, itemCount.get(popularItem.itemName) + 1);
                    } else {
                        itemCount.put(popularItem.itemName, 1);
                    }
                }
            }

            // output results
            System.out.println(warehouseID + ", " + districtID);
            System.out.println(lastN);
            for (int i = 0; i < lastNOrders.size(); i++) {
                Order order = lastNOrders.get(i);
                System.out.println(order.getOrderID() + ": " + order.getOrderEntry());
                Customer customer = customers.get(i);
                System.out.println(customer.getFirstName() + ", " + customer.getMiddleName() + ", " + customer.getLastName());
                List<PopularItem> popularItems = orderPopularItemMap.get(order);
                for (PopularItem popularItem : popularItems) {
                    System.out.println(popularItem.itemName + "," + popularItem.quantity);
                }
            }
            for (Map.Entry<String, Integer> stringIntegerEntry : itemCount.entrySet()) {
                System.out.println(((Map.Entry<?, ?>) stringIntegerEntry).getKey() + ", " + ((Map.Entry<?, ?>) stringIntegerEntry).getValue());
            }

            // connection.commit();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer findNextOrderID() throws SQLException {

        String selectNextOrderSql = "select d_next_oid from wholesale.district \n" +
                "where d_w_id = ? and d_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(selectNextOrderSql);
        preparedStatement.setInt(1, warehouseID);
        preparedStatement.setInt(2, districtID);

        ResultSet resultSet = preparedStatement.executeQuery();
        Integer nextOrderId = null;
        if (resultSet.next()) {
            nextOrderId = resultSet.getInt(1);
        }
        if (nextOrderId == null) {
            throw new RuntimeException();
        }

        preparedStatement.close();
        return nextOrderId;
    }

    private List<Order> findOrders(Integer nextOrderID) throws SQLException {

        String selectOderSql = "select o_c_id, o_entry_d from wholesale.order \n" +
                "where o_w_id = ? and o_d_id = ? and o_id between ? and ?;";

        PreparedStatement preparedStatement = connection.prepareStatement(selectOderSql);
        preparedStatement.setInt(1, warehouseID);
        preparedStatement.setInt(2, districtID);
        preparedStatement.setInt(3, nextOrderID - lastN);
        preparedStatement.setInt(4, nextOrderID - 1);

        System.out.println(preparedStatement);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Order> orders = new ArrayList<>();
        Integer currentOrderId = nextOrderID - lastN;
        while (resultSet.next()) {
            Order order = new Order();
            order.setOrderID(currentOrderId);
            order.setCustomerID(resultSet.getInt(1));
            order.setOrderEntry(resultSet.getTimestamp(2));
            orders.add(order);
            currentOrderId++;
        }

        preparedStatement.close();
        return orders;
    }

    private List<Customer> findCustomers(List<Order> lastNOrders) throws SQLException {

        List<Customer> customers = new ArrayList<>();

        String selectCustomersSql = "select c_first, c_middle, c_last from wholesale.customer \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(selectCustomersSql);
        preparedStatement.setInt(1, warehouseID);
        preparedStatement.setInt(2, districtID);

        for (Order order : lastNOrders) {
            preparedStatement.setInt(3, order.getCustomerID());
            ResultSet resultSet = preparedStatement.executeQuery();
            Customer customer = new Customer();
            if (resultSet.next()) {
                customer.setFirstName(resultSet.getString(1));
                customer.setMiddleName(resultSet.getString(2));
                customer.setLastName(resultSet.getString(3));
                customers.add(customer);
            }
        }

        preparedStatement.close();
        return customers;
    }

    private Map<Order, List<PopularItem>> findPopularItems(List<Order> lastNOrders) throws SQLException {

        Map<Order, List<PopularItem>> orderPopularItemMap = new HashMap<>();
        PreparedStatement preparedStatement;

        String selectPopularItemsSql = "select ol_i_id, ol_quantity from wholesale.order_line \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ? \n" +
                "and ol_quantity = (select max(ol_quantity) from wholesale.order_line \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ?)";

        String selectItemNameSql = "select i_name from wholesale.item where i_id = ?";

        for (Order order : lastNOrders) {

            preparedStatement = connection.prepareStatement(selectPopularItemsSql);
            preparedStatement.setInt(1, warehouseID);
            preparedStatement.setInt(2, districtID);
            preparedStatement.setInt(3, order.getOrderID());
            preparedStatement.setInt(4, warehouseID);
            preparedStatement.setInt(5, districtID);
            preparedStatement.setInt(6, order.getOrderID());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<PopularItem> popularItems = new ArrayList<>();

            while (resultSet.next()) {
                Integer itemID = resultSet.getInt(1);
                PopularItem popularItem = new PopularItem();
                popularItem.quantity = resultSet.getInt(2);
                preparedStatement = connection.prepareStatement(selectItemNameSql);
                preparedStatement.setInt(1, itemID);
                ResultSet itemResultSet = preparedStatement.executeQuery();
                if (itemResultSet.next()) {
                    popularItem.itemName = resultSet.getString(1);
                }
                popularItems.add(popularItem);
            }
            orderPopularItemMap.put(order, popularItems);
        }
        return orderPopularItemMap;
    }

}
