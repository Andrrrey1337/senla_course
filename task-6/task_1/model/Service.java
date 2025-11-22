package task_1.model;

import task_1.IdGenerator.IdGenerator;

public class Service {
    private final long id;
    private String name;
    private double price;

    public Service(String name, double price) {
        this.id = IdGenerator.next();
        this.name = name;
        this.price = price;
    }

    public Service(long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Имя услуги: " + name + ", " + "стоимость: " + price;
    }

}
