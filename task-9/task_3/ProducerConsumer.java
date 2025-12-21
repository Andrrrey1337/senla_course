package task_3;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ProducerConsumer {

    private static final int BUFFER_SIZE = 5;
    private static final Queue<Integer> buffer = new LinkedList<>();
    private static final Object LOCK = new Object();

    public static void main(String[] args) {

        Thread producer = new Thread(() -> {
            Random random = new Random();

            while (true) {
                synchronized (LOCK) {
                    while (buffer.size() == BUFFER_SIZE) {
                        try {
                            System.out.println("Буфер заполнен. Производитель ждёт...");
                            LOCK.wait();
                        } catch (InterruptedException ignored) {}
                    }

                    int value = random.nextInt(100);
                    buffer.add(value);
                    System.out.println("Производитель произвёл: " + value);

                    LOCK.notifyAll();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                synchronized (LOCK) {
                    while (buffer.isEmpty()) {
                        try {
                            System.out.println("Буфер пуст. Потребитель ждёт...");
                            LOCK.wait();
                        } catch (InterruptedException ignored) {}
                    }

                    int value = buffer.poll();
                    System.out.println("Потребитель потребил: " + value);

                    LOCK.notifyAll();
                }

                try {
                    Thread.sleep(800);
                } catch (InterruptedException ignored) {}
            }
        });

        producer.start();
        consumer.start();
    }
}
