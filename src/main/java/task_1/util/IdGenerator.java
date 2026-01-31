package task_1.util;

import org.springframework.stereotype.Component;

@Component
public class IdGenerator {
    private long nextId = 1;

    public long next() {
        return nextId++;
    }

    public void setNext(long value) {
        nextId = value;
    }
}
