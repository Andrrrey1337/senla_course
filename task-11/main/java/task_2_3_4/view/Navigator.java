package task_2_3_4.view;

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
            item.doAction();
        } else {
            System.out.println("Неверный выбор.");
        }
    }
}
