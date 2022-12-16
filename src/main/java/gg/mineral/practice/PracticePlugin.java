package gg.mineral.practice;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.commands.config.ArenaCommand;
import gg.mineral.practice.commands.config.CatagoryCommand;
import gg.mineral.practice.commands.config.GametypeCommand;
import gg.mineral.practice.commands.config.KitEditorCommand;
import gg.mineral.practice.commands.config.ListConfigCommands;
import gg.mineral.practice.commands.config.LobbyCommand;
import gg.mineral.practice.commands.config.PartiesCommand;
import gg.mineral.practice.commands.config.QueuetypeCommand;
import gg.mineral.practice.commands.config.SettingsConfigCommand;
import gg.mineral.practice.commands.duel.AcceptCommand;
import gg.mineral.practice.commands.duel.DuelCommand;
import gg.mineral.practice.commands.events.EventCommand;
import gg.mineral.practice.commands.party.PartyCommand;
import gg.mineral.practice.commands.settings.ExtendedSettingsCommand;
import gg.mineral.practice.commands.settings.ToggleDuelRequestsCommand;
import gg.mineral.practice.commands.settings.TogglePlayerVisibilityCommand;
import gg.mineral.practice.commands.spectator.FollowCommand;
import gg.mineral.practice.commands.spectator.SpectateCommand;
import gg.mineral.practice.commands.spectator.StopSpectatingCommand;
import gg.mineral.practice.commands.stats.EloCommand;
import gg.mineral.practice.commands.stats.LeaderboardsCommand;
import gg.mineral.practice.commands.stats.PotsCommand;
import gg.mineral.practice.commands.stats.ViewInventoryCommand;
import gg.mineral.practice.commands.tournament.JoinCommand;
import gg.mineral.practice.commands.tournament.TournamentCommand;
import gg.mineral.practice.listeners.BuildListener;
import gg.mineral.practice.listeners.ComsumeListener;
import gg.mineral.practice.listeners.DamageListener;
import gg.mineral.practice.listeners.DeathListener;
import gg.mineral.practice.listeners.EntryListener;
import gg.mineral.practice.listeners.HealthListener;
import gg.mineral.practice.listeners.InteractListener;
import gg.mineral.practice.listeners.InventoryListener;
import gg.mineral.practice.listeners.MovementListener;
import gg.mineral.practice.listeners.PlayerStatusListener;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.PlayerSettingsManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.sql.SQLManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class PracticePlugin extends JavaPlugin {

	public static PracticePlugin INSTANCE;

	@Override
	public void onEnable() {
		FileConfiguration databaseDetails = new FileConfiguration("database.yml", "plugins/Core");

		String host = databaseDetails.getString("host", "host");
		String port = databaseDetails.getString("port", "3306");
		String database = databaseDetails.getString("database", "database");
		String username = databaseDetails.getString("username", "username");
		String password = databaseDetails.getString("password", "password");

		try {
			SQLManager.initialize(host, port, database, username, password);
		} catch (Exception e) {
			System.out.println("FAILED TO CONNECT TO DATABASE");
		}

		INSTANCE = this;

		ProfileManager.load();
		PlayerSettingsManager.load();
		PartyManager.load();
		ArenaManager.load();
		QueuetypeManager.load();
		KitEditorManager.load();
		PartyManager.load();
		PlayerSettingsManager.load();
		CatagoryManager.load();
		GametypeManager.load();

		registerCommands(new ListConfigCommands(), new ArenaCommand(), new QueuetypeCommand(), new GametypeCommand(),
				new KitEditorCommand(), new LobbyCommand(), new PartiesCommand(),
				new AcceptCommand(), new ViewInventoryCommand(), new DuelCommand(), new SpectateCommand(),
				new PotsCommand(), new EloCommand(), new LeaderboardsCommand(), new PartyCommand(),
				new SettingsConfigCommand(), new FollowCommand(), new TogglePlayerVisibilityCommand(),
				new ToggleDuelRequestsCommand(), new CatagoryCommand(), new ExtendedSettingsCommand(),
				new StopSpectatingCommand(), new TournamentCommand(), new JoinCommand(), new EventCommand());

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
}
