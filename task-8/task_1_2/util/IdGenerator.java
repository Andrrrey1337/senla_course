package util;

import annotations.Component;
import annotations.Singleton;

import java.io.Serializable;

@Component
@Singleton
public class IdGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private long nextId = 1;

    public long next() {
        return nextId++;
    }

    public void setNext(long value) {
        nextId = value;
    }
}
