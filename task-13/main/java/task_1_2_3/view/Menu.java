package task_1_2_3.view;

public class Menu {
    private String name;
    private MenuItem[] menuItems;

    public Menu(String name, MenuItem... items) {
        this.name = name;
        this.menuItems = items;
    }

    public void print() {
        System.out.println("\n--- " + name + " ---");
        for (int i = 0; i<menuItems.length; i++) {
            System.out.println((i+1) + ". " + menuItems[i].getTitle());
        }
    }

    public MenuItem getItem(int index) {
        if (-1 < index && index < menuItems.length) {
            return menuItems[index];
        }
        return null;
    }
}
