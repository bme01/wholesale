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

    private final ArrayList<Integer> itemNumber;
    private final ArrayList<Integer> supplierWarehouse;
    private final ArrayList<Integer> quantity;

    private  ArrayList<String> itemNames;

    private BigDecimal warehouseTax;
    private BigDecimal districtTax;
    private BigDecimal customerDiscount;

    private String customerLastName;

    private String customerCredit;
    Connection connection;

    public NewOrderTransaction(String[] customerIdentify, ArrayList<String[]> itemsInfoList)
            throws SQLException, IOException, ClassNotFoundException {
        super(customerIdentify);
        customerID = Integer.parseInt(customerIdentify[0]);
        customerWarehouseID = Integer.parseInt(customerIdentify[1]);
        customerDistrictID = Integer.parseInt(customerIdentify[2]);
        numberOfItems = Integer.parseInt(customerIdentify[3]);
        itemNumber = new ArrayList<>();
        supplierWarehouse = new ArrayList<>();
        quantity = new ArrayList<>();
        for(String[] item : itemsInfoList){
            itemNumber.add(Integer.parseInt(item[0]));
            supplierWarehouse.add(Integer.parseInt(item[1]));
            quantity.add(Integer.parseInt(item[2]));
        }
        itemNames = new ArrayList<>();
        connection = super.getConnection();
    }

    @Override
    public void execute() throws SQLException {
        try {
            Integer N = getNextOrderNumber();
            updateDistrict();
            createNewOrder(N);
            BigDecimal totalAmount = BigDecimal.valueOf(Float.valueOf(0));
            for (int i = 0; i < numberOfItems; i++){
                updateStock(itemNumber.get(i), supplierWarehouse.get(i), quantity.get(i));
                BigDecimal itemAmount = getItemAmount(itemNumber.get(i), quantity.get(i));
                totalAmount = totalAmount.add(itemAmount);
                createNewOrderLine(i + 1, N, itemNumber.get(i),
                        supplierWarehouse.get(i), quantity.get(i), itemAmount);
            }
            BigDecimal finalAmount = calculateTotalAmount(totalAmount);

            printFinalInfo(N, finalAmount);

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
        newOrder.setOrderEntry(new Timestamp(date.getTime()));
        newOrder.setCarrierID(null);
        newOrder.setOrderLineCount(numberOfItems);
        HashSet<Integer> supplierWarehouseSet = new HashSet<>(supplierWarehouse);
        if(supplierWarehouseSet.equals(new HashSet<>(customerWarehouseID))){
            newOrder.setOrderStatus(1);
        }else{
            newOrder.setOrderStatus(0);
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

    private void updateStock(Integer itemId, Integer supplierWarehouseId, Integer quantityOfItem) throws SQLException {
        String getStockQuantitySql = "select s_quantity \n" +
                " from wholesale.stock \n" +
                " where s_w_id = ? and s_i_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getStockQuantitySql);
        preparedStatement.setInt(1, supplierWarehouseId);
        preparedStatement.setInt(2, itemId);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer stockQuantity = 0;
        if(resultSet.next()) {
            stockQuantity = resultSet.getInt(1);
        }
        Integer adjustedQuantity = stockQuantity - quantityOfItem;
        if(adjustedQuantity < 10){
            adjustedQuantity += 100;
        }
        Integer needRemoteWarehouse = 0;
        if(!supplierWarehouseId.equals(customerWarehouseID)){
            needRemoteWarehouse = 1;
        }

        String updateStockQuantitySql = "update wholesale.stock \n" +
                " set s_quantity = ?, s_ytd = s_ytd + ?, \n" +
                "s_ordercnt = s_ordercnt + 1, s_remote_cnt = s_remote_cnt + ? \n" +
                "where s_w_id = ? and s_i_id = ?";
        preparedStatement = connection.prepareStatement(updateStockQuantitySql);
        preparedStatement.setInt(1, adjustedQuantity);
        preparedStatement.setInt(2, quantityOfItem);
        preparedStatement.setInt(3, needRemoteWarehouse);
        preparedStatement.setInt(4, supplierWarehouseId);
        preparedStatement.setInt(5, itemId);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private BigDecimal getItemAmount(Integer itemId, Integer quantityOfItem) throws SQLException {
        String getItemPriceSql = "select i_price, i_name from wholesale.item where i_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getItemPriceSql);
        preparedStatement.setInt(1, itemId);
        ResultSet resultSet = preparedStatement.executeQuery();
        BigDecimal itemPrice = BigDecimal.valueOf(0);
        if(resultSet.next()){
            itemPrice = resultSet.getBigDecimal(1);
            itemNames.add(resultSet.getString(2));
        }
        preparedStatement.close();

        return itemPrice.multiply(BigDecimal.valueOf(quantityOfItem));
    }

    private void createNewOrderLine(Integer orderLineNumber, Integer orderId, Integer itemId,
                                    Integer supplierWarehouseId, Integer quantityOfItem, BigDecimal itemAmount) throws SQLException {

        String getDistinctionInfoSql = "select s_dist_" + String.format("%02d", customerDistrictID) +
                " from wholesale.stock \n" +
                " where s_w_id = ? and s_i_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(getDistinctionInfoSql);
        preparedStatement.setInt(1, supplierWarehouseId);
        preparedStatement.setInt(2, itemId);
        ResultSet resultSet = preparedStatement.executeQuery();

        String distinctionInfo = "";
        if(resultSet.next()) {
            distinctionInfo = resultSet.getString(1);
        }

        OrderLine newOrderLine = new OrderLine();
        newOrderLine.setOrderID(orderId);
        newOrderLine.setDistrictID(customerDistrictID);
        newOrderLine.setWarehouseID(customerWarehouseID);
        newOrderLine.setOrderLineId(orderLineNumber);
        newOrderLine.setItemID(itemId);
        newOrderLine.setSupplyingWarehouseId(supplierWarehouseId);
        newOrderLine.setQuantityOfItem(quantityOfItem);
        newOrderLine.setTotalPrice(itemAmount);
        newOrderLine.setTimeOfDelivery(null);
        newOrderLine.setMiscellaneous(distinctionInfo);

        String createNewOrderLineSql = "insert into wholesale.order_line \n" +
                "( ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id, \n" +
                " ol_supply_w_id, ol_quantity, ol_amount, ol_delivery_d, ol_dist_info ) \n" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        preparedStatement = connection.prepareStatement(createNewOrderLineSql);
        preparedStatement.setInt(1, newOrderLine.getOrderID());
        preparedStatement.setInt(2, newOrderLine.getDistrictID());
        preparedStatement.setInt(3, newOrderLine.getWarehouseID());
        preparedStatement.setInt(4, newOrderLine.getOrderLineId());
        preparedStatement.setInt(5, newOrderLine.getItemID());
        preparedStatement.setInt(6, newOrderLine.getSupplyingWarehouseId());
        preparedStatement.setInt(7, newOrderLine.getQuantityOfItem());
        preparedStatement.setBigDecimal(8, newOrderLine.getTotalPrice());
        preparedStatement.setTimestamp(9, newOrderLine.getTimeOfDelivery());
        preparedStatement.setString(10, newOrderLine.getMiscellaneous());

        preparedStatement.executeUpdate();
        preparedStatement.close();

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

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        System.out.println("Warehouse tax rate: " + warehouseTax +
                ", District tax rate: " + districtTax);

        String getOrderInfoSql = "select o_entry_d from wholesale.\"order\" " +
                "where o_w_id = ? and o_d_id = ? and o_c_id = ? and o_id = ?";
        preparedStatement = connection.prepareStatement(getOrderInfoSql);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.setInt(3, customerID);
        preparedStatement.setInt(4, newOrderId);
        resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            System.out.println("Order number: " + newOrderId +
                    ", entry date: " + resultSet.getTimestamp(1));

            System.out.println("Number of items: " + numberOfItems +
                    ", Total amount for order: " + totalAmount);
        }

        String getOrderLineAmountsInfo = "select ol_amount from wholesale.order_line " +
                "where ol_w_id = ? and ol_d_id = ? and ol_o_id = ? order by ol_number" ;
        preparedStatement = connection.prepareStatement(getOrderLineAmountsInfo);
        preparedStatement.setInt(1, customerWarehouseID);
        preparedStatement.setInt(2, customerDistrictID);
        preparedStatement.setInt(3, newOrderId);
        resultSet = preparedStatement.executeQuery();
        ArrayList<BigDecimal> orderLineAmounts = new ArrayList<>();
        while (resultSet.next()) {
            orderLineAmounts.add(resultSet.getBigDecimal(1));
        }

        for (int i = 0; i < numberOfItems; i++){

            String itemName = itemNames.get(i);
            BigDecimal orderLineAmount = orderLineAmounts.get(i);

            String getStockQuantitySql = "select s_quantity \n" +
                    " from wholesale.stock \n" +
                    " where s_w_id = ? and s_i_id = ?";
            preparedStatement = connection.prepareStatement(getStockQuantitySql);
            preparedStatement.setInt(1, supplierWarehouse.get(i));
            preparedStatement.setInt(2, itemNumber.get(i));
            resultSet = preparedStatement.executeQuery();
            Integer stockQuantity = 0;
            if(resultSet.next()){
                stockQuantity = resultSet.getInt(1);
            }

            System.out.println("Item Number: " + itemNumber.get(i) +
                    ", Item name: " + itemName +
                    ", Supplier Warehouse: " + supplierWarehouse.get(i) +
                    ", Item Quantity: " + quantity.get(i) +
                    ", OrderLine Amount: "  + orderLineAmount +
                    ", Stock Quantity: " + stockQuantity);

        }
        preparedStatement.close();


    }

}
