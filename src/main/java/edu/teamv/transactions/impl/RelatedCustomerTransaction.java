package edu.teamv.transactions.impl;

import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RelatedCustomerTransaction extends Transaction {
    private final Integer customerWarehouseID;
    private final Integer customerDistrictID;
    private final Integer customerID;

    Connection connection;
    public RelatedCustomerTransaction(String[] customerIdentify)
            throws SQLException, IOException, ClassNotFoundException {
        super(customerIdentify);
        customerWarehouseID = Integer.parseInt(customerIdentify[0]);
        customerDistrictID = Integer.parseInt(customerIdentify[1]);
        customerID = Integer.parseInt(customerIdentify[2]);
        connection = super.getConnection();
    }

        public void execute() {
        try {
            System.out.println("======Related Customer Transaction======");
            System.out.println("Customer identifier{" +
                    " warehouseID=" + customerWarehouseID +
                    ", districtId=" + customerDistrictID +
                    ", customerId=" + customerID + " }" );
            System.out.println("Related Customer: ");
            HashMap<Integer, ArrayList<Integer>>  orderItems = getOrderitems();
            for(int w_id = 1; w_id < 11; w_id++){
                if (w_id == customerWarehouseID){
                    continue;
                }
                for(int d_id = 1; d_id < 11; d_id++){
                    HashSet<Integer> relatedCustomerId = new HashSet<>();
                    for(ArrayList<Integer> items : orderItems.values()){
                        ArrayList<Integer> ids = getRelatedCustomers(w_id, d_id, items);
                        relatedCustomerId.addAll(ids);
                    }
                    for(Integer customerId : relatedCustomerId){

                    System.out.println("Customer identifier{" +
                            " warehouseID=" + w_id +
                            ", districtId=" + d_id +
                            ", customerId=" + customerId + " }" );
                    }
                }

            }




            System.out.println("======End Transaction======");

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

    private HashMap<Integer, ArrayList<Integer>> getOrderitems() throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String getOrdersSql = "select coi_o_id, coi_i_id from wholesale.customer_order_items \n" +
                  "where coi_w_id = ? and coi_d_id = ? and coi_c_id = ?";
        preparedStatement = connection.prepareStatement(getOrdersSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.setInt(3, customerID);
        resultSet = preparedStatement.executeQuery();
        HashMap<Integer, ArrayList<Integer>> orderItems = new HashMap<>();
        while(resultSet.next()){
            Integer order = resultSet.getInt(1);
            Integer item = resultSet.getInt(2);
            ArrayList<Integer> itemList;
            if(orderItems.get(order) == null){
                itemList = new ArrayList<>();
            }else {
                itemList = orderItems.get(order);
            }
            itemList.add(item);
            orderItems.put(order, itemList);
        }
        return orderItems;

    }

    ArrayList<Integer> getRelatedCustomers(Integer warehouseId, Integer districtId, ArrayList<Integer> items) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String getOrdersSql = "select coi_c_id, coi_o_id, coi_i_id from wholesale.customer_order_items \n" +
                "where coi_w_id = ? and coi_d_id = ? and coi_i_id in ( " ;
        String[] itemsArray = new String[items.size()];
        for(int i = 0; i < items.size(); i++){
            itemsArray[i] = String.valueOf(items.get(i));
        }
        getOrdersSql = getOrdersSql + String.join(", ", itemsArray) + " )";
        preparedStatement = connection.prepareStatement(getOrdersSql);
        preparedStatement.setInt(1, warehouseId);
        preparedStatement.setInt(2, districtId);
        resultSet = preparedStatement.executeQuery();
        HashMap<String, Integer> data = new HashMap<>();
        while(resultSet.next()){
            String str = resultSet.getInt(1) + "," + resultSet.getInt(2);
            Integer count = data.getOrDefault(str, 0);
            data.put(str, count + 1);
        }
        ArrayList<Integer> relatedCustomers = new ArrayList<>();
        for(String s : data.keySet()){
            if(data.get(s) >= 2){
                relatedCustomers.add(Integer.parseInt(s.split(",")[0]));
            }
        }
        return  relatedCustomers;

    }





//    @Override
//    public void execute() {
//        try {
//            System.out.println("======Related Customer Transaction======");
//            System.out.println("Customer identifier{" +
//                    " warehouseID=" + customerWarehouseID +
//                    ", districtId=" + customerDistrictID +
//                    ", customerId=" + customerID + " }" );
//            System.out.println("Related Customer: ");
//            HashSet<Integer> orderIds = getCustomerOrders();
//            for(Integer orderId : orderIds){
//                ArrayList<Integer[]> relatedOrderInfo = getRelatedOrderInfo(orderId);
//                for(int i = 0; i < relatedOrderInfo.size(); i++){
//                    //System.out.println(relatedOrder[0] + relatedOrder[1] + relatedOrder[2]);
//                    printRelateCustomer(relatedOrderInfo.get(i));
//                }
//
//            }
//            System.out.println("======End Transaction======");
//
//            connection.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            try {
//                connection.rollback();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//
//    }
//
//    private HashSet<Integer> getCustomerOrders() throws SQLException {
//        PreparedStatement preparedStatement;
//        ResultSet resultSet;
//        String getCustomerInfoSql = "select o_id from wholesale.\"order\" \n" +
//                "where o_w_id = ? and o_d_id = ? and o_c_id = ?";
//        preparedStatement = connection.prepareStatement(getCustomerInfoSql);
//        preparedStatement.setInt(1, customerWarehouseID);
//        preparedStatement.setInt(2, customerDistrictID);
//        preparedStatement.setInt(3, customerID);
//        HashSet<Integer> orderIds = new HashSet<>();
//        resultSet = preparedStatement.executeQuery();
//        while (resultSet.next()){
//            orderIds.add(resultSet.getInt(1));
//        }
//        preparedStatement.close();
//        return orderIds;
//    }
//
//    private HashSet<Integer> getOrderLineItems(Integer orderId) throws SQLException {
//        PreparedStatement preparedStatement;
//        ResultSet resultSet;
//        String getCustomerInfoSql = "select ol_i_id from wholesale.order_line \n" +
//                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ?";
//        preparedStatement = connection.prepareStatement(getCustomerInfoSql);
//        preparedStatement.setInt(1, customerWarehouseID);
//        preparedStatement.setInt(2, customerDistrictID);
//        preparedStatement.setInt(3, orderId);
//        HashSet<Integer> OrderLineItems = new HashSet<>();
//        resultSet = preparedStatement.executeQuery();
//        while (resultSet.next()){
//            OrderLineItems.add(resultSet.getInt(1));
//        }
//        preparedStatement.close();
//        return OrderLineItems;
//    }
//
//    private ArrayList<Integer[]> getRelatedOrderInfo(Integer orderId) throws SQLException {
//        PreparedStatement preparedStatement;
//        ResultSet resultSet;
//        String getCustomerInfoSql =
//                "select \n" +
//                "    ol_w_id2, ol_d_id2, ol_o_id2 \n" +
//                "from ( \n" +
//                "    select \n" +
//                "        ol_w_id2, ol_d_id2, ol_o_id2, cn     \n" +
//                "    from \n" +
//                "    (\n" +
//                "        select\n" +
//                "            b.ol_w_id ol_w_id2,\n" +
//                "            b.ol_d_id ol_d_id2,\n" +
//                "            b.ol_o_id ol_o_id2,\n" +
//                "            count(*) cn\n" +
//                "        from wholesale.order_line a\n" +
//                "        inner join wholesale.order_line b\n" +
//                "        on a.ol_w_id = ? and a.ol_d_id = ? and a.ol_o_id = ?   \n" +
//                "        and a.ol_w_id != b.ol_w_id\n" +
//                "        and a.ol_i_id = b.ol_i_id\n" +
//                "        group by b.ol_w_id, b.ol_d_id, b.ol_o_id\n" +
//                "    )a  \n" +
//                ")b  \n" +
//                "where cn >= 2; ";
//        preparedStatement = connection.prepareStatement(getCustomerInfoSql);
//        preparedStatement.setInt(1, customerWarehouseID);
//        preparedStatement.setInt(2, customerDistrictID);
//        preparedStatement.setInt(3, orderId);
//        resultSet = preparedStatement.executeQuery();
//        ArrayList<Integer[]> relatedOrders = new ArrayList<>();
//
//        while(resultSet.next()){
//            Integer[] relatedOrder = new Integer[3];
//            relatedOrder[0] = resultSet.getInt(1);
//            relatedOrder[1] = resultSet.getInt(2);
//            relatedOrder[2] = resultSet.getInt(3);
//            relatedOrders.add(relatedOrder);
//        }
//        preparedStatement.close();
//        return relatedOrders;
//
//    }
//
//    private void printRelateCustomer(Integer[] relatedOrderInfo) throws SQLException {
//        PreparedStatement preparedStatement;
//        ResultSet resultSet;
//        String getCustomerIdSql = "select o_c_id from wholesale.\"order\" \n" +
//                "where o_w_id = ? and o_d_id = ? and o_id = ?";
//        preparedStatement = connection.prepareStatement(getCustomerIdSql);
//        preparedStatement.setInt(1, relatedOrderInfo[0]);
//        preparedStatement.setInt(2, relatedOrderInfo[1]);
//        preparedStatement.setInt(3, relatedOrderInfo[2]);
//        resultSet = preparedStatement.executeQuery();
//        Integer RelatedCustomerId = 0;
//        if(resultSet.next()){
//            RelatedCustomerId = resultSet.getInt(1);
//        }
//        preparedStatement.close();
//
//        System.out.println("Customer identifier{" +
//                " warehouseID=" + relatedOrderInfo[0] +
//                ", districtId=" + relatedOrderInfo[1] +
//                ", customerId=" + RelatedCustomerId + " }" );
//
//
//    }
}
