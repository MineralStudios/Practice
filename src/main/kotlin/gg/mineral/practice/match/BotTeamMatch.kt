package gg.mineral.practice.match

import gg.mineral.api.collection.GlueList
import gg.mineral.bot.ai.goal.*
import gg.mineral.bot.api.concurrent.ListenableFuture
import gg.mineral.bot.api.concurrent.awaitAll
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.practice.bots.Difficulty
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.MatchManager.registerMatch
import gg.mineral.practice.managers.ProfileManager.getOrCreateProfile
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.util.PlayerUtil
import org.bukkit.Bukkit
import java.lang.ref.WeakReference
import java.util.*

class BotTeamMatch(
    team1: Queue<Profile>, team2: Queue<Profile>, private var team1Bots: Collection<BotConfiguration>,
    private var team2Bots: Collection<BotConfiguration>, matchData: MatchData
) :
    TeamMatch(team1, team2, matchData) {
    private var team1BotInstances: MutableList<WeakReference<ClientInstance>> = GlueList()
    private var team2BotInstances: MutableList<WeakReference<ClientInstance>> = GlueList()

    override fun cleanup() {
        super.cleanup()
        team1BotInstances.clear()
        team2BotInstances.clear()
    }

    override fun onStart() {
        super.onStart()

        for (instance in team1BotInstances) {
            instance.get()?.apply {
                configuration.pearlCooldown = data.pearlCooldown
                startGoals(
                    ReplaceArmorGoal(this),
                    HealSoupGoal(this),
                    ThrowHealthPotGoal(this),
                    DrinkPotionGoal(this),
                    EatGappleGoal(this),
                    EatFoodGoal(this),
                    //ThrowDebuffPotGoal(this),
                    ThrowPearlGoal(this),
                    DropEmptyBowlGoal(this),
                    MeleeCombatGoal(this)
                )
            }
        }

        for (instance in team2BotInstances) {
            instance.get()?.apply {
                configuration.pearlCooldown = data.pearlCooldown
                startGoals(
                    ReplaceArmorGoal(this),
                    HealSoupGoal(this),
                    ThrowHealthPotGoal(this),
                    DrinkPotionGoal(this),
                    EatGappleGoal(this),
                    EatFoodGoal(this),
                    //ThrowDebuffPotGoal(this),
                    ThrowPearlGoal(this),
                    DropEmptyBowlGoal(this),
                    MeleeCombatGoal(this)
                )
            }
        }
    }

    override fun start() {
        if (noArenas()) return

        registerMatch(this)
        val arena = arenas[data.arenaId] ?: throw NullPointerException("Arena not found")
        val location1 = arena.location1.bukkit(world) ?: return
        val location2 = arena.location2.bukkit(world) ?: return
        setupLocations(location1, location2)

        team1Players.alive { PlayerUtil.teleport(it, location1) }
        team2Players.alive { PlayerUtil.teleport(it, location2) }

        var suffix = 0
        val spawnFutures = mutableListOf<ListenableFuture<ClientInstance>>()

        for (config in team1Bots) {
            config.usernameSuffix = "" + (suffix++)
            val future = Difficulty.spawn(config, location1)
            spawnFutures += future
            future.onComplete { instance ->
                team1BotInstances.add(WeakReference(instance))
                val player = Bukkit.getPlayer(instance.configuration.uuid) ?: error("Player not found")
                team1Players.put(getOrCreateProfile(player), true)
            }
        }

        for (config in team2Bots) {
            config.usernameSuffix = "" + (suffix++)
            val future = Difficulty.spawn(config, location2)
            spawnFutures += future
            future.onComplete { instance ->
                team2BotInstances.add(WeakReference(instance))
                val player = Bukkit.getPlayer(instance.configuration.uuid) ?: error("Player not found")
                team2Players.put(getOrCreateProfile(player), true)
            }
        }

        awaitAll(spawnFutures, onSuccess = {
            team1Players.alive { participants.add(it) }
            team2Players.alive { participants.add(it) }

            this.profile1 = team1Players.firstKey()
            this.profile2 = team2Players.firstKey()
            this.team1RequiredHitCount = team1Players.size * 100
            this.team2RequiredHitCount = team2Players.size * 100

            this.nametagGroups = setDisplayNameBoard()

            team1Players.alive { teamMember ->
                prepareForMatch(teamMember)
                for (instance in team1BotInstances) instance.get()?.configuration
                    ?.friendlyUUIDs?.add(teamMember.uuid)
            }

            team2Players.alive { teamMember ->
                prepareForMatch(teamMember)
                for (instance in team2BotInstances) instance.get()?.configuration
                    ?.friendlyUUIDs?.add(teamMember.uuid)
            }

            startCountdown()
        }, onError = {
            participants.forEach { end(it) }
        })
    }

}
