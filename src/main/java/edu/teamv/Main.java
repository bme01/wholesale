package edu.teamv;


import edu.teamv.transactions.Transaction;
import edu.teamv.transactions.impl.*;
import edu.teamv.utils.PerformanceMeasurementUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {


    public static void main(String... args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); //NOPMD
        int NumberOfTransactions = 0;
        String commandString;

        try {
            while ((commandString = reader.readLine()) != null) {

                parseAndEvaluate(commandString, reader);
                NumberOfTransactions++;

            }
            reader.close();
            System.out.println("Summary: ");
            System.out.println("Total number of transactions processed: " + NumberOfTransactions);
            return; // EOF found, Streams are closed, terminate process
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // return; // Streams are closed, terminate process

        } finally {
            PerformanceMeasurementUtil.report();
        }
        // PerformanceMeasurementUtil.detailedReport();
    }

    static public void parseAndEvaluate(String commandString, BufferedReader reader)
            throws IOException, SQLException, ClassNotFoundException {
        String[] command = commandString.split(",");
        switch (command[0]) {
            case "N": {
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                ArrayList<String[]> itemsInfoList = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(command[4]); i++) {
                    itemsInfoList.add(reader.readLine().split(","));
                }
                Transaction transaction = new NewOrderTransaction(customeridentifier, itemsInfoList);
                // PerformanceMeasurementUtil.run(transaction::execute);
                PerformanceMeasurementUtil.performanceTest(transaction);
                break;
            }

            case "O": {
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new OrderStatusTransaction(customeridentifier);
                // PerformanceMeasurementUtil.run(transaction::execute);
                PerformanceMeasurementUtil.performanceTest(transaction);
                break;
            }

            case "S": {
                String[] parameters = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new StockLevelTransaction(parameters);
                // PerformanceMeasurementUtil.run(transaction::execute);
                PerformanceMeasurementUtil.performanceTest(transaction);
                break;
            }
            case "R": {
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                // System.out.println("Related customer transaction skipped: " + Arrays.toString(customeridentifier));
                Transaction transaction = new RelatedCustomerTransaction(customeridentifier);
                // PerformanceMeasurementUtil.run(transaction::execute);
                PerformanceMeasurementUtil.performanceTest(transaction);
                break;
            }

            case "P": {
                String[] parameters = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new PaymentTransaction(parameters);
                // PerformanceMeasurementUtil.run(transaction::execute);
                PerformanceMeasurementUtil.performanceTest(transaction);
                break;
            }

            case "D": {
                String[] parameters = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new DeliveryTransaction(parameters);
                // PerformanceMeasurementUtil.run(transaction::execute);
                PerformanceMeasurementUtil.performanceTest(transaction);
                break;
            }

            case "I": {
                String[] parameters = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new PopularItemTransaction(parameters);
                // PerformanceMeasurementUtil.run(transaction::execute);
                PerformanceMeasurementUtil.performanceTest(transaction);
                break;
            }

            case "T": {
                String[] parameters = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new TopBalanceTransaction(parameters);
                // PerformanceMeasurementUtil.run(transaction::execute);
                PerformanceMeasurementUtil.performanceTest(transaction);
                break;
            }

            default:
                System.out.println("No such transaction: " + command);
        }
        PerformanceMeasurementUtil.report();
    }
}

