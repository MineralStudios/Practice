package gg.mineral.practice.arena

import gg.mineral.practice.traits.Spectatable
import gg.mineral.practice.util.collection.ProfileList

class SpectatableArena(val arena: Arena) : Spectatable {
    override val spectators: ProfileList = ProfileList()
    override val participants = ProfileList()
    override val ended = false
    override val world by lazy { arena.generateBaseWorld() }
}