package task_1.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
    private final Menu currentMenu;
    private final ConsoleUI ui;

    public MenuController(Builder builder, ConsoleUI ui) {
        this.ui = ui;
        this.currentMenu = builder.buildRootMenu();
    }

    public void run() {
        while (true) {
            printMenu();
            int choice = ui.readInt("Выберите действие");
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
}
