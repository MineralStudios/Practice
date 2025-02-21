package gg.mineral.practice.match

import gg.mineral.api.collection.GlueList
import gg.mineral.bot.ai.goal.*
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
import java.util.*

class BotTeamMatch(
    team1: Queue<Profile>, team2: Queue<Profile>, private var team1Bots: Collection<BotConfiguration>,
    private var team2Bots: Collection<BotConfiguration>, matchData: MatchData
) :
    TeamMatch(team1, team2, matchData) {
    private var team1BotInstances: MutableList<ClientInstance> = GlueList()
    private var team2BotInstances: MutableList<ClientInstance> = GlueList()

    override fun onMatchStart() {
        super.onMatchStart()

        for (instance in team1BotInstances) {
            instance.configuration.pearlCooldown = data.pearlCooldown
            instance.startGoals(
                ReplaceArmorGoal(instance),
                HealSoupGoal(instance),
                ThrowHealthPotGoal(instance),
                DrinkPotionGoal(instance),
                EatGappleGoal(instance),
                EatFoodGoal(instance),
                //ThrowDebuffPotGoal(instance),
                ThrowPearlGoal(instance),
                DropEmptyBowlGoal(instance),
                MeleeCombatGoal(instance)
            )
        }

        for (instance in team2BotInstances) {
            instance.configuration.pearlCooldown = data.pearlCooldown
            instance.startGoals(
                ReplaceArmorGoal(instance),
                HealSoupGoal(instance),
                ThrowHealthPotGoal(instance),
                DrinkPotionGoal(instance),
                EatGappleGoal(instance),
                EatFoodGoal(instance),
                //ThrowDebuffPotGoal(instance),
                ThrowPearlGoal(instance),
                DropEmptyBowlGoal(instance),
                MeleeCombatGoal(instance)
            )
        }
    }

    override fun start() {
        if (noArenas()) return

        registerMatch(this)
        val arena = arenas[data.arenaId] ?: throw NullPointerException("Arena not found")
        val location1 = arena.location1.bukkit(world)
        val location2 = arena.location2.bukkit(world)
        setupLocations(location1, location2)

        team1Players.alive { teamMember -> PlayerUtil.teleport(teamMember, location1) }
        team2Players.alive { teamMember -> PlayerUtil.teleport(teamMember, location2) }

        var suffix = 0

        for (config in team1Bots) {
            config.usernameSuffix = "" + (suffix++)
            val instance = Difficulty.spawn(config, location1)
            team1Players
                .put(getOrCreateProfile(Bukkit.getPlayer(config.uuid)), true)
            team1BotInstances.add(instance)
        }

        for (config in team2Bots) {
            config.usernameSuffix = "" + (suffix++)
            val clientInstance = Difficulty.spawn(config, location2)
            team2Players
                .put(getOrCreateProfile(Bukkit.getPlayer(config.uuid)), true)
            team2BotInstances.add(clientInstance)
        }

        team1Players.alive { teamMember -> participants.add(teamMember) }
        team2Players.alive { teamMember -> participants.add(teamMember) }

        this.profile1 = team1Players.firstKey()
        this.profile2 = team2Players.firstKey()
        this.team1RequiredHitCount = team1Players.size * 100
        this.team2RequiredHitCount = team2Players.size * 100

        this.nametagGroups = setDisplayNameBoard()

        team1Players.alive { teamMember ->
            prepareForMatch(teamMember)
            for (instance in team1BotInstances) instance.configuration
                .friendlyUUIDs.add(teamMember.uuid)
        }

        team2Players.alive { teamMember ->
            prepareForMatch(teamMember)
            for (instance in team2BotInstances) instance.configuration
                .friendlyUUIDs.add(teamMember.uuid)
        }

        startCountdown()
    }
}
