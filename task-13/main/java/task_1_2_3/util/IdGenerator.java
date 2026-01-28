package task_1_2_3.util;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Singleton;

@Component
@Singleton
public class IdGenerator {
    private long nextId = 1;

    public long next() {
        return nextId++;
    }

    public void setNext(long value) {
        nextId = value;
    }
}
