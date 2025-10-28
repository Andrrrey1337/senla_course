package task_2;

import java.util.ArrayList;
import java.util.List;

public class Bouquet {
    private List<Flower> flowers;

    public  Bouquet() {
        this.flowers = new ArrayList<>();
    }
    public void addFlowers(Flower flower, int count) {
        for (int i = 0; i < count; i++) {
            flowers.add(flower);
        }
    }
    public double costBouquet() {
        double sum = 0;
        for (Flower flower : flowers) {
            sum += flower.cost;
        }
        return sum;
    }

}
