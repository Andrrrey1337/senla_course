package task_1;

import task_1.view.MenuController;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("Система управления отелем");
        new MenuController().run();
    }
}
