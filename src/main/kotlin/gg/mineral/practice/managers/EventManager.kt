package gg.mineral.practice.managers

import gg.mineral.practice.events.Event
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object EventManager {
    val events: MutableMap<String, Event> = Object2ObjectOpenHashMap()

    fun registerEvent(event: Event) {
        events[event.host] = event
    }

    fun remove(event: Event) = events.remove(event.host)

    fun getEventByName(name: String) = events[name]
}
