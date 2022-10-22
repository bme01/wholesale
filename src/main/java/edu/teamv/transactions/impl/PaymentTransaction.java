package edu.teamv.transactions.impl;

import edu.teamv.pojo.Customer;
import edu.teamv.pojo.District;
import edu.teamv.pojo.Warehouse;
import edu.teamv.transactions.Transaction;
import edu.teamv.utils.PreparedStatementUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentTransaction extends Transaction {

    private final Integer customerWarehouseID;

    private final Integer customerDistrictID;

    private final Integer customerID;

    private final BigDecimal payment;

    private final Connection connection;

    public PaymentTransaction(String[] parameters) throws SQLException, IOException, ClassNotFoundException {
        super(parameters);
        customerWarehouseID = Integer.parseInt(parameters[0]);
        customerDistrictID = Integer.parseInt(parameters[1]);
        customerID = Integer.parseInt(parameters[2]);
        payment = new BigDecimal(parameters[3]);
        connection = super.getConnection();
    }

    @Override
    public void execute() {
        try {
            updateWarehouse();
            updateDistrict();
            updateCustomer();
            cleanup();
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

    private void updateWarehouse() throws SQLException {

        String updateWarehouseSql = "update wholesale.warehouse set w_ytd = w_ytd + ? \n" +
                "where w_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(updateWarehouseSql);
        preparedStatement.setBigDecimal(1, payment);
        preparedStatement.setInt(2, customerWarehouseID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private void updateDistrict() throws SQLException {

        String updateDistrictSql = "update wholesale.district set d_ytd = d_ytd + ? \n" +
                "where d_w_id = ? and d_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(updateDistrictSql);
        preparedStatement.setBigDecimal(1, payment);
        preparedStatement.setInt(2, customerWarehouseID);
        preparedStatement.setInt(3, customerDistrictID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }


    private void updateCustomer() throws SQLException {

        PreparedStatement preparedStatement;

        List<Object> sqlParameters = new ArrayList<>();
        sqlParameters.add(payment);
        sqlParameters.add(customerID);
        sqlParameters.add(customerDistrictID);
        sqlParameters.add(customerID);

        String updateCustomerBalanceSql = "update wholesale.customer set c_balance = c_balance - ? \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?";

        preparedStatement = PreparedStatementUtil.getPreparedStatement(connection, updateCustomerBalanceSql, sqlParameters);
        preparedStatement.executeUpdate();

        String updateCustomerYtdPaymentSql = "update wholesale.customer set c_ytd_payment = c_ytd_payment + ? \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?";

        preparedStatement =PreparedStatementUtil.getPreparedStatement(connection, updateCustomerYtdPaymentSql, sqlParameters);
        preparedStatement.executeUpdate();

        sqlParameters.remove(0);

        String updateCustomerPaymentCountSql = "update wholesale.customer set c_payment_cnt = c_payment_cnt + 1 \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?";

        preparedStatement = PreparedStatementUtil.getPreparedStatement(connection, updateCustomerPaymentCountSql, sqlParameters);
        preparedStatement.executeUpdate();

        preparedStatement.close();

    }

    private void cleanup() throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        String selectCustomerSql = "select c_first, c_middle, c_last, \n" +
                "c_street_1, c_street_2, c_city, c_state, c_zip, \n" +
                "c_phone, c_since, c_credit, c_credit_lim, c_discount, c_balance\n" +
                " from wholesale.customer \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?";

        preparedStatement = connection.prepareStatement(selectCustomerSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.setInt(3, customerID);
        resultSet = preparedStatement.executeQuery();
        Customer customer = new Customer();
        if (resultSet.next()){
            customer.setWarehouseID(customerWarehouseID);
            customer.setDistrictId(customerDistrictID);
            customer.setCustomerId(customerID);
            customer.setFirstName(resultSet.getString(1));
            customer.setMiddleName(resultSet.getString(2));
            customer.setLastName(resultSet.getString(3));
            customer.setStreet1(resultSet.getString(4));
            customer.setStreet2(resultSet.getString(5));
            customer.setCity(resultSet.getString(6));
            customer.setState(resultSet.getString(7));
            customer.setZip(resultSet.getString(8));
            customer.setPhone(resultSet.getString(9));
            customer.setSince(resultSet.getTimestamp(10));
            customer.setCredit(resultSet.getString(11));
            customer.setCreditLimit(resultSet.getBigDecimal(12));
            customer.setDiscount(resultSet.getBigDecimal(13));
            customer.setBalance(resultSet.getBigDecimal(14));
        }
        System.out.println(customer);

        String selectWarehouseSql = "select w_street_1, w_street_2, w_city, w_state, w_zip \n" +
                " from wholesale.warehouse";
        preparedStatement = connection.prepareStatement(selectWarehouseSql);
        resultSet = preparedStatement.executeQuery();
        Warehouse warehouse = new Warehouse();
        if (resultSet.next()) {
            warehouse.setStreet1(resultSet.getString(1));
            warehouse.setStreet2(resultSet.getString(2));
            warehouse.setCity(resultSet.getString(3));
            warehouse.setState(resultSet.getString(4));
            warehouse.setZip(resultSet.getString(5));
        }
        System.out.println(warehouse);

        String selectDistrictSql = "select d_street_1, d_street_2, d_city, d_state, d_zip \n" +
                " from wholesale.district";
        preparedStatement = connection.prepareStatement(selectDistrictSql);
        resultSet = preparedStatement.executeQuery();
        District district = new District();
        if (resultSet.next()) {
            district.setStreet1(resultSet.getString(1));
            district.setStreet2(resultSet.getString(2));
            district.setCity(resultSet.getString(3));
            district.setState(resultSet.getString(4));
            district.setZip(resultSet.getString(5));
        }
        System.out.println(district);
        System.out.println(payment);
    }
}
