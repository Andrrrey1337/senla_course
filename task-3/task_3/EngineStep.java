package task_3;

public class EngineStep implements ILineStep{
    public EngineStep() {
        System.out.println("Производство двигателя");
    }
    @Override
    public IProductPart buildProductPart() {
        return new EnginePart();
    }

    @Override
    public String getName() {
        return "Двигатель";
    }
}
