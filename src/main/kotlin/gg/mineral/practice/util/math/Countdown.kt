package gg.mineral.practice.util.math

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.practice.match.Match
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.Bukkit

class Countdown(private val match: Match) {
    private var time: Int = 0
    private var taskID: Int = 0

    constructor(seconds: Int, match: Match) : this(match) {
        this.time = seconds
    }

    fun start() {
        val scheduler = Bukkit.getServer().scheduler
        for (profile in match.participants) profile.inMatchCountdown = true

        scheduler.scheduleSyncDelayedTask(PracticePlugin.INSTANCE, {
            for (profile in match.participants) match.onCountdownStart(
                profile!!
            )
        }, 2L)
        taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, {
            if (time == 0) {
                cancel()
                return@scheduleSyncRepeatingTask
            }
            broadcast(
                match.participants,
                ChatMessages.BEGINS_IN.clone().replace("%time%", "" + time)
            )
            --time
        }, 0L, 20L)
    }

    private fun cancel() {
        match.onMatchStart()

        for (profile in match.participants) {
            match.onMatchStart(profile)
            profile.inMatchCountdown = false
            profile.message(ChatMessages.MATCH_STARTED)
        }

        Bukkit.getScheduler().cancelTask(taskID)
    }
}
