package gg.mineral.practice.party

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.PartyManager.registerParty
import gg.mineral.practice.managers.PartyManager.remove
import gg.mineral.practice.queue.QueueSettings
import gg.mineral.practice.queue.QueuedEntity
import gg.mineral.practice.util.messages.impl.ChatMessages
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.util.*


class Party(val partyLeader: Profile) : QueuedEntity {
    var open = false
    val partyMembers = ObjectOpenHashSet<Profile>()

    init {
        registerParty(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Party) return false
        return other.partyLeader == partyLeader
    }

    fun isPartyLeader(profile: Profile) = partyLeader == profile

    fun add(p: Profile) = partyMembers.add(p)

    fun remove(p: Profile) = partyMembers.remove(p)

    fun contains(p: Profile) = partyMembers.contains(p)

    fun leave(profile: Profile) {
        val leftMessage = ChatMessages.LEFT_PARTY.clone().replace("%player%", profile.name)

        val iter: MutableIterator<Profile> = partyMembers.iterator()

        if (partyLeader == profile) {
            while (iter.hasNext()) {
                val member = iter.next()
                iter.remove()
                member.party = null
                member.message(leftMessage)
            }

            remove(this)
        } else {
            profile.party = null

            while (iter.hasNext()) iter.next().message(leftMessage)
        }
    }

    override fun hashCode() = javaClass.hashCode()

    override val profiles: Queue<Profile>
        get() = LinkedList(partyMembers)

    override val uuid: UUID
        get() = partyLeader.uuid

    override val queueSettings: QueueSettings
        get() = partyLeader.queueSettings
}
