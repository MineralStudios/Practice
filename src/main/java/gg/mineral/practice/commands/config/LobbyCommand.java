package gg.mineral.practice.commands.config;

import org.bukkit.Location;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class LobbyCommand extends PlayerCommand {

	public LobbyCommand() {
		super("lobby", "practice.permission.admin");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Location loc = pl.getLocation();
		pl.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		PlayerManager.setSpawnLocation(loc);
		ChatMessages.SPAWN_SET.send(pl);
	}
}
