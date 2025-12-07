import di.DependencyInjector;
import exceptions.HotelException;
import service.HotelService;
import util.StateManager;
import view.MenuController;

public class HotelApp {
    private static final StateManager stateManager = new StateManager();

    public static void main(String[] args) {
        System.out.println("Система управления отелем");

        DependencyInjector di = new DependencyInjector();

        try {
            StateManager stateManager = (StateManager) di.getDependency(StateManager.class);
            HotelService loaded = stateManager.loadState();
            HotelService serviceToUse;
            if (loaded != null) {
                System.out.println("Состояние загружено из файла");
                serviceToUse = loaded;
            } else {
                serviceToUse = (HotelService) di.getDependency(HotelService.class);
                System.out.println("Создано новое состояние.");
            }

            new MenuController(serviceToUse).run();
        } catch (HotelException e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}