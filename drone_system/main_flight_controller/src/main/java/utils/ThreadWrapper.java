package utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Wraps a class in a thread so method calls can be executed in the separate thread.
 */
public class ThreadWrapper<T> implements Runnable {

    @Getter
    private T obj;
    private final ArrayList<Consumer<T>> consumers;
    private boolean isDead;

    ThreadWrapper(T obj) {
        this.obj = obj;
        this.consumers = new ArrayList<>();
        this.isDead = false;
    }

    void runMethod(Consumer<T> consumer) {
        synchronized (this.consumers) {
            this.consumers.add(consumer);
        }
    }

    @Override
    public void run() {
        while (!this.isDead) {
            if (this.consumers.size() > 0) {
                Consumer<T> consumer;
                synchronized (this.consumers) {
                    consumer = this.consumers.get(0);
                    consumers.remove(consumer);
                }
                consumer.accept(obj);
            }
            Thread.yield();
        }
    }

    public T kill() {
        this.isDead = true;
        return obj;
    }
}
