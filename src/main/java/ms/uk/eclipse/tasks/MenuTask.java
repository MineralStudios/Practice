package ms.uk.eclipse.tasks;

import ms.uk.eclipse.inventory.PracticeMenu;

public class MenuTask {
    PracticeMenu m;

    public MenuTask(PracticeMenu m) {
        this.m = m;
    }

    public PracticeMenu getMenu() {
        return m;
    }
}
