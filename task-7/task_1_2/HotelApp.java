package task_1;

import task_1.exceptions.HotelException;
import task_1.model.Admin;
import task_1.util.StateManager;
import task_1.view.MenuController;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("Система управления отелем");

        try {
            Admin loadedAdmin = StateManager.loadState();
            if (loadedAdmin != null) {
                Admin.setInstanceForLoading(loadedAdmin);
            }

            new MenuController().run();

        } catch (HotelException e) {
            System.err.println("Критическая ошибка при загрузке состояния: " + e.getMessage());
            e.printStackTrace();
        }
    }
}