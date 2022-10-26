package edu.teamv.pojo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderLine {
    private Integer warehouseID;
    private Integer districtID;
    private Integer orderID;
    private Integer orderLineId;
    private Integer itemID;
    private Timestamp timeOfDelivery;
    private BigDecimal totalPrice;
    private Integer SupplyingWarehouseId;
    private Integer quantityOfItem;
    private String Miscellaneous;

    public Integer getWarehouseID() {
        return warehouseID;
    }

    public Integer getDistrictID() {
        return districtID;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public Integer getOrderLineId() {
        return orderLineId;
    }

    public Integer getItemID() {
        return itemID;
    }

    public Timestamp getTimeOfDelivery() {
        return timeOfDelivery;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Integer getSupplyingWarehouseId() {
        return SupplyingWarehouseId;
    }

    public Integer getQuantityOfItem() {
        return quantityOfItem;
    }

    public String getMiscellaneous() {
        return Miscellaneous;
    }

    public void setWarehouseID(Integer warehouseID) {
        this.warehouseID = warehouseID;
    }

    public void setDistrictID(Integer districtID) {
        this.districtID = districtID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public void setOrderLineId(Integer orderLineId) {
        this.orderLineId = orderLineId;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public void setTimeOfDelivery(Timestamp timeOfDelivery) {
        this.timeOfDelivery = timeOfDelivery;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setSupplyingWarehouseId(Integer supplyingWarehouseId) {
        SupplyingWarehouseId = supplyingWarehouseId;
    }

    public void setQuantityOfItem(Integer quantityOfItem) {
        this.quantityOfItem = quantityOfItem;
    }

    public void setMiscellaneous(String miscellaneous) {
        Miscellaneous = miscellaneous;
    }

}
