package ms.uk.eclipse.commands.config;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;

public class ListConfigCommands extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public ListConfigCommands() {
		super("practice", "practice.permission.config");
		setAliases("practiceconfig", "practicecommandslist");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);
		player.message(new StrikingMessage("Config", CC.PRIMARY, true));
		player.message(new ChatMessage("Queuetypes: /queuetype", CC.SECONDARY, false));
		player.message(new ChatMessage("Gametypes: /gametype", CC.SECONDARY, false));
		player.message(new ChatMessage("Catagories: /catagory", CC.SECONDARY, false));
		player.message(new ChatMessage("Aim Trainer: /aimtrainer", CC.SECONDARY, false));
		player.message(new ChatMessage("PvP Bots: /pvpbots", CC.SECONDARY, false));
		player.message(new ChatMessage("Events: /events", CC.SECONDARY, false));
		player.message(new ChatMessage("Arenas: /arena", CC.SECONDARY, false));
		player.message(new ChatMessage("Kit Editor: /kiteditor", CC.SECONDARY, false));
		player.message(new ChatMessage("Parties: /parties", CC.SECONDARY, false));
		player.message(new ChatMessage("Lobby: /lobby", CC.SECONDARY, false));
		player.message(new ChatMessage("Settings: /settingsconfig", CC.SECONDARY, false));
	}
}
