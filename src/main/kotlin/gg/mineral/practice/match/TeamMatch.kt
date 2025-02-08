package gg.mineral.practice.match

import gg.mineral.api.collection.GlueList
import gg.mineral.api.nametag.NametagGroup
import gg.mineral.bot.api.BotAPI
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.menus.InventoryStatsMenu
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.MatchManager.registerMatch
import gg.mineral.practice.managers.MatchManager.remove
import gg.mineral.practice.managers.ProfileManager.setInventoryStats
import gg.mineral.practice.match.appender.MatchAppender
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.match.data.MatchStatisticCollector
import gg.mineral.practice.party.Party
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard
import gg.mineral.practice.scoreboard.impl.PartyMatchScoreboard
import gg.mineral.practice.scoreboard.impl.TeamBoxingScoreboard
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.Strings
import io.isles.nametagapi.NametagAPI
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

open class TeamMatch : Match, MatchAppender {

    val team1Players = Object2BooleanLinkedOpenHashMap<Profile>()
    protected val team2Players = Object2BooleanLinkedOpenHashMap<Profile>()
    private var team1InventoryStatsMenus: MutableList<InventoryStatsMenu> = GlueList()
    private var team2InventoryStatsMenus: MutableList<InventoryStatsMenu> = GlueList()
    var nametagGroups: Array<NametagGroup>? = null

    var team1HitCount: Int = 0
    var team2HitCount: Int = 0

    var team1RequiredHitCount: Int = 0
    var team2RequiredHitCount: Int = 0

    constructor(team1: Collection<Profile>, team2: Collection<Profile>, matchData: MatchData) : super(matchData) {
        fun Object2BooleanLinkedOpenHashMap<Profile>.add(profile: Profile) = put(profile, true)
        for (profile in team1) team1Players.add(profile)
        for (profile in team2) team2Players.add(profile)
    }

    constructor(party1: Party, party2: Party, matchData: MatchData) : this(
        party1.partyMembers,
        party2.partyMembers,
        matchData
    )

    constructor(party: Party, matchData: MatchData) : this(party.partyMembers, matchData)

    constructor(profiles: Collection<Profile>, matchData: MatchData) : super(matchData) {
        participants.addAll(profiles)
        val size = participants.size
        val team1 = participants.subList(0, (size + 1) / 2)
        val team2 = participants.subList((size + 1) / 2, size)

        fun Object2BooleanLinkedOpenHashMap<Profile>.add(profile: Profile) = put(profile, true)
        for (profile in team1) team1Players.add(profile)
        for (profile in team2) team2Players.add(profile)
    }

    override fun startMatchTimeLimit() {
        this.timeRemaining = timeLimitSec
        Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(
            PracticePlugin.INSTANCE,
            {
                if (ended) return@scheduleSyncRepeatingTask
                if (timeRemaining-- <= 0) for (profile in team1Players.alive()) end(profile)
            }, 0, 20
        )
    }

    override fun start() {
        if (noArenas()) return
        if (!registerMatch(this)) return

        val arena = arenas[data.arenaId] ?: throw NullPointerException("Arena not found")
        val location1 = arena.location1.bukkit(world)
        val location2 = arena.location2.bukkit(world)
        setupLocations(location1, location2)

        team1Players.alive { teamMember: Profile ->
            participants.add(
                teamMember
            )
        }
        team2Players.alive { teamMember: Profile ->
            participants.add(
                teamMember
            )
        }

        this.profile1 = team1Players.firstKey()
        this.profile2 = team2Players.firstKey()
        this.team1RequiredHitCount = team1Players.size * 100
        this.team2RequiredHitCount = team2Players.size * 100

        this.nametagGroups = setDisplayNameBoard()

        team1Players.alive { teamMember: Profile ->
            PlayerUtil.teleport(
                teamMember,
                location1
            )
        }
        team2Players.alive { teamMember: Profile ->
            PlayerUtil.teleport(
                teamMember,
                location2
            )
        }

        prepareForMatch(participants)

        startMatchTimeLimit()
        startCountdown()
    }

    override fun setScoreboard(p: Profile) {
        p.scoreboard = if (data.boxing) TeamBoxingScoreboard.INSTANCE else PartyMatchScoreboard.INSTANCE
    }

