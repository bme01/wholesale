package edu.teamv.pojo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Customer {

    private Integer warehouseID;

    private Integer districtId;

    private Integer customerId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String street1;

    private String street2;

    private String city;

    private String state;

    private String zip;

    private String phone;

    private Timestamp since;

    private String credit;

    private BigDecimal creditLimit;

    private BigDecimal discount;

    private BigDecimal balance;

    private Float yearToDatePayment;

    private Integer paymentCount;

    private Integer deliveryCount;

    private String data;

    public Integer getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(Integer warehouseID) {
        this.warehouseID = warehouseID;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getSince() {
        return since;
    }

    public void setSince(Timestamp since) {
        this.since = since;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Float getYearToDatePayment() {
        return yearToDatePayment;
    }

    public void setYearToDatePayment(Float yearToDatePayment) {
        this.yearToDatePayment = yearToDatePayment;
    }

    public Integer getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(Integer paymentCount) {
        this.paymentCount = paymentCount;
    }

    public Integer getDeliveryCount() {
        return deliveryCount;
    }

    public void setDeliveryCount(Integer deliveryCount) {
        this.deliveryCount = deliveryCount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "warehouseID=" + warehouseID +
                ", districtId=" + districtId +
                ", customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", phone='" + phone + '\'' +
                ", since=" + since +
                ", credit='" + credit + '\'' +
                ", creditLimit=" + creditLimit +
                ", discount=" + discount +
                ", balance=" + balance +
                ", yearToDatePayment=" + yearToDatePayment +
                ", paymentCount=" + paymentCount +
                ", deliveryCount=" + deliveryCount +
                ", data='" + data + '\'' +
                '}';
    }
}

