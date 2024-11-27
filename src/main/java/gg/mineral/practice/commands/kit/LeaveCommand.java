package gg.mineral.practice.commands.kit;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.val;

public class LeaveCommand extends PlayerCommand {

    public LeaveCommand() {
        super("leave");
    }

    @Override
    public void execute(org.bukkit.entity.Player player, String[] args) {
        val profile = ProfileManager.getOrCreateProfile(player);

        if (profile.getPlayerStatus() != PlayerStatus.KIT_CREATOR
                || profile.getPlayerStatus() != PlayerStatus.KIT_EDITOR) {
            profile.message(ErrorMessages.NOT_IN_KIT_EDITOR_OR_CREATOR);
            return;
        }

        if (profile.getPlayerStatus() == PlayerStatus.KIT_CREATOR) {
            profile.leaveKitCreator();
            return;
        }

        profile.leaveKitEditor();
    }

}
