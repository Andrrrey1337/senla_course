package task_1_2.view;

public class MenuItem {
    private String title;
    private IAction action;

    public MenuItem(String title, IAction action) {
        this.title = title;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public IAction getAction() {
        return action;
    }

    public void doAction() {
        action.execute();
    }
}

