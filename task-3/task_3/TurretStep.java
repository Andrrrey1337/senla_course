package task_3;

public class TurretStep implements ILineStep{
    public TurretStep() {
        System.out.println("Производство башни");
    }
    @Override
    public IProductPart buildProductPart() {
        return new TurretPart();
    }

    @Override
    public String getName() {
        return "Башня";
    }
}
