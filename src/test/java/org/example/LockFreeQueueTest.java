package org.example;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LockFreeQueueTest {

    @Test
    public void testQueueCorrectness() throws InterruptedException {
        LockFreeQueue<Integer> queue = new LockFreeQueue<>(100);
        int threadsCount = 10;
        int itemsPerThread = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch latch = new CountDownLatch(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    queue.offer(j);
                }
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(threadsCount * itemsPerThread, queue.size());

        for (int i = 0; i < threadsCount * itemsPerThread; i++) {
            queue.poll();
        }

        assertEquals(0, queue.size());
        executor.shutdown();
    }

    @Test
    public void testQueuePerformance() throws InterruptedException {
        LockFreeQueue<Integer> queue = new LockFreeQueue<>(100000);
        int threadsCount = 10;
        int itemsPerThread = 10000;
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch latch = new CountDownLatch(threadsCount);
        AtomicInteger totalItems = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadsCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    queue.offer(j);
                }
                latch.countDown();
            });
        }

        latch.await();

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to offer: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();

        for (int i = 0; i < threadsCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    if (queue.poll() != null) {
                        totalItems.incrementAndGet();
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Ждем завершения всех потоков
        }

        endTime = System.currentTimeMillis();
        System.out.println("Time taken to poll: " + (endTime - startTime) + " ms");

        assertEquals(threadsCount * itemsPerThread, totalItems.get());
    }
}
