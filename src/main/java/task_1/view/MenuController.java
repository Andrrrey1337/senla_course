package task_1.view;

import task_1.service.HotelService;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
    private Builder builder;
    private Menu currentMenu;
    private final Scanner scanner = new Scanner(System.in);

    public MenuController(HotelService hotelService) {
        this.builder = new Builder(hotelService);
        this.currentMenu = builder.buildRootMenu();
    }

    public void run() {
        while (true) {
            printMenu();
            System.out.print("Выберите действие: ");
            int choice = getInt();
            navigate(choice);
        }
    }

    public void printMenu() {
        currentMenu.print();
    }

    public void navigate(int index) {
        MenuItem item = currentMenu.getItem(index - 1);
        if (item != null) {
            logger.info("Начало обработки команды от пользователя: '{}'", item.getTitle());
            try {
                item.doAction();
                logger.info("Команда '{}' успешно обработана.", item.getTitle());
            }
            catch (Exception e) {
                logger.error("Ошибка при обработке команды '{}': ", item.getTitle());
            }
        } else {
            System.out.println("Неверный выбор.");
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
