package edu.teamv.pojo;

import java.math.BigDecimal;

public class Warehouse {

    private Integer warehouseID;

    private String warehouseName;

    private String street1;

    private String street2;

    private String city;

    private String state;

    private String zip;

    private BigDecimal taxRate;

    private BigDecimal yearToDatePayment;

    public Integer getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(Integer warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getYearToDatePayment() {
        return yearToDatePayment;
    }

    public void setYearToDatePayment(BigDecimal yearToDatePayment) {
        this.yearToDatePayment = yearToDatePayment;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseID=" + warehouseID +
                ", warehouseName='" + warehouseName + '\'' +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", taxRate=" + taxRate +
                ", yearToDatePayment=" + yearToDatePayment +
                '}';
    }
}
