package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.val;

public class TogglePartyRequestsCommand extends PlayerCommand {

    public TogglePartyRequestsCommand() {
        super("togglepartyrequests");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        val profile = ProfileManager.getOrCreateProfile(pl);
        profile.getRequestHandler().setPartyRequests(!profile.getRequestHandler().isPartyRequests());
        ChatMessages.PARTY_REQUESTS_TOGGLED.clone()
                .replace("%toggled%", profile.getRequestHandler().isPartyRequests() ? "enabled" : "disabled")
                .send(pl);
    }
}
