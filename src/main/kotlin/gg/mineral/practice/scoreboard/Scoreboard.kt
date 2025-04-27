package gg.mineral.practice.scoreboard

import gg.mineral.practice.entity.Profile

interface Scoreboard {
    fun updateBoard(board: ScoreboardHandler, profile: Profile)
}
