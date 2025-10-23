public class Tank implements IProduct{
    private IProductPart firstPart;  // корпус
    private IProductPart secondPart; // двигатель
    private IProductPart thirdPart; // башня
    @Override
    public void installFirstPart(IProductPart part) {
        this.firstPart = part;
        System.out.println("Установка первой части: " + part);
    }

    @Override
    public void installSecondPart(IProductPart part) {
        this.secondPart = part;
        System.out.println("Установка второй части: " + part);
    }

    @Override
    public void installThirdPart(IProductPart part) {
        this.thirdPart = part;
        System.out.println("Установка третьей части: " + part);
    }

}
