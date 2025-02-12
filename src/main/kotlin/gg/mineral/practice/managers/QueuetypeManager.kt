package gg.mineral.practice.managers

import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.config.yaml.FileConfiguration
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import org.bukkit.configuration.ConfigurationSection

object QueuetypeManager {
    val config: FileConfiguration = FileConfiguration("queues.yml", "plugins/Practice")
    var queuetypes: Byte2ObjectOpenHashMap<Queuetype?> = Byte2ObjectOpenHashMap()
    var CURRENT_ID: Byte = 0

    fun registerQueuetype(queuetype: Queuetype): Queuetype? = queuetypes.put(queuetype.id, queuetype)

    fun remove(queuetype: Queuetype): Queuetype? {
        queuetype.delete()
        return queuetypes.remove(queuetype.id)
    }

    fun getQueuetypeByName(string: String): Queuetype? {
        for (queuetype in queuetypes.values) if (queuetype?.name.equals(string, ignoreCase = true)) return queuetype
        return null
    }

    fun load() {
        val configSection: ConfigurationSection = config.getConfigurationSection("Queue.") ?: return

        for (key in configSection.getKeys(false)) {
            if (key == null) continue
            registerQueuetype(Queuetype(key, CURRENT_ID++))
        }
    }
}
