package gg.mineral.practice.util.collection

import gg.mineral.practice.PracticePlugin
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectIterator
import org.bukkit.Bukkit

class AutoExpireList<K> : Iterable<K> {
    private val map = Object2LongOpenHashMap<K>()

    init {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            PracticePlugin.INSTANCE,
            { this.removeExpired() }, 20, 20
        )
    }

    fun add(e: K) {
        map.put(e, System.currentTimeMillis())
    }

    private fun removeExpired() {
        map.object2LongEntrySet()
            .removeIf { System.currentTimeMillis() - it.longValue >= 30000 }
    }

    fun clear() {
        map.clear()
    }

    fun entryIterator(): ObjectIterator<Object2LongMap.Entry<K>> {
        return map.object2LongEntrySet().iterator()
    }

    override fun iterator(): MutableIterator<K> {
        return map.keys.iterator()
    }

    fun containsKey(key: K): Boolean {
        return map.containsKey(key)
    }
}
