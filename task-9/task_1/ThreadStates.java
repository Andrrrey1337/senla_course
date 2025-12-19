package task_1;

public class ThreadStates {

    private static final Object LOCK = new Object();

    public static void main(String[] args) throws Exception {

        Thread thread = new Thread(() -> {
            try {
                // RUNNABLE
                System.out.println("Внутри потока: " + Thread.currentThread().getState());

                // BLOCKED
                synchronized (LOCK) {
                    // WAITING
                    LOCK.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // NEW
        System.out.println("NEW: " + thread.getState());

        // устанвливаем здесь BLOCKED
        synchronized (LOCK) {
            thread.start();
            Thread.sleep(100);
            System.out.println("BLOCKED: " + thread.getState());
        }

        Thread.sleep(100);

        // WAITING
        System.out.println("WAITING: " + thread.getState());

        // TIMED_WAITING
        Thread timedThread = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        });

        timedThread.start();
        Thread.sleep(100);
        System.out.println("TIMED_WAITING: " + timedThread.getState());

        // будим первый поток
        synchronized (LOCK) {
            LOCK.notify();
        }

        thread.join();
        timedThread.join();

        // TERMINATED
        System.out.println("TERMINATED (thread): " + thread.getState());
        System.out.println("TERMINATED (timedThread): " + timedThread.getState());
    }
}