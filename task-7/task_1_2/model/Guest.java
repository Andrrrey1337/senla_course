package task_1.model;

import java.io.Serializable;

public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    private String name;

    public Guest(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
