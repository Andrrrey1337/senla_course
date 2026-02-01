package task_1;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import task_1.config.AppConfig;
import task_1.util.IdSyncManager;
import task_1.view.MenuController;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("Система управления отелем");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)){
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
