package edu.teamv;

import edu.teamv.transactions.Transaction;
import edu.teamv.transactions.impl.*;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionTest {
    @Test
    public void deliveryTest() {
        String parameters[] = {"1", "1"};
        try {
            Transaction transaction = new DeliveryTransaction(parameters);
            transaction.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void paymentTest() {
        String parameters[] = {"1", "1", "1", "100.98"};
        try {
            Transaction transaction = new PaymentTransaction(parameters);
            transaction.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void topBalanceTest() {
        String[] parameters = {};
        Transaction transaction = null;
        try {
            transaction = new TopBalanceTransaction(parameters);
            transaction.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void popularItemTest () {
        String[] parameters = {"1", "1", "10"};
        try {
            Transaction transaction = new PopularItemTransaction(parameters);
            transaction.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void newOrderTest () {

        String[] customerIdentify = {"2115", "1", "5", "16"};
        ArrayList<String[]> itemsInfo = new ArrayList<>();
        itemsInfo.add(new String[]{"87749", "1", "1"});
        itemsInfo.add(new String[]{"64487", "1", "6"});
        itemsInfo.add(new String[]{"97991", "1", "3"});
        itemsInfo.add(new String[]{"97508", "1", "4"});
        itemsInfo.add(new String[]{"23638", "1", "6"});
        itemsInfo.add(new String[]{"77139", "1", "1"});
        itemsInfo.add(new String[]{"6069", "1", "5"});
        itemsInfo.add(new String[]{"16101", "1", "1"});
        itemsInfo.add(new String[]{"20951", "1", "4"});
        itemsInfo.add(new String[]{"31455", "1", "4"});
        itemsInfo.add(new String[]{"40675", "1", "2"});
        itemsInfo.add(new String[]{"64199", "1", "10"});
        itemsInfo.add(new String[]{"80703", "1", "3"});
        itemsInfo.add(new String[]{"87685", "1", "8"});
        itemsInfo.add(new String[]{"88468", "1", "1"});
        itemsInfo.add(new String[]{"89759", "1", "3"});


        try {
            Transaction transaction = new NewOrderTransaction(customerIdentify, itemsInfo);
            transaction.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void OrderStatusTest () {
        String[] parameters = {"1", "1", "1771"};
        try {
            Transaction transaction = new OrderStatusTransaction(parameters);
            transaction.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void StockLevelTest () {
        String[] parameters = {"1", "4", "19", "50"};
        try {
            Transaction transaction = new StockLevelTransaction(parameters);
            transaction.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void relatedCustomerTest () {
        String[] parameters = {"1", "5", "2675"};
        try {
            Transaction transaction = new RelatedCustomerTransaction(parameters);
            transaction.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
