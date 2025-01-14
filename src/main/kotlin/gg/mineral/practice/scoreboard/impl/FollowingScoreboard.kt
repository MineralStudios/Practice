package gg.mineral.practice.scoreboard.impl

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.scoreboard.Scoreboard
import gg.mineral.practice.scoreboard.ScoreboardHandler
import gg.mineral.practice.util.messages.CC

class FollowingScoreboard

    : Scoreboard {
    override fun updateBoard(board: ScoreboardHandler, profile: Profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral")

        board.updateLines(
            CC.BOARD_SEPARATOR,
            CC.ACCENT + "Following: " + CC.SECONDARY + profile.following!!.name,
            CC.SPACER,
            CC.SECONDARY + "mineral.gg",
            CC.BOARD_SEPARATOR
        )
    }

    companion object {
        val INSTANCE: Scoreboard = FollowingScoreboard()
    }
}
