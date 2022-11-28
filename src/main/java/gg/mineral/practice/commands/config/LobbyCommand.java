package gg.mineral.practice.commands.config;

import org.bukkit.Location;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.ChatMessages;

public class LobbyCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public LobbyCommand() {
		super("lobby", "practice.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Location loc = pl.getLocation();
		pl.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		playerManager.setSpawnLocation(loc);
		ChatMessages.SPAWN_SET.send(pl);
	}
}
