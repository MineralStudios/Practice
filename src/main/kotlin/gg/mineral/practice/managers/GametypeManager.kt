package gg.mineral.practice.managers

import gg.mineral.api.config.FileConfiguration
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.QueuetypeManager.queuetypes
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap

object GametypeManager {
    val config = FileConfiguration("gametype.yml", "plugins/Practice")
    val gametypes: Byte2ObjectOpenHashMap<Gametype> = Byte2ObjectOpenHashMap()
    var CURRENT_ID: Byte = 0

    fun registerGametype(gametype: Gametype): Gametype? = gametypes.put(gametype.id, gametype)

    fun remove(gametype: Gametype) {
        gametypes.remove(gametype.id)
        gametype.delete()

        for (queuetype in queuetypes.values) queuetype.menuEntries.removeInt(gametype)
    }

    fun getGametypeByName(string: String): Gametype? {
        for (gametype in gametypes.values) if (gametype.name.equals(string, ignoreCase = true)) return gametype
        return null
    }

    fun load() {
        val configSection = config.getConfigurationSection("Gametype.")

        for (key in configSection.getKeys(false)) {
            if (key == null) continue
            registerGametype(Gametype(key, CURRENT_ID++))
        }

        EloManager.setAllEloAndLeaderboards()
    }
}
