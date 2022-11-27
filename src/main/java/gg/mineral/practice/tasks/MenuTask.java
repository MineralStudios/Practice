package gg.mineral.practice.tasks;

import gg.mineral.practice.inventory.PracticeMenu;

public class MenuTask {
    PracticeMenu m;

    public MenuTask(PracticeMenu m) {
        this.m = m;
    }

    public PracticeMenu getMenu() {
        return m;
    }
}
