package task_1_2_3.view;

import task_1_2_3.service.HotelService;

import java.util.Scanner;

public class MenuController {
    private Builder builder;
    private Navigator navigator;
    private Scanner scanner = new Scanner(System.in);

    public MenuController(HotelService hotelService) {
        this.builder = new Builder(hotelService);
        this.navigator = new Navigator(builder.buildRootMenu());
    }

    public void run() {
        while (true) {
            navigator.printMenu();
            System.out.print("Выберите действие: ");
            int choice = getInt();
            navigator.navigate(choice);
        }
    }
    private int getInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Неверный ввод. Повторите: ");
            }
        }
    }
}
