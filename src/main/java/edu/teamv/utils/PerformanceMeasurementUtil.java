package edu.teamv.utils;

import edu.teamv.transactions.Transaction;
import edu.teamv.transactions.impl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerformanceMeasurementUtil {

    private static int executedTransactions;
    private static int failedTransactions;
    private static double totalExecutionTime;
    private static final List<Double> latencyList = new ArrayList<>();

    private static int executedDeliveryTransactions;
    private static int failedDeliveryTransactions;
    private static double totalExecutionTimeOfDeliveryTransaction;
    private static final List<Double> latencyListOfDeliveryTransactions = new ArrayList<>();

    private static int executedNewOrderTransactions;
    private static int failedNewOrderTransactions;
    private static double totalExecutionTimeOfNewOrderTransaction;
    private static final List<Double> latencyListOfNewOrderTransactions = new ArrayList<>();

    private static int executedOrderStatusTransactions;
    private static int failedOrderStatusTransactions;
    private static double totalExecutionTimeOfOrderStatusTransaction;
    private static final List<Double> latencyListOfOrderStatusTransactions = new ArrayList<>();

    private static int executedPaymentTransactions;
    private static int failedPaymentTransactions;
    private static double totalExecutionTimeOfPaymentTransaction;
    private static final List<Double> latencyListOfPaymentTransactions = new ArrayList<>();

    private static int executedPopularItemTransactions;
    private static int failedPopularItemTransactions;
    private static double totalExecutionTimeOfPopularItemTransaction;
    private static final List<Double> latencyListOfPopularItemTransactions = new ArrayList<>();

    private static int executedRelatedCustomerTransactions;
    private static int failedRelatedCustomerTransactions;
    private static double totalExecutionTimeOfRelatedCustomerTransaction;
    private static final List<Double> latencyListOfRelatedCustomerTransactions = new ArrayList<>();

    private static int executedStockLevelTransactions;
    private static int failedStockLevelTransactions;
    private static double totalExecutionTimeOfStockLevelTransaction;
    private static final List<Double> latencyListOfStockLevelTransactions = new ArrayList<>();

    private static int executedTopBalanceTransactions;
    private static int failedTopBalanceTransactions;
    private static double totalExecutionTimeOfTopBalanceTransaction;
    private static final List<Double> latencyListOfTopBalanceTransactions = new ArrayList<>();


    public static <E extends Throwable> void run(Function<E> function) {
        Watch watch = new Watch();
        watch.start();
        try {
            function.exec();
            watch.stop();
            executedTransactions++;
            double latency = (double) watch.execMs / 1_000_000_000;
            latencyList.add(latency);
            totalExecutionTime += latency;
            System.out.println("===========================");
            System.out.println("Latency: " + latency);
            System.out.println("Executed Transactions: " + executedTransactions);
            System.out.println("===========================");
        } catch (Throwable t) {
            failedTransactions++;
            // throw new RuntimeException(t);
        }
    }

    public static void performanceTest(Transaction transaction) {

        Watch watch = new Watch();
        watch.start();
        try {
            transaction.execute();
            watch.stop();
            executedTransactions++;
            double latency = (double) watch.execMs / 1_000_000_000;
            latencyList.add(latency);
            totalExecutionTime += latency;
            System.out.println("===========================");
            System.out.println("Latency: " + latency);
            System.out.println("Executed Transactions: " + executedTransactions);
            System.out.println("===========================");

            if (transaction instanceof DeliveryTransaction) {
                executedDeliveryTransactions++;
                totalExecutionTimeOfDeliveryTransaction += latency;
                latencyListOfDeliveryTransactions.add(latency);
            }

            if (transaction instanceof NewOrderTransaction) {
                executedNewOrderTransactions++;
                totalExecutionTimeOfNewOrderTransaction += latency;
                latencyListOfNewOrderTransactions.add(latency);
            }

            if (transaction instanceof OrderStatusTransaction) {
                executedOrderStatusTransactions++;
                totalExecutionTimeOfOrderStatusTransaction += latency;
                latencyListOfOrderStatusTransactions.add(latency);
            }

            if (transaction instanceof PaymentTransaction) {
                executedPaymentTransactions++;
                totalExecutionTimeOfPaymentTransaction += latency;
                latencyListOfPaymentTransactions.add(latency);
            }

            if (transaction instanceof PopularItemTransaction) {
                executedPopularItemTransactions++;
                totalExecutionTimeOfPopularItemTransaction += latency;
                latencyListOfPopularItemTransactions.add(latency);
            }

            if (transaction instanceof RelatedCustomerTransaction) {
                executedRelatedCustomerTransactions++;
                totalExecutionTimeOfRelatedCustomerTransaction += latency;
                latencyListOfRelatedCustomerTransactions.add(latency);
            }

            if (transaction instanceof StockLevelTransaction) {
                executedStockLevelTransactions++;
                totalExecutionTimeOfStockLevelTransaction += latency;
                latencyListOfStockLevelTransactions.add(latency);
            }

            if (transaction instanceof TopBalanceTransaction) {
                executedTopBalanceTransactions++;
                totalExecutionTimeOfTopBalanceTransaction += latency;
                latencyListOfTopBalanceTransactions.add(latency);
            }


        } catch (Throwable t) {
            failedTransactions++;
            // throw new RuntimeException(t);
        }


    }

    public static class Watch {
        private long startMs;
        private long endMs;
        private long execMs;

        private void start() {
            this.startMs = System.nanoTime();
        }

        private void stop() {
            this.endMs = System.nanoTime();
            this.execMs = this.endMs - this.startMs;
        }

    }

    public static void report() {
        double throughput = executedTransactions / totalExecutionTime;
        Collections.sort(latencyList);
        double average = totalExecutionTime / executedTransactions;
        double median = latencyList.get(executedTransactions / 2);
        double percentile95 = latencyList.get((int) (executedTransactions * 0.95));
        double percentile99 = latencyList.get((int) (executedTransactions * 0.99));
        System.out.println(String.format("%d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedTransactions, totalExecutionTime, throughput, average, median, percentile95, percentile99));
    }

    public static void detailedReport() {
        double throughput = executedTransactions / totalExecutionTime;
        Collections.sort(latencyList);
        double average = totalExecutionTime / executedTransactions;
        double median = latencyList.get(executedTransactions / 2);
        double percentile95 = latencyList.get((int) (executedTransactions * 0.95));
        double percentile99 = latencyList.get((int) (executedTransactions * 0.99));
        System.out.println(String.format("%d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedTransactions, totalExecutionTime, throughput, average, median, percentile95, percentile99));

        Collections.sort(latencyListOfDeliveryTransactions);
        System.out.println(String.format("Delivery transaction statistics: %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedDeliveryTransactions, totalExecutionTimeOfDeliveryTransaction, totalExecutionTimeOfDeliveryTransaction / executedDeliveryTransactions, executedDeliveryTransactions / totalExecutionTimeOfDeliveryTransaction, latencyListOfDeliveryTransactions.get((int) (executedDeliveryTransactions * 0.5)), latencyListOfDeliveryTransactions.get((int) (executedDeliveryTransactions * 0.95)), latencyListOfDeliveryTransactions.get((int) (executedDeliveryTransactions * 0.99))));

        Collections.sort(latencyListOfNewOrderTransactions);
        System.out.println(String.format("NewOrder transaction statistics: %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedNewOrderTransactions, totalExecutionTimeOfNewOrderTransaction, totalExecutionTimeOfNewOrderTransaction / executedNewOrderTransactions, executedNewOrderTransactions / totalExecutionTimeOfNewOrderTransaction, latencyListOfNewOrderTransactions.get((int) (executedNewOrderTransactions * 0.5)), latencyListOfNewOrderTransactions.get((int) (executedNewOrderTransactions * 0.95)), latencyListOfNewOrderTransactions.get((int) (executedNewOrderTransactions * 0.99))));

        Collections.sort(latencyListOfOrderStatusTransactions);
        System.out.println(String.format("OrderStatus transaction statistics: %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedOrderStatusTransactions, totalExecutionTimeOfOrderStatusTransaction, totalExecutionTimeOfOrderStatusTransaction / executedOrderStatusTransactions, executedOrderStatusTransactions / totalExecutionTimeOfOrderStatusTransaction, latencyListOfOrderStatusTransactions.get((int) (executedOrderStatusTransactions * 0.5)), latencyListOfOrderStatusTransactions.get((int) (executedOrderStatusTransactions * 0.95)), latencyListOfOrderStatusTransactions.get((int) (executedOrderStatusTransactions * 0.99))));

        Collections.sort(latencyListOfPaymentTransactions);
        System.out.println(String.format("Payment transaction statistics: %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedPaymentTransactions, totalExecutionTimeOfPaymentTransaction, totalExecutionTimeOfPaymentTransaction / executedPaymentTransactions, executedPaymentTransactions / totalExecutionTimeOfPaymentTransaction, latencyListOfPaymentTransactions.get((int) (executedPaymentTransactions * 0.5)), latencyListOfPaymentTransactions.get((int) (executedPaymentTransactions * 0.95)), latencyListOfPaymentTransactions.get((int) (executedPaymentTransactions * 0.99))));

        Collections.sort(latencyListOfPopularItemTransactions);
        System.out.println(String.format("PopularItem transaction statistics: %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedPopularItemTransactions, totalExecutionTimeOfPopularItemTransaction, totalExecutionTimeOfPopularItemTransaction / executedPopularItemTransactions, executedPopularItemTransactions / totalExecutionTimeOfPopularItemTransaction, latencyListOfPopularItemTransactions.get((int) (executedPopularItemTransactions * 0.5)), latencyListOfPopularItemTransactions.get((int) (executedPopularItemTransactions * 0.95)), latencyListOfPopularItemTransactions.get((int) (executedPopularItemTransactions * 0.99))));

        // Collections.sort(latencyListOfRelatedCustomerTransactions);
        // System.out.println(String.format("RelatedCustomer transaction statistics: %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
        //         executedRelatedCustomerTransactions, totalExecutionTimeOfRelatedCustomerTransaction, totalExecutionTimeOfRelatedCustomerTransaction / executedRelatedCustomerTransactions, executedRelatedCustomerTransactions / totalExecutionTimeOfRelatedCustomerTransaction, latencyListOfRelatedCustomerTransactions.get((int) (executedRelatedCustomerTransactions * 0.5)), latencyListOfRelatedCustomerTransactions.get((int) (executedRelatedCustomerTransactions * 0.95)), latencyListOfRelatedCustomerTransactions.get((int) (executedRelatedCustomerTransactions * 0.99))));

        Collections.sort(latencyListOfStockLevelTransactions);
        System.out.println(String.format("StockLevel transaction statistics: %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedStockLevelTransactions, totalExecutionTimeOfStockLevelTransaction, totalExecutionTimeOfStockLevelTransaction / executedStockLevelTransactions, executedStockLevelTransactions / totalExecutionTimeOfStockLevelTransaction, latencyListOfStockLevelTransactions.get((int) (executedStockLevelTransactions * 0.5)), latencyListOfStockLevelTransactions.get((int) (executedStockLevelTransactions * 0.95)), latencyListOfStockLevelTransactions.get((int) (executedStockLevelTransactions * 0.99))));

        Collections.sort(latencyListOfTopBalanceTransactions);
        System.out.println(String.format("TopBalance transaction statistics: %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedTopBalanceTransactions, totalExecutionTimeOfTopBalanceTransaction, totalExecutionTimeOfTopBalanceTransaction / executedTopBalanceTransactions, executedTopBalanceTransactions / totalExecutionTimeOfTopBalanceTransaction, latencyListOfTopBalanceTransactions.get((int) (executedTopBalanceTransactions * 0.5)), latencyListOfTopBalanceTransactions.get((int) (executedTopBalanceTransactions * 0.95)), latencyListOfTopBalanceTransactions.get((int) (executedTopBalanceTransactions * 0.99))));

    }


    @FunctionalInterface
    public interface Function<E extends Throwable> {
        void exec() throws E;
    }
}

