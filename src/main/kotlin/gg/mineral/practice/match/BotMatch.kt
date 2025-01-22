package gg.mineral.practice.match

import gg.mineral.bot.ai.goal.DrinkPotionGoal
import gg.mineral.bot.ai.goal.EatGappleGoal
import gg.mineral.bot.ai.goal.MeleeCombatGoal
import gg.mineral.bot.ai.goal.ReplaceArmorGoal
import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.bots.Difficulty
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.MatchManager.registerMatch
import gg.mineral.practice.managers.ProfileManager.getOrCreateProfile
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.items.ItemStacks
import org.bukkit.Bukkit
import org.bukkit.Location

class BotMatch(profile1: Profile, config: BotConfiguration, matchData: MatchData) : Match(matchData) {
    private var clientInstance: ClientInstance? = null
    private val config: BotConfiguration

    init {
        this.profile1 = profile1
        this.config = config
        addParticipants(profile1)
    }

    override fun start() {
        if (noArenas()) return

        registerMatch(this)
        val arena = arenas[data.arenaId]
        val location1 = arena.location1.bukkit(world)
        val location2 = arena.location2.bukkit(world)

        setupLocations(location1, location2)

        teleportPlayers(location1, location2)

        this.clientInstance = Difficulty.spawn(config, location2)
        val bukkitPl = Bukkit.getPlayer(config.uuid) ?: throw NullPointerException("Fake player is null")

        this.profile2 = getOrCreateProfile(bukkitPl)
        addParticipants(profile2!!)
        
        handleOpponentMessages()
        startMatchTimeLimit()
        startCountdown()

        prepareForMatch(participants)
    }

    override fun teleportPlayers(location1: Location, location2: Location) {
        profile1?.let { PlayerUtil.teleport(it, location1) }
    }

    override fun onMatchStart() {
        super.onMatchStart()

        clientInstance?.let {
            it.configuration.pearlCooldown = data.pearlCooldown
            it.startGoals(
                ReplaceArmorGoal(it), DrinkPotionGoal(it),
                EatGappleGoal(it),
                MeleeCombatGoal(it)
            )
        } ?: throw IllegalStateException("Client Instance is null.")
    }

    override fun end(victim: Profile) {
        super.end(victim)

        BotAPI.INSTANCE.despawn(victim.player.uniqueId)
    }

    override fun giveQueueAgainItem(profile: Profile) {
        if (profile.player.isFake()) return

        Bukkit.getServer().scheduler.runTaskLater(
            PracticePlugin.INSTANCE,
            {
                profile.inventory.setItem(
                    profile.inventory.heldItemSlot,
                    ItemStacks.QUEUE_AGAIN,
                    Runnable {
                        if (profile.playerStatus !== PlayerStatus.QUEUEING) {
                            profile.playerStatus = PlayerStatus.QUEUEING

                            BotMatch(
                                profile, config,
                                this@BotMatch.data
                            ).start()
                        }
                    })
            },
            20
        )
    }

    override fun end(attacker: Profile, victim: Profile) {
        super.end(attacker, victim)

        BotAPI.INSTANCE.despawn(attacker.player.uniqueId, victim.player.uniqueId)
    }
}
