package edu.teamv.transactions.impl;

import edu.teamv.pojo.Customer;
import edu.teamv.transactions.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopBalanceTransaction extends Transaction {

    private final Connection connection;

    public TopBalanceTransaction(String[] parameters) {
        connection = super.getConnection();
    }

    @Override
    public void execute() {

        try {

            String selectTop10CustomersSql = "select c_w_id, c_d_id, c_first, c_middle, c_last, c_balance \n" +
                    " from wholesale.customer " +
                    "order by c_balance limit 10;";

            PreparedStatement preparedStatement1 = connection.prepareStatement(selectTop10CustomersSql);
            ResultSet resultSet1 = preparedStatement1.executeQuery();

            // find top 10 customers
            List<Customer> top10Customers = new ArrayList<>();
            while (resultSet1.next()) {
                Customer customer = new Customer();
                customer.setWarehouseID(resultSet1.getInt(1));
                customer.setDistrictId(resultSet1.getInt(2));
                customer.setFirstName(resultSet1.getString(3));
                customer.setMiddleName(resultSet1.getString(4));
                customer.setLastName(resultSet1.getString(5));
                customer.setBalance(resultSet1.getBigDecimal(6));
                top10Customers.add(customer);
            }

            preparedStatement1.close();

            // find warehouse name and district name for each customer
            String selectWDNamesSql = "select w_name, d_name \n" +
                    "from wholesale.warehouse, wholesale.district \n" +
                    "where w_id = ? and d_w_id = ? and d_id = ?";

            String[] warehouseNames = new String[10];
            String[] districtNames = new String[10];
            for (int i = 0; i < 10; i++) {
                Customer customer = top10Customers.get(i);
                PreparedStatement preparedStatement2 = connection.prepareStatement(selectWDNamesSql);
                preparedStatement2.setInt(1, customer.getWarehouseID());
                preparedStatement2.setInt(2, customer.getWarehouseID());
                preparedStatement2.setInt(3, customer.getDistrictId());
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                if (resultSet2.next()) {
                    warehouseNames[i] = resultSet2.getString(1);
                    districtNames[i] = resultSet2.getString(2);
                }
                preparedStatement2.close();
            }

            // output results
            for (int i = 0; i < 10; i++) {
                System.out.println(top10Customers.get(i));
                System.out.println(warehouseNames[i]);
                System.out.println(districtNames[i]);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
