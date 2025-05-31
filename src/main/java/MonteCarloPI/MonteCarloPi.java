package MonteCarloPI;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.io.FileWriter;
import java.io.IOException;

public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single threads): " + (endTime - startTime) / 1_000_000 + " ms");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n",NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (Multi-threaded): " + piWithThreads);
        System.out.println("Time taken (Multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms");

        System.out.println("Do you want the benchmark in CSV file? [y/n]");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.next();
        if (answer.equalsIgnoreCase("y")) {
            runBenchmarksAndExportCSV();
        }
        System.out.println("End of calculation");

    }

    // Monte Carlo Pi Approximation without threads
    public static double estimatePiWithoutThreads(long numPoints) {
        long pointsInsideCircle = 0;
        for (long i = 0; i < numPoints; i++) {
            double x = Math.random();
            double y = Math.random();
            if (x * x + y * y <= 1.0) {
                pointsInsideCircle++;
            }
        }
        return 4.0 * pointsInsideCircle / numPoints;

    }


    // Monte Carlo Pi Approximation with threads
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long pointsPerThread = numPoints / numThreads;
        long remaining = numPoints % numThreads;

        long[] results = new long[numThreads];

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            final long points = (i == 0) ? pointsPerThread + remaining : pointsPerThread;

            Runnable task = () -> {
                long inside = 0;
                for (long j = 0; j < points; j++) {
                    double x = Math.random();
                    double y = Math.random();
                    if (x * x + y * y <= 1.0) {
                        inside++;
                    }
                }
                results[threadIndex] = inside;
            };

            executor.execute(task);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long totalInsideCircle = 0;
        for (long r : results) {
            totalInsideCircle += r;
        }

        return 4.0 * totalInsideCircle / numPoints;
    }



    public static void runBenchmarksAndExportCSV() throws InterruptedException, ExecutionException, IOException {
        long[] pointCounts = {
                100_000L, 500_000L, 1_000_000L, 5_000_000L, 10_000_000L,
                20_000_000L, 30_000_000L, 40_000_000L, 50_000_000L
        };

        int numThreads = Runtime.getRuntime().availableProcessors();
        FileWriter writer = new FileWriter("benchmark_results.csv");

        writer.write("NumPoints,SingleThreadedTime(ms),SingleThreadedPi,MultiThreadedTime(ms),MultiThreadedPi\n");

        for (long numPoints : pointCounts) {
            long start = System.nanoTime();
            double pi1 = estimatePiWithoutThreads(numPoints);
            long singleTime = (System.nanoTime() - start) / 1_000_000;

            start = System.nanoTime();
            double pi2 = estimatePiWithThreads(numPoints, numThreads);
            long multiTime = (System.nanoTime() - start) / 1_000_000;

            writer.write(String.format("%d,%d,%.6f,%d,%.6f\n",
                    numPoints, singleTime, pi1, multiTime, pi2));

            System.out.printf("Completed: %,d points\n", numPoints);
        }

        writer.close();
        System.out.println("Benchmark completed. Results saved to benchmark_results.csv");
    }


}