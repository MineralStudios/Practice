package gg.mineral.practice.events

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.managers.EventManager
import gg.mineral.practice.managers.ProfileManager
import gg.mineral.practice.match.EventMatch
import gg.mineral.practice.match.Match
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.traits.Spectatable
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.World
import java.lang.ref.WeakReference

class Event(hostProfile: Profile, val eventArenaId: Byte) : Spectatable {
    private val matches = GlueList<Match>()
    override val spectators = ProfileList()
    override val world: WeakReference<World>
    private val matchData: MatchData
    private var round = 1
    val host: String
    private var started = false
    override var ended = false
    override val participants = ProfileList()

    init {
        val duelSettings = hostProfile.duelSettings
        val enabledArenas = Byte2BooleanOpenHashMap().apply { put(eventArenaId, true) }
        this.matchData = MatchData(duelSettings, enabledArenas)
        matchData.arenaId = eventArenaId
        this.host = hostProfile.name
        val arena = ArenaManager.arenas.get(eventArenaId)
        this.world = arena?.generate() ?: throw NullPointerException("Arena not found")
        this.addPlayer(hostProfile)
    }

    fun addPlayer(profile: Profile): Boolean {
        if (started) return profile.message(ErrorMessages.EVENT_STARTED).let { true }

        ArenaManager.arenas.get(eventArenaId)?.waitingLocation?.bukkit(this.world)
            ?.let { PlayerUtil.teleport(profile, it) }
            ?: error("Arena not found")
        profile.event = this

        ProfileManager.broadcast(participants, ChatMessages.JOINED_EVENT.clone().replace("%player%", profile.name))
        return participants.add(profile)
    }

    private fun startRound() {
        if (participants.size == 1) {
            val winner: Profile? = participants.first
            winner?.event = null

            for (spectator in spectators) spectator.stopSpectating()

            ended = true
            EventManager.remove(this)
            return
        }

        if (participants.isEmpty()) {
            for (spectator in spectators) spectator.stopSpectating()
            EventManager.remove(this)
            ended = true
            return
        }

        val iterator = participants.iterator()

        val profile1 = iterator.next()

        if (!iterator.hasNext()) {
            profile1.message(ChatMessages.NO_OPPONENT)
            return
        }

        val profile2 = iterator.next()

        val match = EventMatch(profile1, profile2, matchData, this)
        match.start()
        matches.add(match)
    }

    fun removePlayer(profile: Profile) {
        if (!participants.remove(profile)) return
        profile.event = null

        ProfileManager.broadcast(participants, ChatMessages.LEFT_EVENT.clone().replace("%player%", profile.name))

        if (participants.isEmpty()) {
            EventManager.remove(this)
            ended = true
            return
        }

        if (participants.size == 1) {
            val winner = participants.first
            winner?.event = null

            for (spectator in spectators) spectator.stopSpectating()

            EventManager.remove(this)
            ended = true

            if (winner != null)
                ProfileManager.broadcast(ChatMessages.WON_EVENT.clone().replace("%player%", winner.name))
        }
    }

    fun start() {
        if (started) return

        EventManager.registerEvent(this)

        val messageToBroadcast = ChatMessages.BROADCAST_EVENT.clone()
            .replace("%player%", host).setTextEvent(
                ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/join $host"
                ),
                ChatMessages.CLICK_TO_JOIN
            )

        ProfileManager.broadcast(messageToBroadcast)

        Bukkit.getScheduler().runTaskLater(PracticePlugin.INSTANCE, {
            started = true

            if (participants.size == 1) {
                val winner = participants.first
                winner?.event = null

                for (spectator in spectators) spectator.stopSpectating()

                winner?.message(ErrorMessages.EVENT_NOT_ENOUGH_PLAYERS)
                EventManager.remove(this@Event)
                ended = true
                return@runTaskLater
            }

            startRound()
        }, 600)
    }

    fun removeMatch(match: Match) {
        matches.remove(match)

        if (ended) return

        if (matches.isEmpty()) {
            val broadcastedMessage: gg.mineral.practice.util.messages.ChatMessage =
                ChatMessages.ROUND_OVER.clone().replace("%round%", "" + round)

            ProfileManager.broadcast(participants, broadcastedMessage)

            Bukkit.getScheduler().runTaskLater(PracticePlugin.INSTANCE, {
                startRound()
                round++
            }, 100)
        }
    }
}
