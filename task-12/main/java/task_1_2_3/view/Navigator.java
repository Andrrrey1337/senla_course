package task_1_2_3.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Navigator {
    private static final Logger logger = LoggerFactory.getLogger(Navigator.class);
    private Menu currentMenu;

    public Navigator(Menu startMenu) {
        currentMenu = startMenu;
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
