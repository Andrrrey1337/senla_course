import java.util.ArrayList;
import java.util.List;

public class AssemblyLine implements IAssemblyLine{
    private ILineStep step1;
    private ILineStep step2;
    private ILineStep step3;

    public AssemblyLine(ILineStep step1,ILineStep step2,ILineStep step3) {
        this.step1 = step1;
        this.step2 = step2;
        this.step3 = step3;
        System.out.println("Сборочная линия:" + step1.getName() + ", " + step2.getName() + ", " + step3.getName());
    }

    @Override
    public IProduct assembleProduct(IProduct product) {
        IProductPart firstPart = step1.buildProductPart();
        IProductPart secondPart = step2.buildProductPart();
        IProductPart thirdPart = step3.buildProductPart();
        product.installFirstPart(firstPart);
        product.installSecondPart(secondPart);
        product.installThirdPart(thirdPart);
        System.out.printf("Танк собран\nПервая часть: %s\nВторая часть: %s\nТретья часть: %s", firstPart, secondPart, thirdPart);
        return product;
    }
}
