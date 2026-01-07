package task_2_3_4.util;

import task_2_3_4.annotations.Component;
import task_2_3_4.annotations.Singleton;

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