    override fun end(victim: Profile) {
        if (ended || victim.dead) return

        victim.dead = true

        stat(victim) { it.end(false) }

        val isTeam1 = team1Players.all().contains(victim)
        val attackerTeam = if (isTeam1) team2Players else team1Players
        val victimTeam = if (isTeam1) team1Players else team2Players
        val attackerInventoryStatsMenus = if (isTeam1)
            team2InventoryStatsMenus
        else
            team1InventoryStatsMenus
        val victimInventoryStatsMenus = if (isTeam1)
            team1InventoryStatsMenus
        else
            team2InventoryStatsMenus
        val attackerTeamHits = if (isTeam1) team2HitCount else team1HitCount
        val victimTeamHits = if (isTeam1) team1HitCount else team2HitCount

        stat(victim) {
            victimInventoryStatsMenus.add(
                setInventoryStats(
                    it
                )
            )
        }

        pearlCooldown.cooldowns.removeInt(victim.uuid)
        victim.player.heal()
        victim.player.removePotionEffects()
        victim.inventory.clear()

        victim.scoreboard = DefaultScoreboard.INSTANCE

        victimTeam.reportDeath(victim)

        val hasKiller = victim.killer != null
        var message = if (hasKiller) ChatMessages.KILLED_BY_PLAYER else ChatMessages.DIED
        message = message.clone().replace("%victim%", victim.name)
        val finalMessage = if (hasKiller) message.replace("%attacker%", victim.killer!!.name) else message

        for (profile in participants) profile.message(finalMessage)

        val victimsAlive = victimTeam.alive()

        if (!victimsAlive.isEmpty()) {
            participants.remove(victim)
            if (victim.match == this) victim.match = null

            if (BotAPI.INSTANCE.despawn(victim.player.uniqueId)) return

            var allBots = true

            for (profile in participants) if (!profile.player.isFake()) {
                allBots = false
                break
            }

            victimsAlive.first?.let { victim.spectate(it) }

            if (allBots) for (profile in victimsAlive) end(profile)

            return
        }

        ended = true

        Bukkit.getScheduler().cancelTask(timeTaskId)

        nametagGroups?.let {
            for (nametagGroup in it) {
                nametagGroup.delete()
                for (player in nametagGroup.players) refreshBukkitScoreboard(player)
            }
        }

        val attackerTeamIterator: Iterator<Profile> = attackerTeam.alive().iterator()

        val attackerTeamLeader = attackerTeamIterator.next()

        attackerEndMatch(attackerTeamLeader, attackerInventoryStatsMenus)

        while (attackerTeamIterator.hasNext()) attackerEndMatch(
            attackerTeamIterator.next(),
            attackerInventoryStatsMenus
        )

        for (invStats in attackerInventoryStatsMenus) {
            val profile = invStats.matchStatisticCollector.profile
            setInventoryStats(profile, attackerInventoryStatsMenus)
        }

        for (invStats in victimInventoryStatsMenus) {
            val profile = invStats.matchStatisticCollector.profile
            setInventoryStats(profile, victimInventoryStatsMenus)
        }

        if (!remove(this)) return

        val winMessage = TextComponent(
            CC.GREEN + "Winner: " + CC.GRAY + attackerTeamLeader.name + "'s team"
        )
        val loseMessage = TextComponent(
            CC.RED + "Loser: " + CC.GRAY + victimTeam.firstKey()!!.name + "'s team"
        )
        loseMessage.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder(CC.RED + "Hits: " + victimTeamHits)
                .create()
        )
        winMessage.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder(CC.GREEN + "Hits: " + attackerTeamHits).create()
        )
        loseMessage.clickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + victim.name)
        winMessage.clickEvent = ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/viewinventory " + attackerTeamLeader.name
        )

        for (profile in participants) {
            profile.player.sendMessage(CC.SEPARATOR)
            profile.player.sendMessage(Strings.MATCH_RESULTS)
            profile.player.spigot().sendMessage(winMessage)
            profile.player.spigot().sendMessage(loseMessage)
            profile.player.sendMessage(CC.SEPARATOR)
        }

        participants.remove(victim)

        if (!BotAPI.INSTANCE.despawn(victim.player.uniqueId)) {
            victim.player.removePotionEffects()
            victim.teleportToLobby()

            if (victim.party != null) victim.inventory.setInventoryForParty()
            else victim.inventory.setInventoryForLobby()
        }

        if (victim.match == this) victim.match = null

        for (spectator in spectators) {
            val player = spectator.player
            player.sendMessage(CC.SEPARATOR)
            player.sendMessage(Strings.MATCH_RESULTS)
            player.spigot().sendMessage(winMessage)
            player.spigot().sendMessage(loseMessage)
            player.sendMessage(CC.SEPARATOR)
            spectator.stopSpectating()
        }

        clearWorld()
    }

    override fun getTeam(profile: Profile, alive: Boolean): ProfileList {
        val profileSet = if (team1Players.all().contains(profile))
            team1Players
        else
            team2Players
        return profileSet.alive()
    }

    override fun getOpponent(p: Profile): Profile {
        return if (team1Players.all().contains(p))
            team2Players.firstKey()!!
        else
            team1Players.firstKey()!!
    }

    private fun attackerEndMatch(attacker: Profile, attackerInventoryStatsMenus: MutableList<InventoryStatsMenu>) {
        stat(attacker) { collector: MatchStatisticCollector -> collector.end(true) }

        stat(attacker) { collector: MatchStatisticCollector? ->
            attackerInventoryStatsMenus
                .add(setInventoryStats(collector!!))
        }

        pearlCooldown.cooldowns.removeInt(attacker.uuid)
        attacker.player.heal()
        attacker.player.removePotionEffects()
        attacker.inventory.clear()

        attacker.scoreboard = MatchEndScoreboard.INSTANCE

        giveQueueAgainItem(attacker)

        Bukkit.getServer().scheduler.runTaskLater(PracticePlugin.INSTANCE, {
            if (attacker.match?.ended == false) return@runTaskLater
            attacker.teleportToLobby()
            if (attacker.party != null) attacker.inventory.setInventoryForParty()
            else attacker.inventory.setInventoryForLobby()

            if (attacker.match == this) attacker.match = null
            attacker.scoreboard = DefaultScoreboard.INSTANCE
            BotAPI.INSTANCE.despawn(attacker.player.uniqueId)
        }, POST_MATCH_TIME.toLong())
    }

    override fun incrementTeamHitCount(attacker: Profile, victim: Profile): Boolean {
        stat(attacker) { it.increaseHitCount() }
        stat(victim) { it.resetCombo() }

        val isTeam1 = team1Players.all().contains(attacker)
        val hitCount = if (isTeam1) ++team1HitCount else ++team2HitCount
        val requiredHitCount = if (isTeam1) team1RequiredHitCount else team2RequiredHitCount
        val opponentTeam = if (isTeam1) team2Players else team1Players

        if (hitCount >= requiredHitCount
            && data.boxing
        ) {
            opponentTeam.alive { this.end(it) }
            return true
        }

        return false
    }

    fun setDisplayNameBoard(): Array<NametagGroup> {
        val playerGroup = NametagGroup()
        val opponentGroup = NametagGroup()

        team1Players.alive { profile: Profile ->
            playerGroup.add(
                profile.player
            )
        }
        team2Players.alive { profile: Profile ->
            opponentGroup.add(
                profile.player
            )
        }

        team1Players.alive { profile: Profile ->
            NametagAPI.setPrefix(
                playerGroup,
                profile.name,
                CC.GREEN
            )
        }
        team2Players.alive { profile: Profile ->
            NametagAPI.setPrefix(
                playerGroup,
                profile.name,
                CC.RED
            )
        }

        team2Players.alive { profile: Profile ->
            NametagAPI.setPrefix(
                opponentGroup,
                profile.name,
                CC.GREEN
            )
        }
        team1Players.alive { profile: Profile ->
            NametagAPI.setPrefix(
                opponentGroup,
                profile.name,
                CC.RED
            )
        }

        return arrayOf(playerGroup, opponentGroup)
    }

    companion object {
        fun refreshBukkitScoreboard(player: Player) {
            val scoreboard = player.scoreboard
            val manager = Bukkit.getScoreboardManager()
            val blankScoreboard = manager.newScoreboard
            Bukkit.getScheduler().runTaskLater(
                PracticePlugin.INSTANCE,
                {
                    player.scoreboard = blankScoreboard
                    Bukkit.getScheduler().runTaskLater(
                        PracticePlugin.INSTANCE,
                        { player.scoreboard = scoreboard }, 1L
                    )
                }, 1L
            )
        }
    }
}
