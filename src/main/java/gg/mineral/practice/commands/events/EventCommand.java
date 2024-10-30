package gg.mineral.practice.commands.events;

import org.bukkit.entity.Player;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;

import gg.mineral.practice.inventory.menus.SelectEventMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.val;

public class EventCommand extends PlayerCommand {

    public EventCommand() {
        super("event", "practice.event");
    }

    @Override
    public void execute(Player pl, String[] args) {
        val profile = ProfileManager.getOrCreateProfile(pl);

        if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        profile.openMenu(new SelectEventMenu());
    }
}
