package edu.teamv.utils;

import edu.teamv.transactions.Transaction;
import edu.teamv.transactions.impl.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PerformanceMeasurementUtil {

    private static final Statistics overallStatistics = new Statistics();

    private static final Statistics deliveryStatistics = new Statistics();

    private static final Statistics newOrderStatistics = new Statistics();

    private static final Statistics orderStatusStatistics = new Statistics();

    private static final Statistics paymentStatistics = new Statistics();

    private static final Statistics popularItemStatistics = new Statistics();

    private static final Statistics relatedCustomerStatistics = new Statistics();

    private static final Statistics stockLevelStatistics = new Statistics();

    private static final Statistics topBalanceStatistics = new Statistics();


    public static <E extends Throwable> void run(Function<E> function) {
        Watch watch = new Watch();
        watch.start();
        try {
            function.exec();
            watch.stop();
            double latency = (double) watch.execNs / 1_000_000;
            overallStatistics.executeOne(latency);
            System.out.println("===========================");
            System.out.println("Latency: " + latency);
            System.out.println("Executed Transactions: " + overallStatistics.getExecuted());
            System.out.println("===========================");
        } catch (Throwable t) {
            overallStatistics.failOne();
            // throw new RuntimeException(t);
        }
    }

    public static void performanceTest(Transaction transaction) {

        Watch watch = new Watch();
        watch.start();
        try {
            transaction.execute();
            watch.stop();
            double latency = (double) watch.execNs / 1_000_000;
            overallStatistics.executeOne(latency);
            System.out.println("===========================");
            System.out.println("Latency: " + latency);
            System.out.println("Executed Transactions: " + overallStatistics.getExecuted());
            System.out.println("===========================");

            if (transaction instanceof DeliveryTransaction) {
                deliveryStatistics.executeOne(latency);
            }

            if (transaction instanceof NewOrderTransaction) {
                newOrderStatistics.executeOne(latency);
            }

            if (transaction instanceof OrderStatusTransaction) {
                orderStatusStatistics.executeOne(latency);
            }

            if (transaction instanceof PaymentTransaction) {
                paymentStatistics.executeOne(latency);
            }

            if (transaction instanceof PopularItemTransaction) {
                popularItemStatistics.executeOne(latency);
            }

            if (transaction instanceof RelatedCustomerTransaction) {
                relatedCustomerStatistics.executeOne(latency);
            }

            if (transaction instanceof StockLevelTransaction) {
                stockLevelStatistics.executeOne(latency);
            }

            if (transaction instanceof TopBalanceTransaction) {
                topBalanceStatistics.executeOne(latency);
            }


        } catch (Throwable t) {
            overallStatistics.failOne();

            if (transaction instanceof DeliveryTransaction) {
                deliveryStatistics.failOne();
            }

            if (transaction instanceof NewOrderTransaction) {
                newOrderStatistics.failOne();
            }

            if (transaction instanceof OrderStatusTransaction) {
                orderStatusStatistics.failOne();
            }

            if (transaction instanceof PaymentTransaction) {
                paymentStatistics.failOne();
            }

            if (transaction instanceof PopularItemTransaction) {
                popularItemStatistics.failOne();
            }

            if (transaction instanceof RelatedCustomerTransaction) {
                relatedCustomerStatistics.failOne();
            }

            if (transaction instanceof StockLevelTransaction) {
                stockLevelStatistics.failOne();
            }

            if (transaction instanceof TopBalanceTransaction) {
                topBalanceStatistics.failOne();
            }
        }


    }

    public static class Watch {
        private long startNs;
        private long endNs;
        private long execNs;

        private void start() {
            this.startNs = System.nanoTime();
        }

        private void stop() {
            this.endNs = System.nanoTime();
            this.execNs = this.endNs - this.startNs;
        }

    }

    public static class Statistics {
        private int executed = 0;
        private int failed = 0;

        private double totalExecutionTime;
        private final List<Double> latencyList = new ArrayList<>();

        private boolean sorted = false;

        private void executeOne(double latency) {
            executed++;
            totalExecutionTime += latency;
            latencyList.add(latency);
            sorted = false;
        }

        private void failOne() {
            failed++;
        }

        private double throughput() {
            return executed / (totalExecutionTime / 1_000);
        }

        private double average() {
            return totalExecutionTime / executed;
        }


        private double median() {
            if (!sorted) {
                sort();
            }
            return latencyList.get(executed / 2);
        }

        private double percentile95() {
            if (!sorted) {
                sort();
            }
            return latencyList.get((int) (executed * 0.95));
        }

        private double percentile99() {
            if (!sorted) {
                sort();
            }
            return latencyList.get((int) (executed * 0.99));
        }

        private void sort() {
            Collections.sort(latencyList);
            sorted = true;
        }

        private void show() {
            if (latencyList.isEmpty()) return;
            System.out.println(String.format("%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",
                    executed, totalExecutionTime / 1_000, throughput(), average(), median(), percentile95(), percentile99()));

        }

        private void show(String transactionName) {
            if (latencyList.isEmpty()) return;
            System.out.println(String.format("%s: %d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f; Aborted: %d",
                    transactionName, executed, totalExecutionTime / 1_000, throughput(), average(), median(), percentile95(), percentile99(), failed));

        }

        private void toStderr(String clientNum) {
            System.err.println(String.format(String.format("%s,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",
                    clientNum, executed, totalExecutionTime / 1_000, throughput(), average(), median(), percentile95(), percentile99())));
        }

        public int getExecuted() {
            return executed;
        }
    }

    public static void report() {
        overallStatistics.show();
    }

    public static void report(String clientNum) {
        overallStatistics.toStderr(clientNum);
    }

    public static void detailedReport() {
        overallStatistics.show();

        deliveryStatistics.show("Delivery");

        newOrderStatistics.show("New Order");

        orderStatusStatistics.show("Order Status");

        paymentStatistics.show("Payment");

        popularItemStatistics.show("Popular Items");

        relatedCustomerStatistics.show("Related Customer");

        stockLevelStatistics.show("Stock Level");

        topBalanceStatistics.show("Top Balance");

    }

    public static void reportThroughput() throws IOException {

        String clientsPath = "." + File.separator + "clients.csv";
        Stream<String> stream = Files.lines(Paths.get(clientsPath));
        List<String> lines = stream.collect(Collectors.toList());

        List<Double> throughputList = new ArrayList<>();
        double total = 0;

        for (String line : lines) {
            String[] splits = line.split(",");
            double throughput = Double.parseDouble(splits[3]);
            throughputList.add(throughput);
            total += throughput;
        }

        Collections.sort(throughputList);
        System.out.println(String.format("%f,%f,%f", throughputList.get(0), throughputList.get(throughputList.size() - 1), total / throughputList.size()));
    }

    @FunctionalInterface
    public interface Function<E extends Throwable> {
        void exec() throws E;
    }
}

