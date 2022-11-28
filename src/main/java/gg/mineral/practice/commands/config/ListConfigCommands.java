package gg.mineral.practice.commands.config;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.ChatMessages;

public class ListConfigCommands extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public ListConfigCommands() {
		super("practice", "practice.config");
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
