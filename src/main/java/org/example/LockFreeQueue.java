package org.example;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LockFreeQueue<T> {
    private final AtomicReferenceArray<T> array;
    private final AtomicInteger head;
    private final AtomicInteger tail;
    private final int capacity;

    public LockFreeQueue(int capacity) {
        this.capacity = capacity;
        this.array = new AtomicReferenceArray<>(capacity);
        this.head = new AtomicInteger(0);
        this.tail = new AtomicInteger(0);
    }

    public boolean offer(T item) {
        if (item == null) throw new NullPointerException();
        int currentTail;
        int nextTail;
        do {
            currentTail = tail.get();
            if (currentTail - head.get() == capacity) {
                return false; // Очередь заполнена
            }
            nextTail = currentTail + 1;
        } while (!tail.compareAndSet(currentTail, nextTail));
        array.lazySet(currentTail % capacity, item);
        return true;
    }

    public T poll() {
        int currentHead;
        int nextHead;
        T item;
        do {
            currentHead = head.get();
            if (currentHead == tail.get()) {
                return null; // Очередь пуста
            }
            nextHead = currentHead + 1;
            item = array.get(currentHead % capacity);
        } while (!head.compareAndSet(currentHead, nextHead));
        return item;
    }

    public boolean isEmpty() {
        return head.get() == tail.get();
    }

    public int size() {
        return tail.get() - head.get();
    }
}
