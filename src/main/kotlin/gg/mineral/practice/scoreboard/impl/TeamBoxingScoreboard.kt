package gg.mineral.practice.scoreboard.impl

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.match.TeamMatch
import gg.mineral.practice.match.appender.MatchAppender
import gg.mineral.practice.scoreboard.Scoreboard
import gg.mineral.practice.scoreboard.ScoreboardHandler
import gg.mineral.practice.util.messages.CC

class TeamBoxingScoreboard : Scoreboard, MatchAppender {
    override fun updateBoard(board: ScoreboardHandler, profile: Profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral")

        profile.match?.let {
            if (it is TeamMatch) {
                val isTeam1: Boolean = it.team1Players.all().contains(profile)
                val hitCount: Int = if (isTeam1) it.team1HitCount else it.team2HitCount
                val opponentHitCount: Int = if (isTeam1) it.team2HitCount else it.team1HitCount
                val requiredHitCount: Int = if (isTeam1) it.team1RequiredHitCount else it.team2RequiredHitCount
                val opponentRequiredHitCount: Int = if (isTeam1)
                    it.team2RequiredHitCount
                else
                    it.team1RequiredHitCount

                board.updateLines(
                    CC.BOARD_SEPARATOR,
                    CC.ACCENT + "Your Hits: " + CC.SECONDARY + hitCount + "/" + requiredHitCount,
                    CC.ACCENT + "Their Hits: " + CC.SECONDARY + opponentHitCount + "/" + opponentRequiredHitCount,
                    CC.SPACER,
                    CC.SECONDARY + "mineral.gg",
                    CC.BOARD_SEPARATOR
                )
            }
        }
    }

    companion object {
        val INSTANCE: Scoreboard = TeamBoxingScoreboard()
    }
}
