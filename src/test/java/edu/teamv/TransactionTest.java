package edu.teamv;

import edu.teamv.transactions.Transaction;
import edu.teamv.transactions.impl.Delivery;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TransactionTest {
    @Test
    public void DeliveryTest() {
        String parameters[] = {"1", "1"};
        try {
            Transaction transaction = new Delivery(parameters);
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
