package gg.mineral.practice.commands.config;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.val;

public class LobbyCommand extends PlayerCommand {

	public LobbyCommand() {
		super("lobby", "practice.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		val loc = pl.getLocation();
		pl.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		ProfileManager.setSpawnLocation(loc);
		ChatMessages.SPAWN_SET.send(pl);
	}
}
