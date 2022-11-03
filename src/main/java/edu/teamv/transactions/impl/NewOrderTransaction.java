package edu.teamv.transactions.impl;

import edu.teamv.pojo.Order;
import edu.teamv.pojo.OrderLine;
import edu.teamv.transactions.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class NewOrderTransaction extends Transaction {
    private final Integer customerWarehouseID;
    private final Integer customerDistrictID;
    private final Integer customerID;
    private final Integer numberOfItems;

    private OrderItem[] orderItems;


    private BigDecimal warehouseTax;
    private BigDecimal districtTax;
    private BigDecimal customerDiscount;

    private String customerLastName;

    private String customerCredit;

    private Timestamp orderCreatedTime;

    PreparedStatement preparedStatementForOrderLine;

    PreparedStatement preparedStatementForCustomerOrderLine;
    Connection connection;
    private class OrderItem {
        public Integer itemNumber;
        public Integer supplierWarehouse;
        public Integer quantity;
        public String itemName;
        public BigDecimal orderLineAmount;
        public  Integer stockQuantity;
    }

    public NewOrderTransaction(String[] customerIdentify, ArrayList<String[]> itemsInfoList)
            throws SQLException, IOException, ClassNotFoundException {
        super(customerIdentify);
        customerID = Integer.parseInt(customerIdentify[0]);
        customerWarehouseID = Integer.parseInt(customerIdentify[1]);
        customerDistrictID = Integer.parseInt(customerIdentify[2]);
        numberOfItems = Integer.parseInt(customerIdentify[3]);
        orderItems = new OrderItem[numberOfItems];
        int i = 0;
        for(String[] item : itemsInfoList){
            OrderItem orderItem = new OrderItem();
            orderItem.itemNumber = Integer.parseInt(item[0]);
            orderItem.supplierWarehouse = Integer.parseInt(item[1]);
            orderItem.quantity = Integer.parseInt(item[2]);
            orderItems[i] = orderItem;
            i++;

        }
        connection = super.getConnection();
    }

    @Override
    public void execute() throws SQLException {
        try {
            Integer N = getNextOrderNumber();
            updateDistrict();
            createNewOrder(N);
            BigDecimal totalAmount = BigDecimal.valueOf(Float.valueOf(0));

            String createNewOrderLineSql = "insert into wholesale.order_line \n" +
                    "( ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id, \n" +
                    " ol_supply_w_id, ol_quantity, ol_amount, ol_delivery_d, ol_dist_info ) \n" +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatementForOrderLine = connection.prepareStatement(createNewOrderLineSql);
            String createNewCustomerOrderLineSql = "insert into wholesale.customer_order_items \n" +
                    "(coi_w_id, coi_d_id, coi_c_id, coi_o_id, coi_i_id) \n" +
                    "values (?, ?, ?, ?, ?)";
            preparedStatementForCustomerOrderLine = connection.prepareStatement(createNewCustomerOrderLineSql);


            for (int i = 0; i < numberOfItems; i++){
                updateStock(orderItems, i);
                BigDecimal itemAmount = getItemAmount(orderItems, i);
                totalAmount = totalAmount.add(itemAmount);
                createNewOrderLine(i + 1, N, orderItems, i);
            }
            preparedStatementForOrderLine.executeBatch();
            preparedStatementForCustomerOrderLine.executeBatch();
            BigDecimal finalAmount = calculateTotalAmount(totalAmount);

            printFinalInfo(N, finalAmount);
            //connection.commit();
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

    private Integer getNextOrderNumber() throws SQLException {
        String getNextOrderSql = "select d_next_oid \n" +
                " from wholesale.district \n"+
                " where d_w_id = ? and d_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(getNextOrderSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer res = 0;
        if(resultSet.next()){
            res = resultSet.getInt(1);
        }

        preparedStatement.close();

        return res;
    }
    private void updateDistrict() throws SQLException {
        String updateDistrictNextOrderNumberSql = "update wholesale.district set d_next_oid = d_next_oid + 1 \n" +
                "where d_w_id = ? and d_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(updateDistrictNextOrderNumberSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private void createNewOrder(Integer newOrderId) throws SQLException {
        Order newOrder = new Order();
        newOrder.setOrderID(newOrderId);
        newOrder.setWarehouseID(customerWarehouseID);
        newOrder.setDistrictID(customerDistrictID);
        newOrder.setCustomerID(customerID);
        Date date = new Date();
        orderCreatedTime = new Timestamp(date.getTime());
        newOrder.setOrderEntry(orderCreatedTime);
        newOrder.setCarrierID(null);
        newOrder.setOrderLineCount(numberOfItems);
        newOrder.setOrderStatus(1);
        for(int i = 0; i < orderItems.length; i++){
            if(orderItems[i].supplierWarehouse != customerWarehouseID){
                newOrder.setOrderStatus(0);
                break;
            }
        }

        String createNewOrderSql = "insert into wholesale.\"order\" \n" +
                "( o_w_id , o_d_id , o_id , o_c_id, o_carrier_id,\n" +
                " o_ol_cnt, o_all_local, o_entry_d ) \n" +
                "Values (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(createNewOrderSql);
        preparedStatement.setInt(1, newOrder.getWarehouseID());
        preparedStatement.setInt(2, newOrder.getDistrictID());
        preparedStatement.setInt(3, newOrder.getOrderID());
        preparedStatement.setInt(4, newOrder.getCustomerID());
        preparedStatement.setObject(5, newOrder.getCustomerID(), java.sql.Types.INTEGER);
        preparedStatement.setInt(6, newOrder.getOrderLineCount());
        preparedStatement.setInt(7, newOrder.getOrderStatus());
        preparedStatement.setTimestamp(8, newOrder.getOrderEntry());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private void updateStock(OrderItem[] orderItems, int index) throws SQLException {
        String getStockQuantitySql = "select s_quantity \n" +
                " from wholesale.stock \n" +
                " where s_w_id = ? and s_i_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getStockQuantitySql);
        preparedStatement.setInt(1, orderItems[index].supplierWarehouse);
        preparedStatement.setInt(2, orderItems[index].itemNumber);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer stockQuantity = 0;
        if(resultSet.next()) {
            stockQuantity = resultSet.getInt(1);
        }
        Integer adjustedQuantity = stockQuantity - orderItems[index].quantity;
        if(adjustedQuantity < 10){
            adjustedQuantity += 100;
        }
        orderItems[index].stockQuantity = adjustedQuantity;

        Integer needRemoteWarehouse = 0;
        if(!orderItems[index].supplierWarehouse.equals(customerWarehouseID)){
            needRemoteWarehouse = 1;
        }

        String updateStockQuantitySql = "update wholesale.stock \n" +
                " set s_quantity = ?, s_ytd = s_ytd + ?, \n" +
                "s_ordercnt = s_ordercnt + 1, s_remote_cnt = s_remote_cnt + ? \n" +
                "where s_w_id = ? and s_i_id = ?";
        preparedStatement = connection.prepareStatement(updateStockQuantitySql);
        preparedStatement.setInt(1, adjustedQuantity);
        preparedStatement.setInt(2, orderItems[index].quantity);
        preparedStatement.setInt(3, needRemoteWarehouse);
        preparedStatement.setInt(4, orderItems[index].supplierWarehouse);
        preparedStatement.setInt(5, orderItems[index].itemNumber);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private BigDecimal getItemAmount(OrderItem[] orderItems, int index) throws SQLException {
        String getItemPriceSql = "select i_price, i_name from wholesale.item where i_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getItemPriceSql);
        preparedStatement.setInt(1, orderItems[index].itemNumber);
        ResultSet resultSet = preparedStatement.executeQuery();
        BigDecimal itemPrice = BigDecimal.valueOf(0);
        if(resultSet.next()){
            itemPrice = resultSet.getBigDecimal(1);
            orderItems[index].itemName = resultSet.getString(2);
        }
        BigDecimal itemAmount = itemPrice.multiply(BigDecimal.valueOf(orderItems[index].quantity));
        orderItems[index].orderLineAmount = itemAmount;
        preparedStatement.close();

        return itemAmount;
    }

    private void createNewOrderLine(Integer orderLineNumber, Integer orderId,
                                    OrderItem[] orderItems, int index) throws SQLException {

        String getDistinctionInfoSql = "select s_dist_" + String.format("%02d", customerDistrictID) +
                " from wholesale.stock \n" +
                " where s_w_id = ? and s_i_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getDistinctionInfoSql);
        preparedStatement.setInt(1, orderItems[index].itemNumber);
        preparedStatement.setInt(2, orderItems[index].itemNumber);
        ResultSet resultSet = preparedStatement.executeQuery();

        String distinctionInfo = "";
        if(resultSet.next()) {
            distinctionInfo = resultSet.getString(1);
        }
        preparedStatement.close();

        preparedStatementForOrderLine.setInt(1, orderId);
        preparedStatementForOrderLine.setInt(2, customerDistrictID);
        preparedStatementForOrderLine.setInt(3, customerWarehouseID);
        preparedStatementForOrderLine.setInt(4, orderLineNumber);
        preparedStatementForOrderLine.setInt(5, orderItems[index].itemNumber);
        preparedStatementForOrderLine.setInt(6, orderItems[index].supplierWarehouse);
        preparedStatementForOrderLine.setInt(7, orderItems[index].quantity);
        preparedStatementForOrderLine.setBigDecimal(8, orderItems[index].orderLineAmount);
        preparedStatementForOrderLine.setTimestamp(9, null);
        preparedStatementForOrderLine.setString(10, distinctionInfo);
        preparedStatementForOrderLine.addBatch();

        preparedStatementForCustomerOrderLine.setInt(1, customerWarehouseID);
        preparedStatementForCustomerOrderLine.setInt(2, customerDistrictID);
        preparedStatementForCustomerOrderLine.setInt(3, customerID);
        preparedStatementForCustomerOrderLine.setInt(4, orderId);
        preparedStatementForCustomerOrderLine.setInt(5, orderItems[index].itemNumber);
        preparedStatementForCustomerOrderLine.addBatch();


    }

    private BigDecimal calculateTotalAmount(BigDecimal totalAmount) throws SQLException {
        String getWarehouseTaxSql = "select w_tax from wholesale.warehouse where w_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getWarehouseTaxSql);
        preparedStatement.setInt(1, customerWarehouseID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            warehouseTax = resultSet.getBigDecimal(1);
        }
        String getDistrictTaxSql = "select d_tax from wholesale.district where d_w_id = ? and d_id = ?";
        preparedStatement = connection.prepareStatement(getDistrictTaxSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            districtTax = resultSet.getBigDecimal(1);
        }
        String getCustomerInfoSql = "select c_last, c_credit, c_discount from wholesale.customer \n" +
                "where c_w_id = ? and c_d_id = ? and c_id = ?";
        preparedStatement = connection.prepareStatement(getCustomerInfoSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.setInt(3, customerID);
        resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            customerLastName = resultSet.getString(1);
            customerCredit = resultSet.getString(2);
            customerDiscount = resultSet.getBigDecimal(3);
        }
        //TOTAL_AMOUNT = TOTAL_AMOUNT × (1 + D_TAX + W_TAX) × (1 − C_DISCOUNT)
        BigDecimal finalAmount = totalAmount.multiply(warehouseTax.add(districtTax).add(BigDecimal.valueOf(1)))
                .multiply(customerDiscount.negate().add(BigDecimal.valueOf(1)));
        preparedStatement.close();
        return finalAmount;
    }

    void printFinalInfo(Integer newOrderId, BigDecimal totalAmount) throws SQLException {

        System.out.println("======New Order Transaction======");

        //print Customer identifier (W_ID, D_ID, C_ID), lastname C_LAST, credit C_CREDIT, discount C_DISCOUNT
        System.out.println("Customer{" +
                " warehouseID=" + customerWarehouseID +
                ", districtId=" + customerDistrictID +
                ", customerId=" + customerID +
                ", lastname=" + customerLastName +
                ", credit=" + customerCredit +
                ", discount=" + customerDiscount +
                "}"
        );

        System.out.println("Warehouse tax rate: " + warehouseTax +
                ", District tax rate: " + districtTax);


        System.out.println("Order number: " + newOrderId +
                ", entry date: " + orderCreatedTime);

        System.out.println("Number of items: " + numberOfItems +
                ", Total amount for order: " + totalAmount);


        for (int i = 0; i < numberOfItems; i++){

            System.out.println("Item Number: " + orderItems[i].itemNumber +
                    ", Item name: " + orderItems[i].itemName +
                    ", Supplier Warehouse: " + orderItems[i].supplierWarehouse +
                    ", Item Quantity: " + orderItems[i].quantity +
                    ", OrderLine Amount: "  + orderItems[i].orderLineAmount +
                    ", Stock Quantity: " + orderItems[i].stockQuantity);

        }

    }

}
