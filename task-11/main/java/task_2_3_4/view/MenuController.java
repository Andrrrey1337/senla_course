package task_2_3_4.view;

import task_2_3_4.service.HotelService;

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
