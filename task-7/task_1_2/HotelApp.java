import exceptions.HotelException;
import service.HotelService;
import util.StateManager;
import view.MenuController;

public class HotelApp {
    private static final StateManager stateManager = new StateManager();
    public static void main(String[] args) {
        System.out.println("Система управления отелем");

        try {
            HotelService loadedHotelService = stateManager.loadState();
            if (loadedHotelService != null) {
                HotelService.setInstanceForLoading(loadedHotelService);
            }

            new MenuController().run();

        } catch (HotelException e) {
            System.err.println("Критическая ошибка при загрузке состояния: " + e.getMessage());
            e.printStackTrace();
        }
    }
}