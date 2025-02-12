package gg.mineral.practice.scoreboard.impl

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.match.Match
import gg.mineral.practice.match.TeamMatch
import gg.mineral.practice.match.data.MatchStatisticCollector
import gg.mineral.practice.scoreboard.Scoreboard
import gg.mineral.practice.scoreboard.ScoreboardHandler
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.messages.CC

class SpectatorScoreboard

    : Scoreboard {
    override fun updateBoard(board: ScoreboardHandler, profile: Profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral")

        val spectatable = profile.spectatable

        if (spectatable is TeamMatch) {
            val team = spectatable.getTeam(spectatable.profile1!!, true)
            val opponents = ProfileList(spectatable.participants)
            opponents.removeAll(team)

            if (spectatable.data.boxing) {
                val hitCount = spectatable.team1HitCount
                val opponentHitCount = spectatable.team2HitCount
                val requiredHitCount = spectatable.team1RequiredHitCount
                val opponentRequiredHitCount = spectatable.team2RequiredHitCount

                board.updateLines(
                    CC.BOARD_SEPARATOR,
                    CC.SECONDARY + spectatable.profile1!!.name,
                    (CC.YELLOW + " * " + CC.ACCENT + "Hits: "
                            + CC.WHITE + hitCount + "/" + requiredHitCount),
                    CC.SECONDARY
                            + spectatable.profile2!!.name,
                    (CC.YELLOW + " * " + CC.ACCENT + "Hits: " + CC.WHITE
                            + opponentHitCount + "/" + opponentRequiredHitCount),
                    CC.SPACER,
                    CC.SECONDARY + "mineral.gg",
                    CC.BOARD_SEPARATOR
                )
                return
            }

            board.updateLines(
                CC.BOARD_SEPARATOR,
                CC.SECONDARY + spectatable.profile1!!.name,
                (CC.YELLOW + " * " + CC.ACCENT + "Team Remaining: "
                        + CC.WHITE + team.size),
                CC.SECONDARY
                        + spectatable.profile2!!.name,
                (CC.YELLOW + " * " + CC.ACCENT + "Team Remaining: " + CC.WHITE
                        + opponents.size),
                CC.SPACER,
                CC.SECONDARY + "mineral.gg",
                CC.BOARD_SEPARATOR
            )
        } else if (spectatable is Match) {
            if (spectatable.data.boxing) {
                val profile1 = spectatable.profile1
                val profile2 = spectatable.profile2

                val profile1HitCount = spectatable.computeStat(
                    profile1!!.uuid,
                    MatchStatisticCollector::hitCount
                )
                val profile2HitCount = spectatable.computeStat(
                    profile2!!.uuid,
                    MatchStatisticCollector::hitCount
                )

                board.updateLines(
                    CC.BOARD_SEPARATOR,
                    CC.SECONDARY + profile1.name,
                    (CC.YELLOW + " * " + CC.ACCENT + "Ping: "
                            + CC.WHITE
                            + profile1.player.handle.ping),
                    (CC.YELLOW + " * " + CC.ACCENT + "Hits: "
                            + CC.WHITE
                            + profile1HitCount),
                    CC.SECONDARY
                            + profile2.name,
                    (CC.YELLOW + " * " + CC.ACCENT + "Ping: " + CC.WHITE
                            + profile2.player.handle.ping),
                    (CC.YELLOW + " * " + CC.ACCENT + "Hits: "
                            + CC.WHITE
                            + profile2HitCount),
                    CC.SPACER,
                    CC.SECONDARY + "mineral.gg",
                    CC.BOARD_SEPARATOR
                )
                return
            }

            board.updateLines(
                CC.BOARD_SEPARATOR,
                CC.SECONDARY + spectatable.profile1!!.name,
                (CC.YELLOW + " * " + CC.ACCENT + "Ping: "
                        + CC.WHITE + spectatable.profile1!!.player.handle.ping),
                CC.SECONDARY
                        + spectatable.profile2!!.name,
                (CC.YELLOW + " * " + CC.ACCENT + "Ping: " + CC.WHITE
                        + spectatable.profile2!!.player.handle.ping),
                CC.SPACER,
                CC.SECONDARY + "mineral.gg",
                CC.BOARD_SEPARATOR
            )
        }
    }

    companion object {
        val INSTANCE: Scoreboard = SpectatorScoreboard()
    }
}
