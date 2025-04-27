package gg.mineral.practice.managers

import gg.mineral.practice.arena.Arena
import gg.mineral.practice.arena.ArenaImpl
import gg.mineral.practice.util.config.yaml.FileConfiguration
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import org.bukkit.configuration.ConfigurationSection

object ArenaManager {
    val config = FileConfiguration("arenas.yml", "plugins/Practice")
    val arenas: Byte2ObjectOpenHashMap<Arena?> = Byte2ObjectOpenHashMap()
    var CURRENT_ID: Byte = 0

    fun registerArena(arena: Arena): Arena? = arenas.put(arena.id, arena)

    fun remove(arena: Arena): Arena? {
        arena.delete()

        for (gametype in GametypeManager.gametypes.values) gametype?.arenas?.remove(arena.id)
        for (queuetype in QueuetypeManager.queuetypes.values) queuetype?.arenas?.remove(arena.id)
        return arenas.remove(arena.id)
    }

    fun getArenaByName(string: String): Arena? {
        for (arena in arenas.values) if (arena?.name.equals(string, ignoreCase = true)) return arena
        return null
    }

    fun load() {
        val configSection: ConfigurationSection = config.getConfigurationSection("Arena.") ?: return

        for (key in configSection.getKeys(false)) {
            if (key == null) continue
            registerArena(ArenaImpl(key, CURRENT_ID++))
        }
    }
}
