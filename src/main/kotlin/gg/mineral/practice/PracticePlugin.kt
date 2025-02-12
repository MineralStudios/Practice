package gg.mineral.practice

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import dev.rollczi.litecommands.LiteCommands
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import gg.mineral.practice.arena.Arena
import gg.mineral.practice.category.Category
import gg.mineral.practice.commands.config.*
import gg.mineral.practice.commands.context.ProfileContext
import gg.mineral.practice.commands.context.SpawnLocationContext
import gg.mineral.practice.commands.duel.AcceptCommand
import gg.mineral.practice.commands.duel.DuelCommand
import gg.mineral.practice.commands.events.EventCommand
import gg.mineral.practice.commands.kit.LeaveCommand
import gg.mineral.practice.commands.match.ForfeitCommand
import gg.mineral.practice.commands.party.PartyCommand
import gg.mineral.practice.commands.resolver.*
import gg.mineral.practice.commands.settings.*
import gg.mineral.practice.commands.spectator.FollowCommand
import gg.mineral.practice.commands.spectator.SpectateCommand
import gg.mineral.practice.commands.spectator.StopSpectatingCommand
import gg.mineral.practice.commands.stats.EloCommand
import gg.mineral.practice.commands.stats.LeaderboardsCommand
import gg.mineral.practice.commands.stats.PotsCommand
import gg.mineral.practice.commands.stats.ViewInventoryCommand
import gg.mineral.practice.commands.testing.BotTestingCommand
import gg.mineral.practice.commands.tournament.JoinCommand
import gg.mineral.practice.commands.tournament.TournamentCommand
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.listeners.*
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.managers.CategoryManager
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.managers.QueuetypeManager
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.world.SpawnLocation
import gg.mineral.practice.util.world.VoidWorldGenerator
import gg.mineral.server.combat.KnockbackProfile
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


class PracticePlugin : JavaPlugin() {
    companion object {
        lateinit var INSTANCE: PracticePlugin
    }

    override fun onLoad() {
        INSTANCE = this

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

    private lateinit var liteCommands: LiteCommands<CommandSender>

    override fun onEnable() {
        PacketEvents.getAPI().init()

        ArenaManager.load()
        QueuetypeManager.load()
        CategoryManager.load()
        GametypeManager.load()
        // TODO: change invalid command messages
        this.liteCommands = LiteBukkitFactory.builder("Practice", this)
            .commands(
                PracticeCommand(),
                ArenaCommand(),
                CategoryCommand(),
                GametypeCommand(),
                KitEditorCommand(),
                LeaderboardConfigCommand(),
                LobbyCommand(),
                QueuetypeCommand(),
                PartiesCommand(),
                SettingsConfigCommand(),
                SpectateConfigCommand(),
                AcceptCommand(),
                DuelCommand(),
                EventCommand(),
                LeaveCommand(),
                PartyCommand(),
                ToggleDuelRequestsCommand(),
                SettingsCommand(),
                DayCommand(),
                NightCommand(),
                TogglePartyRequestsCommand(),
                TogglePlayerVisibilityCommand(),
                ToggleScoreboardCommand(),
                FollowCommand(),
                SpectateCommand(),
                StopSpectatingCommand(),
                EloCommand(),
                ViewInventoryCommand(),
                PotsCommand(),
                LeaderboardsCommand(),
                TournamentCommand(),
                JoinCommand(),
                BotTestingCommand(),
                ForfeitCommand()
            )
            .context(Profile::class.java, ProfileContext())
            .context(
                SpawnLocation::class.java, SpawnLocationContext()
            )
            .argument(
                Arena::class.java, ArenaResolver()
            )
            .argument(
                Category::class.java, CategoryResolver()
            )
            .argument(
                Gametype::class.java, GametypeResolver()
            )
            .argument(
                KnockbackProfile::class.java, KnockbackProfileResolver()
            )
            .argument(
                Profile::class.java, ProfileResolver()
            )
            .argument(
                ProfileData::class.java, ProfileDataResolver()
            )
            .argument(
                Queuetype::class.java, QueuetypeResolver()
            )
            .build()

        PacketEvents.getAPI().eventManager.registerListener(
            PacketEventsListener(), PacketListenerPriority.NORMAL
        )
        registerListeners(
            BuildListener(), InteractListener(), ComsumeListener(), InventoryListener(),
            DeathListener(), DamageListener(), EntryListener(), HealthListener(),
            MovementListener(), ProjectileListener(), CommandListener()
        )
    }

    private fun initCommands() {

    }

    override fun onDisable() {
        Bukkit.getServer().scheduler.cancelTasks(this)
        PacketEvents.getAPI().terminate()
        this.liteCommands.unregister()
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String) = VoidWorldGenerator()

    private fun registerListeners(vararg listeners: Listener) {
        for (listener in listeners) Bukkit.getPluginManager().registerEvents(listener, this)
    }
}
