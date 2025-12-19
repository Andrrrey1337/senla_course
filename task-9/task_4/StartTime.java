package task_4;

public class StartTime {
    public static void main(String[] args) throws InterruptedException{
        TimeDaemonThread timeThread = new TimeDaemonThread(2);
        timeThread.start();
        Thread.sleep(10000);
    }
}
