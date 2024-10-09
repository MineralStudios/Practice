package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.messages.CC;

@ClickCancelled(true)
public class BotTeamSettingsMenu extends PracticeMenu {

    @Override
    public void update() {

    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Team Settings";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
