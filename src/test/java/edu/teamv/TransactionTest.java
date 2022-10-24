package edu.teamv;

import edu.teamv.transactions.Transaction;
import edu.teamv.transactions.impl.DeliveryTransaction;
import edu.teamv.transactions.impl.PaymentTransaction;
import edu.teamv.transactions.impl.TopBalanceTransaction;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        transaction.execute();
    }
}
