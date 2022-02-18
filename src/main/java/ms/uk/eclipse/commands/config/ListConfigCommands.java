package ms.uk.eclipse.commands.config;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.rank.RankPower;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ChatMessages;

public class ListConfigCommands extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public ListConfigCommands() {
		super("practice", RankPower.MANAGER);
		setAliases("practiceconfig", "practicecommandslist");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		ChatMessages.CONFIG_COMMANDS.send(pl);
		ChatMessages.QUEUETYPE.send(pl);
		ChatMessages.GAMETYPE.send(pl);
		ChatMessages.CATAGORY.send(pl);
		ChatMessages.AIM_TRAINER.send(pl);
		ChatMessages.PVP_BOTS.send(pl);
		ChatMessages.EVENTS.send(pl);
		ChatMessages.ARENA.send(pl);
		ChatMessages.KIT_EDITOR.send(pl);
		ChatMessages.PARTIES.send(pl);
		ChatMessages.LOBBY.send(pl);
		ChatMessages.SETTINGS_CONFIG.send(pl);
	}
}
