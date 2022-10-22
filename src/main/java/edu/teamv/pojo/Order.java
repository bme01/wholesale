package edu.teamv.pojo;

import java.sql.Timestamp;

public class Order {

    private Integer wareHouseID;
    private Integer DistrictID;
    private Integer OrderID;
    private Integer CustomerID;
    private Integer carrierID;
    private Integer orderLineCount;
    private Integer orderStatus;
    private Timestamp orderEntry;



    public Integer getWareHouseID() {
        return wareHouseID;
    }

    public void setWareHouseID(Integer wareHouseID) {
        this.wareHouseID = wareHouseID;
    }

    public Integer getDistrictID() {
        return DistrictID;
    }

    public void setDistrictID(Integer districtID) {
        DistrictID = districtID;
    }

    public Integer getOrderID() {
        return OrderID;
    }

    public void setOrderID(Integer orderID) {
        OrderID = orderID;
    }

    public Integer getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(Integer customerID) {
        CustomerID = customerID;
    }

    public Integer getCarrierID() {
        return carrierID;
    }

    public void setCarrierID(Integer carrierID) {
        this.carrierID = carrierID;
    }

    public Integer getOrderLineCount() {
        return orderLineCount;
    }

    public void setOrderLineCount(Integer orderLineCount) {
        this.orderLineCount = orderLineCount;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Timestamp getOrderEntry() {
        return orderEntry;
    }

    public void setOrderEntry(Timestamp orderEntry) {
        this.orderEntry = orderEntry;
    }

    @Override
    public String toString() {
        return "Order{" +
                "wareHouseID=" + wareHouseID +
                ", DistrictID=" + DistrictID +
                ", OrderID=" + OrderID +
                ", CustomerID=" + CustomerID +
                ", carrierID=" + carrierID +
                ", orderLineCount=" + orderLineCount +
                ", orderStatus=" + orderStatus +
                ", orderEntry=" + orderEntry +
                '}';
    }
}
