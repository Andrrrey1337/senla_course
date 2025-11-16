package task_1.model;

import task_1.IdGenerator.IdGenerator;

public class Guest {
    private final long id;
    private String name;

    public Guest(String name) {
        this.id = IdGenerator.next();
        this.name = name;
    }

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
