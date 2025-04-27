package gg.mineral.practice.util.math

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.battle.Battle
import gg.mineral.practice.contest.Contest
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.practice.match.Match
import gg.mineral.practice.tournaments.Tournament
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.Bukkit

class Countdown(private var time: Int = 0, private val battle: Battle, private val function: (Int) -> Unit = {}) {
    private var taskID: Int = 0

    fun start() {
        val scheduler = Bukkit.getServer().scheduler
        for (profile in battle.participants) profile.inCountdown = true

        scheduler.scheduleSyncDelayedTask(PracticePlugin.INSTANCE, {
            for (profile in battle.participants) profile.let {
                battle.onCountdownStart(
                    it
                )
            }
        }, 2L)

        taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, {
            if (time == 0 || battle.ended || (battle is Contest && battle.forceStart)) return@scheduleSyncRepeatingTask cancel()

            function(time)

            if (time <= 5 || time < 60 && time % 10 == 0 || time >= 60 && time % 60 == 0)
                broadcast(
                    battle.participants,
                    if (time < 60)
                        ChatMessages.BEGINS_IN_SECONDS.clone().replace("%time%", "" + time).replace(
                            "%type%", when (battle) {
                                is Match -> "match"
                                is Tournament -> "tournament"
                                else -> "event"
                            }
                        ) else
                        ChatMessages.BEGINS_IN_MINUTES.clone().replace("%time%", "" + time / 60).replace(
                            "%type%", when (battle) {
                                is Match -> "match"
                                is Tournament -> "tournament"
                                else -> "event"
                            }
                        )
                )
            --time
        }, 0L, 20L)
    }

    private fun cancel() {
        for (profile in battle.participants.filter { it.inCountdown }) {
            profile.let {
                it.inCountdown = false
                it.message(
                    ChatMessages.BATTLE_STARTED.clone().replace(
                        "%type%", when (battle) {
                            is Match -> "match"
                            is Tournament -> "tournament"
                            else -> "event"
                        }
                    )
                )

                battle.onStart(it)
            }
        }

        battle.onStart()

        Bukkit.getScheduler().cancelTask(taskID)
    }
}
