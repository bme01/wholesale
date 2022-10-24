package edu.teamv;



import edu.teamv.transactions.impl.NewOrderTransaction;
import edu.teamv.transactions.impl.OrderStatusTransaction;
import edu.teamv.transactions.impl.RelatedCustomerTransaction;
import edu.teamv.transactions.impl.StockLevelTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Shell {


    public static void main(String... args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); //NOPMD
        Shell shell = new Shell();
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
                NewOrderTransaction newOrderTransaction = new NewOrderTransaction(customeridentifier, itemsInfoList);
                newOrderTransaction.execute();
                break;
            }

            case "O":{
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                OrderStatusTransaction orderStatusTransaction = new OrderStatusTransaction(customeridentifier);
                orderStatusTransaction.execute();
                break;
            }

            case "S":{
                String[] parameters = Arrays.copyOfRange(command, 1, command.length);
                StockLevelTransaction stockLevelTransaction = new StockLevelTransaction(parameters);
                stockLevelTransaction.execute();
                break;
            }
            case "R":{
                String[] customeridentifier = Arrays.copyOfRange(command, 1, command.length);
                RelatedCustomerTransaction relatedCustomerTransaction = new RelatedCustomerTransaction(customeridentifier);
                relatedCustomerTransaction.execute();
                break;
            }
        }
    }
}

