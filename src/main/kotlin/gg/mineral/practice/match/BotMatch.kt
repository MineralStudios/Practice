package gg.mineral.practice.match

import gg.mineral.bot.ai.goal.*
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
import java.lang.ref.WeakReference

class BotMatch(profile1: Profile, private val config: BotConfiguration, matchData: MatchData) :
    Match(matchData, profile1) {
    private var clientInstance: WeakReference<ClientInstance>? = null

    override fun cleanup() {
        super.cleanup()
        clientInstance = null
    }

    override fun start() {
        if (noArenas()) {
            onError("No arenas available")
            return
        }

        registerMatch(this)
        val arena = arenas[data.arenaId] ?: run {
            onError("Arena not found")
            return
        }
        val location1 = arena.location1.bukkit(world) ?: return
        val location2 = arena.location2.bukkit(world) ?: return

        setupLocations(location1, location2)

        teleportPlayers(location1, location2)

        this.clientInstance = Difficulty.spawn(config, location2)
        PracticePlugin.INSTANCE.entryListener.addJoinListener(config.uuid) {
            getOrCreateProfile(it).let { profile ->
                this.profile2 = profile
                addParticipants(profile)
            }

            handleOpponentMessages()
            startCountdown()

            prepareForMatch(participants)
        }
    }

    override fun teleportPlayers(location1: Location, location2: Location) {
        profile1?.let { PlayerUtil.teleport(it, location1) }
    }

    override fun onStart() {
        super.onStart()

        clientInstance?.get()?.let {
            it.configuration.pearlCooldown = data.pearlCooldown
            it.startGoals(
                ReplaceArmorGoal(it),
                HealSoupGoal(it),
                ThrowHealthPotGoal(it),
                DrinkPotionGoal(it),
                EatGappleGoal(it),
                EatFoodGoal(it),
                //ThrowDebuffPotGoal(it),
                ThrowPearlGoal(it),
                DropEmptyBowlGoal(it),
                MeleeCombatGoal(it)
            )
        } ?: onError("Client instance is null")
    }

    override fun end(victim: Profile) {
        super.end(victim)

        BotAPI.INSTANCE.despawn(victim.uuid)
    }

    override fun giveQueueAgainItem(profile: Profile) {
        if (profile.player?.isFake() == true) return

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

        BotAPI.INSTANCE.despawn(attacker.uuid, victim.uuid)
    }
}
