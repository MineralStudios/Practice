package gg.mineral.practice.scoreboard.impl

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.getPing
import gg.mineral.practice.match.data.MatchStatisticCollector
import gg.mineral.practice.scoreboard.Scoreboard
import gg.mineral.practice.scoreboard.ScoreboardHandler
import gg.mineral.practice.util.messages.CC

class BoxingScoreboard : Scoreboard {
    override fun updateBoard(board: ScoreboardHandler, profile: Profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral")

        val match = profile.match

        match!!.stat(profile.uuid) {
            val opponent = match.getOpponent(profile) ?: return@stat
            val opponentHitCount = match.computeStat(opponent.uuid, MatchStatisticCollector::hitCount)
            val hitDifference = (it.hitCount
                    - opponentHitCount)
            var symbol = "+"
            var color = CC.D_GREEN

            if (hitDifference < 0) {
                symbol = ""
                color = CC.D_RED
            }
            board.updateLines(
                CC.BOARD_SEPARATOR,
                (CC.ACCENT + "Opponent: " + CC.SECONDARY
                        + match.getOpponent(profile)?.name),
                (CC.ACCENT + "Your Hits: " + CC.SECONDARY
                        + it.hitCount + color
                        + " (" + symbol + hitDifference
                        + ")"),
                (CC.ACCENT + "Their Hits: " + CC.SECONDARY
                        + opponentHitCount),
                (CC.ACCENT + "Your Ping: " + CC.SECONDARY
                        + board.player.getPing()),
                (CC.ACCENT + "Their Ping: " + CC.SECONDARY
                        + profile.match!!.getOpponent(profile)?.player
                    ?.getPing()),
                CC.SPACER,
                CC.SECONDARY + "mineral.gg",
                CC.BOARD_SEPARATOR
            )
        }
    }

    companion object {
        val INSTANCE: Scoreboard = BoxingScoreboard()
    }
}
