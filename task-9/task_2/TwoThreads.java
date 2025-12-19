package task_2;

public class TwoThreads {

    private static final Object LOCK = new Object();
    private static boolean firstTurn = true;

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (LOCK) {
                    while (!firstTurn) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException ignored) {}
                    }
                    System.out.println(Thread.currentThread().getName());
                    firstTurn = false;
                    LOCK.notifyAll();
                }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (LOCK) {
                    while (firstTurn) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException ignored) {}
                    }
                    System.out.println(Thread.currentThread().getName());
                    firstTurn = true;
                    LOCK.notifyAll();
                }
            }
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}

