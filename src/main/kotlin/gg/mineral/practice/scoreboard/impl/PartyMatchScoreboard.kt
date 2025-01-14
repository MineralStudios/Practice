package gg.mineral.practice.scoreboard.impl

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.scoreboard.Scoreboard
import gg.mineral.practice.scoreboard.ScoreboardHandler
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.messages.CC

class PartyMatchScoreboard : Scoreboard {
    override fun updateBoard(board: ScoreboardHandler, profile: Profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral")
        val team = profile.match!!.getTeam(profile, true)
        val opponents = ProfileList(profile.match!!.participants)
        opponents.removeAll(team)

        board.updateLines(
            CC.BOARD_SEPARATOR, CC.ACCENT + "Your Team Remaining: " + CC.SECONDARY + team.size,
            CC.ACCENT + "Their Team Remaining: " + CC.SECONDARY + opponents.size, CC.SPACER,
            CC.SECONDARY + "mineral.gg", CC.BOARD_SEPARATOR
        )
    }

    companion object {
        val INSTANCE: Scoreboard = PartyMatchScoreboard()
    }
}
