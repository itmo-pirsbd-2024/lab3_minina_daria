package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class LockFreeQueueBenchmark {

    private LockFreeQueue<Integer> queue;

    @Setup
    public void setup() {
        queue = new LockFreeQueue<>(1000);
    }

    @Benchmark
    public void testOffer() {
        queue.offer(42);
    }

    @Benchmark
    public void testPoll() {
        queue.poll();
    }

    @Benchmark
    public void testOfferAndPoll() {
        queue.offer(42);
        queue.poll();
    }

    @Benchmark
    @Threads(4)
    public void testConcurrentOfferAndPoll() {
        queue.offer(42);
        queue.poll();
    }
}


