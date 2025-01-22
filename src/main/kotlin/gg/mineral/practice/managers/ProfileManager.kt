package gg.mineral.practice.managers

import gg.mineral.api.config.FileConfiguration
import gg.mineral.practice.entity.ExtendedProfileData
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.entity.appender.PlayerAppender
import gg.mineral.practice.inventory.menus.InventoryStatsMenu
import gg.mineral.practice.util.config.SpawnLocationProp
import gg.mineral.practice.util.messages.Message
import gg.mineral.practice.util.world.Schematic
import gg.mineral.practice.util.world.SpawnLocation
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Predicate

object ProfileManager : PlayerAppender {

    class ProfileMap : Object2ObjectOpenHashMap<UUID, Profile>()

    private val lobbyConfig: FileConfiguration = FileConfiguration("lobby.yml", "plugins/Practice")

    val playerConfig: FileConfiguration = FileConfiguration(
        "PlayerData.yml",
        "plugins/Practice/PlayerData"
    )

    private val schematicFile by lazy {
        val worldName = lobbyConfig.getString("Lobby.World", "PracticeLobby")
        Schematic.get(worldName) ?: throw IllegalStateException("Lobby schematic not found")
    }

    private val lobbyWorld by lazy { schematicFile.generateWorld("") }

    var spawnLocation by SpawnLocationProp(lobbyConfig, "Lobby", SpawnLocation(0, 70, 0))

    val lobbyLocation: Location
        get() = spawnLocation.bukkit(lobbyWorld)

    val profiles = ProfileMap()
    private val inventoryStats = Object2ObjectOpenHashMap<String, List<InventoryStatsMenu>>()

    fun remove(profile: Profile) = profiles.remove(profile.uuid)

    fun getInventoryStats(s: String) = inventoryStats[s]

    fun count(predicate: Predicate<Profile>): Int {
        var count = 0
        for (profile in profiles.values) {
            if (!predicate.test(profile)) continue
            count++
        }

        return count
    }

    private var lastOnline = 0
    private var lastCount = 0

    fun countBots(): Int {
        if (lastOnline == Bukkit.getOnlinePlayers().size) return lastCount
        var count = 0
        lastOnline = Bukkit.getOnlinePlayers().size
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.isFake()) continue

            count++
        }

        return count.also { lastCount = it }
    }

    fun getProfileData(name: String, uuid: UUID? = null): ProfileData {
        if (uuid != null) {
            val profile = profiles[uuid]

            if (profile != null) return profile
        }

        for (p in profiles.values) {
            if (!p.name.equals(name, ignoreCase = true)) continue
            return p
        }

        return uuid?.let { ExtendedProfileData(name, it) } ?: ProfileData(name)
    }

    fun getProfile(uuid: UUID) = profiles[uuid]

    fun getProfile(uuid: UUID, predicate: Predicate<Profile>): Profile? {
        val profile = profiles[uuid]

        if (profile == null || !predicate.test(profile)) return null

        return profile
    }

    fun getProfile(name: String): Profile? {
        for (p in profiles.values) {
            if (!p.name.equals(name, ignoreCase = true)) continue
            return p
        }
        return null
    }

    fun removeIfExists(pl: Player?) {
        if (pl == null) return
        profiles.remove(pl.uniqueId)
    }

    fun getOrCreateProfile(pl: Player): Profile = profiles.computeIfAbsent(
        pl.uniqueId,
        Object2ObjectFunction { Profile(pl) })

    fun setInventoryStats(p: Profile, menu: InventoryStatsMenu) {
        inventoryStats[p.name] = listOf(menu)
    }

    fun setInventoryStats(p: Profile, menus: List<InventoryStatsMenu>) {
        inventoryStats[p.name] = menus
    }

    fun broadcast(c: Collection<Profile>, message: Message) {
        for (p in c) p.message(message)
    }

    fun broadcast(message: Message) = broadcast(profiles.values, message)

    fun getProfile(player: Player) = profiles[player.uniqueId]
}
