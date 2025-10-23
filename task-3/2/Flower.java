abstract class Flower {
    protected String name;
    protected String color;
    protected double cost;

    public Flower(String name, String color, double cost) {
        this.name = name;
        this.color = color;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getColor() {
        return color;
    }
    @Override
    public String toString() {
        return name + color;
    }
}
