package task_1_2.util;

import java.io.Serializable;

public final class IdGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private long nextId = 1;

    public  long next() {
        return nextId++;
    }

    public  void setNext(long value) {
        nextId = value;
    }
}
