package gg.mineral.practice.util.math

import gg.mineral.practice.PracticePlugin
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.bukkit.Bukkit
import java.util.*

class PearlCooldown {
    val cooldowns = Object2IntOpenHashMap<UUID>()

    init {
        Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(
            PracticePlugin.INSTANCE,
            {
                for (entry in cooldowns.object2IntEntrySet()) {
                    val uuid = entry.key
                    var cooldown = entry.intValue

                    if (cooldown > 0) {
                        cooldowns.put(uuid, --cooldown)
                        val player = Bukkit.getPlayer(uuid)
                        player.level = cooldown
                    }
                }
            }, 0L, 20L
        )
    }

    fun isActive(uuid: UUID) = cooldowns.getInt(uuid) > 0

    fun getTimeRemaining(uuid: UUID) = cooldowns.getInt(uuid)
}
