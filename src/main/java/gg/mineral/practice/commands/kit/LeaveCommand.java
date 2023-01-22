package gg.mineral.practice.commands.kit;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class LeaveCommand extends PlayerCommand {

    public LeaveCommand() {
        super("leave");
    }

    @Override
    public void execute(org.bukkit.entity.Player player, String[] args) {
        Profile profile = ProfileManager.getOrCreateProfile(player);

        if (!profile.isInKitEditor() || !profile.isInKitEditor()) {
            profile.message(ErrorMessages.NOT_IN_KIT_EDITOR_OR_CREATOR);
            return;
        }

        if (profile.isInKitCreator()) {
            profile.leaveKitCreator();
            return;
        }

        profile.leaveKitEditor();
    }
}
