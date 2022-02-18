package ms.uk.eclipse.commands.config;

import org.bukkit.Location;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.rank.RankPower;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ChatMessages;

public class LobbyCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public LobbyCommand() {
		super("lobby", RankPower.MANAGER);
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Location loc = pl.getLocation();
		pl.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		playerManager.setSpawnLocation(loc);
		ChatMessages.SPAWN_SET.send(pl);
	}
}
