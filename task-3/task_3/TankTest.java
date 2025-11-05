package task_3;

public class TankTest {
    public static void main(String[] args){
        ILineStep frameStep = new FrameStep();
        ILineStep engineStep = new EngineStep();
        ILineStep turretStep = new TurretStep();

        IAssemblyLine assemblyLine = new AssemblyLine(frameStep,engineStep,turretStep);

        IProduct prototypeTank = new Tank();
        IProduct tank = assemblyLine.assembleProduct(prototypeTank);


    }
}
