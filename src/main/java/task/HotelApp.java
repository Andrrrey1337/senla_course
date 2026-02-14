package task;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import task.config.AppConfig;
import task.util.IdSyncManager;
import task.view.MenuController;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("Система управления отелем");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            IdSyncManager idSync = context.getBean(IdSyncManager.class);
            idSync.sync();

            MenuController menuController = context.getBean(MenuController.class);
            menuController.run();
        } catch (RuntimeException e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
