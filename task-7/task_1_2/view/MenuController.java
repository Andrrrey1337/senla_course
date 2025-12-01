package view;

import java.util.Scanner;

public class MenuController {
    private Builder builder;
    private Navigator navigator;
    private Scanner scanner = new Scanner(System.in);

    public MenuController() {
        this.builder = new Builder();
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
