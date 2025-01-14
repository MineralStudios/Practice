package gg.mineral.practice.managers

import gg.mineral.api.config.FileConfiguration
import gg.mineral.practice.queue.Queuetype
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import org.bukkit.configuration.ConfigurationSection

object QueuetypeManager {
    val config: FileConfiguration = FileConfiguration("queues.yml", "plugins/Practice")
    var queuetypes: Byte2ObjectOpenHashMap<Queuetype> = Byte2ObjectOpenHashMap()
    var CURRENT_ID: Byte = 0

    fun registerQueuetype(queuetype: Queuetype) {
        queuetypes.put(queuetype.id, queuetype)
    }

    fun remove(queuetype: Queuetype) {
        queuetypes.remove(queuetype.id)
        queuetype.delete()
    }

    fun getQueuetypeByName(string: String): Queuetype? {
        for (queuetype in queuetypes.values) if (queuetype.name.equals(string, ignoreCase = true)) return queuetype
        return null
    }

    fun load() {
        val configSection: ConfigurationSection = config.getConfigurationSection("Queue.")

        for (key in configSection.getKeys(false)) {
            if (key == null) continue
            registerQueuetype(Queuetype(key, CURRENT_ID++))
        }
    }
}
