package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class TogglePartyRequestsCommand extends PlayerCommand {

    public TogglePartyRequestsCommand() {
        super("togglepartyrequests");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        Profile profile = ProfileManager.getOrCreateProfile(pl);
        profile.setPartyRequests(!profile.isPartyRequests());
        ChatMessages.PARTY_REQUESTS_TOGGLED.clone()
                .replace("%toggled%", profile.isPartyRequests() ? "enabled" : "disabled")
                .send(pl);
    }
}
