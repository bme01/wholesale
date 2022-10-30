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
        public Integer itemID;
        public String itemName;
        public Integer quantity;
    }

    @Override
    public void execute() {
        try {
            Integer nextOrderID = findNextOrderID();
            List<Order> lastNOrders = findOrders(nextOrderID);
            List<Customer> customers = findCustomers(lastNOrders);
            List<HashSet<Integer>> itemsSetList = findItemsSet(lastNOrders);
            Map<Order, List<PopularItem>> orderPopularItemMap = findPopularItems(lastNOrders);

            // find frequency for each popular item
            Map<String, Integer> itemCount = new HashMap<>();
            for (List<PopularItem> popularItems : orderPopularItemMap.values()) {
                for (PopularItem popularItem : popularItems) {
                    for (HashSet hashSet : itemsSetList) {
                        if (hashSet.contains(popularItem.itemID)) {
                            if (itemCount.containsKey(popularItem.itemName)) {
                                itemCount.put(popularItem.itemName, itemCount.get(popularItem.itemName) + 1);
                            } else {
                                itemCount.put(popularItem.itemName, 1);
                            }
                        }
                    }
                }
            }

            // output results
            System.out.println(String.format("District Identifier: (%s, %s)", warehouseID, districtID));
            System.out.println(String.format("Number of Last Orders: %d", lastN));
            for (int i = 0; i < lastNOrders.size(); i++) {
                Order order = lastNOrders.get(i);
                System.out.println(String.format("Order ID: %d; Entry: %s", order.getOrderID(), order.getOrderEntry()));
                Customer customer = customers.get(i);
                System.out.println(String.format("Customer Name: %s, %s, %s", customer.getFirstName(), customer.getMiddleName(), customer.getLastName()));
                List<PopularItem> popularItems = orderPopularItemMap.get(order);
                for (PopularItem popularItem : popularItems) {
                    System.out.println(String.format("Item Name: %s; Quantity: %d", popularItem.itemName, popularItem.quantity));
                }
            }
            for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
                System.out.println(String.format("Item Name: %S; Percentage: %2.2f%%", entry.getKey(), (double) (entry.getValue() * 100 / lastN)));
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

        String selectPopularItemsSql = "select ol_i_id, ol_quantity from wholesale.order_line \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ? \n" +
                "and ol_quantity = (select max(ol_quantity) from wholesale.order_line \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ?)";

        String selectItemNameSql = "select i_name from wholesale.item where i_id = ?";

        for (Order order : lastNOrders) {

            PreparedStatement preparedStatement1 = connection.prepareStatement(selectPopularItemsSql);
            preparedStatement1.setInt(1, warehouseID);
            preparedStatement1.setInt(2, districtID);
            preparedStatement1.setInt(3, order.getOrderID());
            preparedStatement1.setInt(4, warehouseID);
            preparedStatement1.setInt(5, districtID);
            preparedStatement1.setInt(6, order.getOrderID());
            ResultSet resultSet = preparedStatement1.executeQuery();

            List<PopularItem> popularItems = new ArrayList<>();

            while (resultSet.next()) {
                PopularItem popularItem = new PopularItem();
                popularItem.itemID = resultSet.getInt(1);
                popularItem.quantity = resultSet.getInt(2);
                PreparedStatement preparedStatement2 = connection.prepareStatement(selectItemNameSql);
                preparedStatement2.setInt(1, popularItem.itemID);
                ResultSet itemResultSet = preparedStatement2.executeQuery();
                if (itemResultSet.next()) {
                    popularItem.itemName = resultSet.getString(1);
                }
                preparedStatement2.close();
                popularItems.add(popularItem);
            }
            orderPopularItemMap.put(order, popularItems);
            preparedStatement1.close();
        }
        return orderPopularItemMap;
    }

    private List<HashSet<Integer>> findItemsSet(List<Order> lastNOrders) throws SQLException {
        List<HashSet<Integer>> itemsSetList = new ArrayList<>();


        String selectItemsSql = "select ol_i_id from wholesale.order_line \n" +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ?";

        PreparedStatement preparedStatement1 = connection.prepareStatement(selectItemsSql);
        for (Order order : lastNOrders) {
            preparedStatement1.setInt(1, warehouseID);
            preparedStatement1.setInt(2, districtID);
            preparedStatement1.setInt(3, order.getOrderID());
            ResultSet resultSet = preparedStatement1.executeQuery();
            HashSet<Integer> itemIDSet = new HashSet<>();
            while (resultSet.next()) {
                itemIDSet.add(resultSet.getInt(1));
            }
            itemsSetList.add(itemIDSet);
        }
        return itemsSetList;
    }

}