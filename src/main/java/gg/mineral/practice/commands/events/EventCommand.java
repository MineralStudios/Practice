package gg.mineral.practice.commands.events;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.SelectEventMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class EventCommand extends PlayerCommand {

    public EventCommand() {
        super("event");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        Profile player = ProfileManager.getOrCreateProfile(pl);

        if (player.getPlayerStatus() != PlayerStatus.IDLE) {
            player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        player.openMenu(new SelectEventMenu());
    }
}
