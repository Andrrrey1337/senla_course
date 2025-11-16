package task_1.IdGenerator;

public final class IdGenerator {
    private static long nextId = 1;

    private IdGenerator() {}

    public static long next() {
        return nextId++;
    }

    public static void setNext(long value) {
        nextId = value;
    }
}
