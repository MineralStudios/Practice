package gg.mineral.practice.match.appender

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.collection.ProfileList
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap
import java.util.function.Consumer

interface MatchAppender {
    fun Object2BooleanLinkedOpenHashMap<Profile>.alive(): ProfileList {
        val list = ProfileList()
        for (e in object2BooleanEntrySet()) if (e.booleanValue) list.add(
            e.key!!
        )
        return list
    }

    fun Object2BooleanLinkedOpenHashMap<Profile>.alive(consumer: Consumer<Profile>) {
        for (e in object2BooleanEntrySet()) if (e.booleanValue) consumer.accept(e.key)
    }

    fun Object2BooleanLinkedOpenHashMap<Profile>.all() = keys

    fun Object2BooleanLinkedOpenHashMap<Profile>.add(profile: Profile) = put(profile, true)

    fun Object2BooleanLinkedOpenHashMap<Profile>.reportDeath(profile: Profile) = put(profile, false)
}
