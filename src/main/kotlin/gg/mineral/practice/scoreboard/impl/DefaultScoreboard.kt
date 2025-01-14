package gg.mineral.practice.scoreboard.impl

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.count
import gg.mineral.practice.managers.ProfileManager.countBots
import gg.mineral.practice.managers.ProfileManager.profiles
import gg.mineral.practice.scoreboard.Scoreboard
import gg.mineral.practice.scoreboard.ScoreboardHandler
import gg.mineral.practice.util.messages.CC

class DefaultScoreboard : Scoreboard {
    override fun updateBoard(board: ScoreboardHandler, profile: Profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral")
        board.updateLines(
            CC.BOARD_SEPARATOR,
            (CC.ACCENT + "Online: " + CC.SECONDARY
                    + profiles.size),
            (CC.ACCENT + "Bots: " + CC.SECONDARY
                    + countBots()),
            (CC.ACCENT + "In Game: " + CC.SECONDARY
                    + count { it.playerStatus === PlayerStatus.FIGHTING }),
            CC.SPACER,
            CC.SECONDARY + "mineral.gg",
            CC.BOARD_SEPARATOR
        )
    }

    companion object {
        val INSTANCE: Scoreboard = DefaultScoreboard()
    }
}
