package ms.uk.eclipse;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import ms.uk.eclipse.commands.config.ArenaCommand;
import ms.uk.eclipse.commands.config.CatagoryCommand;
import ms.uk.eclipse.commands.config.GametypeCommand;
import ms.uk.eclipse.commands.config.KitEditorCommand;
import ms.uk.eclipse.commands.config.ListConfigCommands;
import ms.uk.eclipse.commands.config.LobbyCommand;
import ms.uk.eclipse.commands.config.PartiesCommand;
import ms.uk.eclipse.commands.config.PvPBotsCommand;
import ms.uk.eclipse.commands.config.QueuetypeCommand;
import ms.uk.eclipse.commands.config.SettingsConfigCommand;
import ms.uk.eclipse.commands.duel.AcceptCommand;
import ms.uk.eclipse.commands.duel.DuelCommand;
import ms.uk.eclipse.commands.party.PartyCommand;
import ms.uk.eclipse.commands.settings.ExtendedSettingsCommand;
import ms.uk.eclipse.commands.settings.ToggleDuelRequestsCommand;
import ms.uk.eclipse.commands.settings.TogglePlayerVisibilityCommand;
import ms.uk.eclipse.commands.spectator.FollowCommand;
import ms.uk.eclipse.commands.spectator.SpectateCommand;
import ms.uk.eclipse.commands.spectator.StopSpectatingCommand;
import ms.uk.eclipse.commands.stats.EloCommand;
import ms.uk.eclipse.commands.stats.LeaderboardsCommand;
import ms.uk.eclipse.commands.stats.PotsCommand;
import ms.uk.eclipse.commands.stats.ViewInventoryCommand;
import ms.uk.eclipse.commands.tournament.JoinCommand;
import ms.uk.eclipse.commands.tournament.TournamentCommand;
import ms.uk.eclipse.kit.KitEditorManager;
import ms.uk.eclipse.listeners.BuildListener;
import ms.uk.eclipse.listeners.ComsumeListener;
import ms.uk.eclipse.listeners.DamageListener;
import ms.uk.eclipse.listeners.DeathListener;
import ms.uk.eclipse.listeners.EntryListener;
import ms.uk.eclipse.listeners.HealthListener;
import ms.uk.eclipse.listeners.InteractListener;
import ms.uk.eclipse.listeners.InventoryListener;
import ms.uk.eclipse.listeners.MovementListener;
import ms.uk.eclipse.listeners.PlayerStatusListener;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.CatagoryManager;
import ms.uk.eclipse.managers.EloManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.LeaderboardManager;
import ms.uk.eclipse.managers.MatchManager;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.PlayerSettingsManager;
import ms.uk.eclipse.managers.PvPBotsManager;
import ms.uk.eclipse.managers.QueueEntryManager;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.managers.TournamentManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class PracticePlugin extends JavaPlugin {

	public static PracticePlugin INSTANCE;
	ArenaManager arenaManager;
	GametypeManager gametypeManager;
	QueuetypeManager queuetypeManager;
	MatchManager matchManager;
	PlayerManager playerManager;
	PlayerSettingsManager playerSettingsManager;
	KitEditorManager kitEditorManager;
	PartyManager partyManager;
	CatagoryManager catagoryManager;
	PvPBotsManager pvPBotsManager;
	LeaderboardManager leaderboardManager;
	EloManager eloManager;
	TournamentManager tournamentManager;
	QueueEntryManager queueEntryManager;

	@Override
	public void onEnable() {
		INSTANCE = this;

		eloManager = new EloManager();
		leaderboardManager = new LeaderboardManager();
		matchManager = new MatchManager();
		arenaManager = new ArenaManager();
		gametypeManager = new GametypeManager();
		queuetypeManager = new QueuetypeManager();
		playerManager = new PlayerManager();
		playerSettingsManager = new PlayerSettingsManager();
		kitEditorManager = new KitEditorManager();
		partyManager = new PartyManager();
		catagoryManager = new CatagoryManager();
		pvPBotsManager = new PvPBotsManager();
		queueEntryManager = new QueueEntryManager();
		tournamentManager = new TournamentManager();

		playerManager.load();
		playerSettingsManager.load();
		partyManager.load();
		pvPBotsManager.load();
		arenaManager.load();
		queuetypeManager.load();
		catagoryManager.load();
		gametypeManager.load();

		registerCommands(new ListConfigCommands(), new ArenaCommand(), new QueuetypeCommand(), new GametypeCommand(),
				new KitEditorCommand(), new LobbyCommand(), new PartiesCommand(), new PvPBotsCommand(),
				new AcceptCommand(), new ViewInventoryCommand(), new DuelCommand(), new SpectateCommand(),
				new PotsCommand(), new EloCommand(), new LeaderboardsCommand(), new PartyCommand(),
				new SettingsConfigCommand(), new FollowCommand(), new TogglePlayerVisibilityCommand(),
				new ToggleDuelRequestsCommand(), new CatagoryCommand(), new ExtendedSettingsCommand(),
				new StopSpectatingCommand(), new TournamentCommand(), new JoinCommand());

		registerListeners(new BuildListener(), new InteractListener(), new ComsumeListener(), new InventoryListener(),
				new DeathListener(), new DamageListener(), new EntryListener(), new HealthListener(),
				new MovementListener(), new PlayerStatusListener());
	}

	public void registerCommands(Command... cmds) {
		for (Command c : cmds) {
			MinecraftServer.getServer().server.getCommandMap().registerOverride(c.getName(), "Practice", c);
		}
	}

	public void registerListeners(Listener... listeners) {
		for (Listener l : listeners) {
			Bukkit.getPluginManager().registerEvents(l, this);
		}
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}

	public GametypeManager getGametypeManager() {
		return gametypeManager;
	}

	public QueuetypeManager getQueuetypeManager() {
		return queuetypeManager;
	}

	public PlayerSettingsManager getSettingsManager() {
		return playerSettingsManager;
	}

	public QueueEntryManager getQueueEntryManager() {
		return queueEntryManager;
	}

	public KitEditorManager getKitEditorManager() {
		return kitEditorManager;
	}

	public PartyManager getPartyManager() {
		return partyManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public MatchManager getMatchManager() {
		return matchManager;
	}

	public PvPBotsManager getPvPBotsManager() {
		return pvPBotsManager;
	}

	public CatagoryManager getCatagoryManager() {
		return catagoryManager;
	}

	public LeaderboardManager getLeaderboardManager() {
		return leaderboardManager;
	}

	public EloManager getEloManager() {
		return eloManager;
	}

	public TournamentManager getTournamentManager() {
		return tournamentManager;
	}
}
