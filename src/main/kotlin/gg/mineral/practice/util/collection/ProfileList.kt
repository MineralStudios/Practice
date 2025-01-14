package gg.mineral.practice.util.collection

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.entity.Profile
import java.io.Serial
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class ProfileList : ConcurrentLinkedQueue<Profile> {
    constructor() : super()

    constructor(c: Collection<Profile>) : super(c)

    override fun add(element: Profile): Boolean {
        if (contains(element)) return false
        return super.add(element)
    }

    override fun contains(element: Profile): Boolean {
        for (profile in this) if (element.uuid == profile.uuid) return true
        return false
    }

    fun subList(fromIndex: Int, toIndex: Int): List<Profile> {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) throw IndexOutOfBoundsException()

        val subList: MutableList<Profile> = GlueList()
        val iterator: Iterator<Profile> = iterator()

        for (i in 0..<fromIndex) {
            if (iterator.hasNext()) {
                iterator.next()
            } else {
                throw IndexOutOfBoundsException()
            }
        }

        for (i in fromIndex..<toIndex) {
            if (iterator.hasNext()) {
                subList.add(iterator.next())
            } else {
                throw IndexOutOfBoundsException()
            }
        }

        return subList
    }

    fun removeFirst(): Profile? = poll()

    val first: Profile?
        get() = peek()

    fun get(uuid: UUID): Profile? {
        // TODO: Optimize this using hashmaps
        for (profile in this) if (profile.uuid == uuid) return profile

        return null
    }

    companion object {
        /**
         *
         */
        @Serial
        private const val serialVersionUID = 1L
    }
}
