package edu.teamv.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerformanceMeasurementUtil {

    private static int executedTransactions;
    private static int failedTransactions;
    private static double totalExecutionTime;
    private static final List<Double> latencyList = new ArrayList<>();


    public static <E extends Throwable> void run(Function<E> function) {
        Watch watch = new Watch();
        watch.start();
        try {
            function.exec();
            executedTransactions++;
            watch.stop();
            double latency = (double) watch.execMs / 1_000_000_000;
            latencyList.add(latency);
            totalExecutionTime += latency ;
        } catch (Throwable t) {
            failedTransactions++;
            throw new RuntimeException(t);
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
        System.out.println(String.format("%d, %.2f, %.2f, %.2f, %.2f, %.2f",
                executedTransactions, throughput, average, median, percentile95, percentile99));
    }

    @FunctionalInterface
    public interface Function<E extends Throwable> {
        void exec() throws E;
    }
}

