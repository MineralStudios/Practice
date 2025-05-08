package gg.mineral.practice.scoreboard.impl

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.getPing
import gg.mineral.practice.scoreboard.Scoreboard
import gg.mineral.practice.scoreboard.ScoreboardHandler
import gg.mineral.practice.util.messages.CC

class InMatchScoreboard : Scoreboard {
    override fun updateBoard(board: ScoreboardHandler, profile: Profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral")
        board.updateLines(
            CC.BOARD_SEPARATOR,
            CC.ACCENT + "Opponent: " + CC.SECONDARY + profile.match!!.getOpponent(profile)?.name,
            CC.ACCENT + "Your Ping: " + CC.SECONDARY + board.player.getPing(),
            (CC.ACCENT + "Their Ping: " + CC.SECONDARY
                    + profile.match?.getOpponent(profile)?.player?.getPing()),
            CC.SPACER,
            CC.SECONDARY + "mineral.gg",
            CC.BOARD_SEPARATOR
        )
    }

    companion object {
        val INSTANCE: Scoreboard = InMatchScoreboard()
    }
}
