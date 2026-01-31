package task_1;

import task_1.di.DependencyInjector;
import task_1.service.HotelService;
import task_1.util.IdSyncManager;
import task_1.view.MenuController;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("Система управления отелем");

        DependencyInjector di = new DependencyInjector();

        try {
            HotelService service = (HotelService) di.getDependency(HotelService.class);
            IdSyncManager idSync = (IdSyncManager) di.getDependency(IdSyncManager.class);
            idSync.sync();
            new MenuController(service).run();
        } catch (RuntimeException e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
