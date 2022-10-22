package edu.teamv.pojo;

import java.math.BigDecimal;

public class District {

    private Integer warehouseID;

    private Integer districtID;

    private String districtName;

    private String street1;

    private String street2;

    private String city;

    private String state;

    private String zip;

    private BigDecimal taxRate;

    private BigDecimal yearToDatePayment;

    private Integer nextOrderID;

    public Integer getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(Integer warehouseID) {
        this.warehouseID = warehouseID;
    }

    public Integer getDistrictID() {
        return districtID;
    }

    public void setDistrictID(Integer districtID) {
        this.districtID = districtID;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
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

    public Integer getNextOrderID() {
        return nextOrderID;
    }

    public void setNextOrderID(Integer nextOrderID) {
        this.nextOrderID = nextOrderID;
    }

    @Override
    public String toString() {
        return "District{" +
                "warehouseID=" + warehouseID +
                ", districtID=" + districtID +
                ", districtName='" + districtName + '\'' +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", taxRate=" + taxRate +
                ", yearToDatePayment=" + yearToDatePayment +
                ", nextOrderID=" + nextOrderID +
                '}';
    }
}
