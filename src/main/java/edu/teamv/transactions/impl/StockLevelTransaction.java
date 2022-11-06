package edu.teamv.transactions.impl;

import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class StockLevelTransaction extends Transaction {
    private final Integer warehouseID;
    private final Integer districtID;
    private final Integer  stockThreshold;
    private final Integer numberOfExaminedOrder;
    Connection connection;
    public StockLevelTransaction(String[] parameters)
            throws SQLException, IOException, ClassNotFoundException {
        super(parameters);
        warehouseID = Integer.parseInt(parameters[0]);
        districtID = Integer.parseInt(parameters[1]);
        stockThreshold = Integer.parseInt(parameters[2]);
        numberOfExaminedOrder = Integer.parseInt(parameters[3]);

        connection = super.getConnection();
    }

    @Override
    public void execute() throws SQLException {
        try {
            System.out.println("======Stock Level Transaction======");
            Integer N = getNextOrderNumber();
            HashSet<Integer> itemSet = getItemSet(N);
            Integer itemNumber = getItemNumberBelowThread(itemSet);
            System.out.println(" The total number of items below the threshold: " + itemNumber);

            connection.commit();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }
            throw new SQLException();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        }

    }

    private Integer getNextOrderNumber() throws SQLException {
        String getNextOrderSql = "select d_next_oid \n" +
                " from wholesale.district \n"+
                " where d_w_id = ? and d_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(getNextOrderSql);
        preparedStatement.setInt(1, warehouseID);
        preparedStatement.setInt(2, districtID);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer res = 0;
        if(resultSet.next()){
            res = resultSet.getInt(1);
        }

        preparedStatement.close();

        return res;
    }

    private HashSet<Integer> getItemSet(Integer nextAvailableOrderNum) throws SQLException {
        HashSet<Integer> itemSet = new HashSet<>();

        String getItemIdSql = "select ol_i_id \n" +
                " from wholesale.order_line \n"+
                " where ol_w_id = ? and ol_d_id = ? and ol_o_id >= ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(getItemIdSql);
        preparedStatement.setInt(1, warehouseID);
        preparedStatement.setInt(2, districtID);
        ResultSet resultSet;
        preparedStatement.setInt(3, nextAvailableOrderNum - numberOfExaminedOrder);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            itemSet.add(resultSet.getInt(1));
        }
        preparedStatement.close();
        return itemSet;
    }

    private Integer getItemNumberBelowThread(HashSet<Integer> itemSet) throws SQLException {
        Integer totalNum = 0;
        String getItemIdSql = "select s_quantity \n" +
                " from wholesale.stock \n"+
                " where s_w_id = ? and s_i_id in ( ";
        ResultSet resultSet;
        ArrayList<String> conditions = new ArrayList<>();
        for(Integer item : itemSet){
            conditions.add(String.valueOf(item));
        }
        getItemIdSql = getItemIdSql + String.join(", ", conditions) + ");";
        PreparedStatement preparedStatement = connection.prepareStatement(getItemIdSql);
        preparedStatement.setInt(1, warehouseID);
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            Integer stockQuantity = resultSet.getInt(1);
            if(stockQuantity < stockThreshold){
                totalNum++;
            }
        }
        preparedStatement.close();
        return totalNum;

    }

}
