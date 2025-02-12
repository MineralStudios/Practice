package gg.mineral.practice.request

import gg.mineral.practice.duel.DuelSettings
import gg.mineral.practice.entity.Profile

data class DuelRequest(val sender: Profile, val duelSettings: DuelSettings)
