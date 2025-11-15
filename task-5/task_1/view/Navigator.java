package task_1.view;

public class Navigator {
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
            item.doAction(); // Выполняем действие, но не переходим к новому меню
        } else {
            System.out.println("Неверный выбор.");
        }
    }
}
