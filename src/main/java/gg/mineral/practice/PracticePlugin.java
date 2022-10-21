package gg.mineral.practice;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class PracticePlugin extends JavaPlugin {

	public static PracticePlugin INSTANCE;

	@Override
	public void onEnable() {
		INSTANCE = this;

		registerCommands(new ListConfigCommands(), new ArenaCommand(), new QueuetypeCommand(), new GametypeCommand(),
				new KitEditorCommand(), new LobbyCommand(), new PartiesCommand(),
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
		for (Command cmd : cmds) {
			MinecraftServer.getServer().server.getCommandMap().register(cmd.getName(), "Practice", cmd);
		}
	}

	public void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}
}
