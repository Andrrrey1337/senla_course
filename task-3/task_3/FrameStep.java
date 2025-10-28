package task_3;

public class FrameStep implements ILineStep {
    public FrameStep() {
        System.out.println("Производство корпуса");
    }
    @Override
    public IProductPart buildProductPart() {

        return new FramePart();
    }

    @Override
    public String getName() {
        return "Корпус";
    }

}
