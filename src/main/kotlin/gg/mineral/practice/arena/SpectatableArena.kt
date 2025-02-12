package gg.mineral.practice.arena

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.traits.Spectatable
import gg.mineral.practice.util.collection.ProfileList
import java.util.concurrent.ConcurrentLinkedDeque

class SpectatableArena(val arena: Arena) : Spectatable {
    override val spectators: ConcurrentLinkedDeque<Profile> = ConcurrentLinkedDeque()
    override val participants = ProfileList()
    override val ended = false
    override val world by lazy { arena.generateBaseWorld() }
}