package kam;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.*;

class ProducerConsumerUsingBlockingQueue {
    static BlockingDeque<Integer> queue = new LinkedBlockingDeque<>();
    public void start() {
        Runnable producter = () -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Random random = new Random();
                int num = random.nextInt(100);
                System.out.println("Producer adding " + num);
                queue.add(num);
            }
        };

        Runnable consumer = () -> {
            while (true) {
                int num = 0;
                try {
                    num = queue.take();
                    System.out.println(String.format("Consumer using %d", num));
                    System.out.println(num);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread tp = new Thread(producter, "Producer thread");
        Thread tc = new Thread(consumer, "Consumer thread");
        tp.start();
        tc.start();
    }
}

class MyBlockingQueue<T> {
    private Queue<T> queue;
    private int max = 5;
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public MyBlockingQueue() {
        this.queue = new LinkedList<>();

    }

    public void put(T item) {
        lock.lock();
        try {
            if (queue.size() == max) {
                notFull.await();
            }
            queue.add(item);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public T take() {
        T item = null;
        lock.lock();
        try {
            while (queue.size() == 0) {
                notEmpty.await();
            }
            item = queue.remove();
            notFull.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return item;
    }
}

class BlockingQueueWaitNotify<T> {
    private Queue<T> queue;
    private int max = 5;
    private Lock lock = new ReentrantLock();
    private Object notFull = new Object();
    private Object   notEmpty = new Object();

    public BlockingQueueWaitNotify() {
        this.queue = new LinkedList<>();
    }

    public void put(T item) {
        lock.lock();
        try {
            if (queue.size() == max) {
                notFull.wait();
            }
            queue.add(item);
            notEmpty.notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public T take() {
        T item = null;
        lock.lock();
        try {
            while (queue.size() == 0) {
                notEmpty.wait();
            }
            item = queue.remove();
            notFull.notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return item;
    }
}

class ProducerConsumerUsingBlockingQueueWaitNotify {
    private BlockingQueueWaitNotify<Integer> queue = new kam.BlockingQueueWaitNotify<Integer>();

    public void start() {
        Runnable producter = () -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Random random = new Random();
                int num = random.nextInt(100);
                System.out.println("Producer adding " + num);
                queue.put(num);
            }
        };

        Runnable consumer = () -> {
            while (true) {
                Optional<Integer> num = Optional.of(queue.take());
                if (num.isPresent()) {
                    System.out.println(String.format("Consumer using %d", num.get()));
                    System.out.println(num);
                }
            }
        };

        Thread tp = new Thread(producter, "Producer thread");
        Thread tc = new Thread(consumer, "Consumer thread");
        tp.start();
        tc.start();
    }
}

class ProducerConsumerWithoutBlockingQueue {
    private MyBlockingQueue<Integer> queue = new MyBlockingQueue<>();

    public void start() {
        Runnable producter = () -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Random random = new Random();
                int num = random.nextInt(100);
                System.out.println("Producer adding " + num);
                queue.put(num);
            }
        };

        Runnable consumer = () -> {
            while (true) {
                Optional<Integer> num = Optional.of(queue.take());
                if (num.isPresent()) {
                    System.out.println(String.format("Consumer using %d", num.get()));
                    System.out.println(num);
                }
            }
        };

        Thread tp = new Thread(producter, "Producer thread");
        Thread tc = new Thread(consumer, "Consumer thread");
        tp.start();
        tc.start();
    }
}

public class Main {
    public static void main(String[] args) {
        /*ProducerConsumerWithoutBlockingQueue pcBlockingQueue = new ProducerConsumerWithoutBlockingQueue();
        pcBlockingQueue.start();*/
        ProducerConsumerUsingBlockingQueueWaitNotify producerConsumerUsingBlockingQueueWaitNotify =
                new ProducerConsumerUsingBlockingQueueWaitNotify();
        producerConsumerUsingBlockingQueueWaitNotify.start();
    }
}
