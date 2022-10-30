package edu.teamv;



import edu.teamv.transactions.Transaction;
import edu.teamv.transactions.impl.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {


    public static void main(String... args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); //NOPMD
        Main main = new Main();
        int NumberOfTransactions = 0;

        while (true) {
            try {
                String commandString;
                try {
                    commandString = reader.readLine();

                } catch (IOException e) {
                    reader.close();
                    return; // Streams are closed, terminate process
                }

                if (commandString == null) {
                    reader.close();
                    System.out.println("Summary: ");
                    System.out.println("Total number of transactions processed: " + NumberOfTransactions);
                    return; // EOF found, Streams are closed, terminate process
                }

                parseAndEvaluate(commandString, reader);
                NumberOfTransactions ++;

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static public void parseAndEvaluate(String commandString, BufferedReader reader)
            throws IOException, SQLException, ClassNotFoundException {
        String[] command = commandString.split(",");
        switch (command[0]){
            case "N": {
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                ArrayList<String[]> itemsInfoList = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(command[4]); i++) {
                    itemsInfoList.add(reader.readLine().split(","));
                    ;
                }
                Transaction transaction = new NewOrderTransaction(customeridentifier, itemsInfoList);
                transaction.execute();
                break;
            }

            case "O":{
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new OrderStatusTransaction(customeridentifier);
                transaction.execute();
                break;
            }

            case "S":{
                String[] parameters = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new StockLevelTransaction(parameters);
                transaction.execute();
                break;
            }
            case "R":{
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new RelatedCustomerTransaction(customeridentifier);
                transaction.execute();
                break;
            }

            case "P":{
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new PaymentTransaction(customeridentifier);
                transaction.execute();
                break;
            }

            case "D":{
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new DeliveryTransaction(customeridentifier);
                transaction.execute();
                break;
            }

            case "I":{
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new PopularItemTransaction(customeridentifier);
                transaction.execute();
                break;
            }

            case "T":{
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                Transaction transaction = new TopBalanceTransaction(customeridentifier);
                transaction.execute();
                break;
            }

            default:
                System.out.println("No such transaction: " + command);
        }
    }
}

