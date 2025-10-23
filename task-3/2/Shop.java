public class Shop {
    public static void main(String[] args) {
        Rose redRose = new Rose("красный",250);
        Rose blackRose = new Rose("черный", 400);
        Lily whiteLily = new Lily("белый", 200);
        Lily pinkLily = new Lily("розовый", 100);
        Peonies peonies = new Peonies("белый", 150);

        Bouquet bouquet = new Bouquet();
        bouquet.addFlowers(redRose, 10);
        bouquet.addFlowers(blackRose,5);
        bouquet.addFlowers(whiteLily, 7);
        bouquet.addFlowers(pinkLily,3);
        bouquet.addFlowers(peonies,2);

        System.out.println("Букет собран, его стоимость: " + bouquet.costBouquet());
    }
}
