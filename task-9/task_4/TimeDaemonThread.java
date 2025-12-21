package task_4;

import java.time.LocalTime;

public class TimeDaemonThread extends Thread {

    private final int seconds;

    public TimeDaemonThread(int seconds) {
        this.seconds = seconds;
        setDaemon(true); // делаем поток служебным
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Текущее время: " + LocalTime.now());
            try {
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}