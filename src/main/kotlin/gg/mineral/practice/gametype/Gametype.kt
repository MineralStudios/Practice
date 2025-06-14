package gg.mineral.practice.gametype

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.arena.Arena
import gg.mineral.practice.entity.ExtendedProfileData
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.kit.Kit
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.managers.ArenaManager.getArenaByName
import gg.mineral.practice.managers.EloManager
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.queue.QueuetypeMenuEntry
import gg.mineral.practice.util.collection.LeaderboardMap
import gg.mineral.practice.util.config.*
import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.*
import java.util.concurrent.CompletableFuture

class Gametype(val name: String, val id: Byte) : QueuetypeMenuEntry {
    val config: FileConfiguration = GametypeManager.config
    private val path: String = "Gametype.$name."

    var regeneration by BoolProp(config, path + "Regen", true)
    var deadlyWater by BoolProp(config, path + "DeadlyWater", false)
    var griefing by BoolProp(config, path + "Griefing", false)
    var build by BoolProp(config, path + "Build", false)

    // TODO: Looting
    var looting by BoolProp(config, path + "Looting", false)
    var damage by BoolProp(config, path + "Damage", true)
    var hunger by BoolProp(config, path + "Hunger", true)
    var boxing by BoolProp(config, path + "Boxing", false)
    var event by BoolProp(config, path + "Event", false)
    override var botsEnabled by BoolProp(config, path + "Bots", false)
    var preventSwordDropping by BoolProp(config, path + "PreventSwordDropping", true)

    // Keep private
    var inCategory by BoolProp(config, path + "InCategory", false)
    override var displayItem by ItemStackProp(config, path + "DisplayItem", ItemStacks.DEFAULT_GAMETYPE_DISPLAY_ITEM)
    override var displayName by StringProp(config, path + "DisplayName", name)
    var pearlCooldown by IntProp(config, path + "PearlCooldown", 10)
    var noDamageTicks by IntProp(config, path + "NoDamageTicks", 20)
    var buildLimit by IntProp(config, path + "BuildLimit", 16)
    var categoryName by StringProp(config, path + "Category", "")

    private var eventArenaName by StringProp(config, path + "EventArena", "")

    var eventArenaId: Byte
        get() = getArenaByName(eventArenaName)?.id ?: -1
        set(value) {
            val arena = ArenaManager.arenas[value]
            arena?.let {
                config[path + "EventArena"] = it.name
                eventArenaName = it.name
            }
        }

    val arenas by lazy {
        val set = ByteOpenHashSet()
        ArenaManager.arenas.values.filterNotNull().filter { config.getBoolean(path + "Arenas." + it.name, false) }
            .forEach { set.add(it.id) }
        set
    }

    var kit by KitProp(
        config,
        path + "Kit",
        Kit(this.name, arrayOfNulls(0), arrayOfNulls(0)),
        name
    )
    val leaderboardMap = LeaderboardMap()
    val eloCache: Object2IntOpenHashMap<ProfileData> = Object2IntOpenHashMap()

    init {
        eloCache.defaultReturnValue(1000)
    }

    fun setEventArena(arena: Arena) {
        this.eventArenaId = arena.id
        config[path + "EventArena"] = arena.name
    }

    fun getElo(profile: ProfileData): CompletableFuture<Int> {

        if (profile !is ExtendedProfileData) return if (!eloCache.containsKey(profile))
            EloManager.get(this, profile.name).thenApply {
                synchronized(eloCache) { eloCache.put(profile, it) }
                it
            }
        else
            CompletableFuture.completedFuture(eloCache.getInt(profile))

        return if (!eloCache.containsKey(profile))
            EloManager.get(this, profile.uuid).thenApply {
                synchronized(eloCache) { eloCache.put(profile, it) }
                it
            }
        else
            CompletableFuture.completedFuture(eloCache.getInt(profile))
    }

    fun getEloMap(vararg profiles: ExtendedProfileData): CompletableFuture<Object2IntOpenHashMap<UUID>> {
        return CompletableFuture.supplyAsync {
            val map = Object2IntOpenHashMap<UUID>()
            val futures by lazy { Object2ObjectOpenHashMap<UUID, CompletableFuture<Int>>() }

            for (profile in profiles) {
                val elo = eloCache.getInt(profile)

                synchronized(eloCache) {
                    if (eloCache.containsKey(profile)) map.put(profile.uuid, elo)
                }

                futures[profile.uuid] = EloManager.get(this, profile.uuid)
                    .thenApply {
                        synchronized(eloCache) {
                            eloCache.put(profile, it)
                        }
                        it
                    }
            }

            for ((key, value) in futures) map.put(
                key, value.join() as Int
            )
            map
        }
    }

    fun setElo(elo: Int, profile: ExtendedProfileData) {
        eloCache.put(profile, elo)
        EloManager.update(profile, name, elo)
    }

    fun enableArena(arena: Arena, enabled: Boolean) {
        if (enabled) arenas.add(arena.id)
        else arenas.remove(arena.id)
        config[path + "Arenas." + arena.name] = arenas.contains(arena.id) && getArenaByName(arena.name) != null
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Gametype) return false
        return other.name.equals(this.name, ignoreCase = true)
    }

    fun updatePlayerLeaderboard(p: Profile, elo: Int, oldElo: Int) {
        if (oldElo == 1000) leaderboardMap.putOrReplace(p.name, elo, oldElo)
        else leaderboardMap.replace(elo, oldElo)
    }

    val leaderboardLore: List<String>
        get() {
            val lore = GlueList<String>()

            for (entry in leaderboardMap.entries) lore.add(CC.SECONDARY + entry.key + ": " + CC.WHITE + entry.value)

            if (lore.isEmpty()) lore.add(CC.ACCENT + "No Data")

            return lore
        }

    fun delete() {
        config.remove("Gametype.$name")
        config.save()
    }

    override fun hashCode() = javaClass.hashCode()
}
