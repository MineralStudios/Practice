package gg.mineral.practice.queue

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.arena.Arena
import gg.mineral.practice.category.Category
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.*
import gg.mineral.practice.managers.ArenaManager.getArenaByName
import gg.mineral.practice.util.config.BoolProp
import gg.mineral.practice.util.config.IntProp
import gg.mineral.practice.util.config.ItemStackProp
import gg.mineral.practice.util.config.StringProp
import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import gg.mineral.server.combat.KnockbackProfile
import gg.mineral.server.combat.KnockbackProfileList
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
import it.unimi.dsi.fastutil.bytes.ByteSet
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.ObjectSortedSet
import java.util.*
import java.util.concurrent.CompletableFuture

class Queuetype(val name: String, val id: Byte) {
    val config: FileConfiguration = QueuetypeManager.config
    val path: String = "Queue.$name."
    var displayItem by ItemStackProp(config, "$path.DisplayItem", ItemStacks.DEFAULT_QUEUETYPE_DISPLAY_ITEM)
    var displayName by StringProp(config, "$path.DisplayName", name)
    var slotNumber by IntProp(config, "$path.Slot", 8)
    var ranked by BoolProp(config, "$path.Elo", false)
    var community by BoolProp(config, "$path.Community", false)
    var unranked by BoolProp(config, "$path.Unranked", false)
    var botsEnabled by BoolProp(config, "$path.Bots", false)
    private var knockbackName by StringProp(config, "$path.Knockback", "")
    var knockback: KnockbackProfile?
        get() = KnockbackProfileList.getKnockbackProfileByName(knockbackName)
        set(value) {
            knockbackName = value?.name ?: ""
            config["$path.Knockback"] = knockbackName
        }


    val menuEntries: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> by lazy {
        val map = object : Object2IntLinkedOpenHashMap<QueuetypeMenuEntry>() {
            override fun put(key: QueuetypeMenuEntry, value: Int): Int {
                val slot = super.put(key, value)
                sortByValues()
                return slot
            }

            private fun sortByValues() {
                val sorted: Object2IntMap<QueuetypeMenuEntry> = object2IntEntrySet().stream()
                    .sorted(Comparator.comparingInt { obj: Object2IntMap.Entry<QueuetypeMenuEntry?> -> obj.intValue })
                    .collect(
                        { Object2IntLinkedOpenHashMap() },
                        { map: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry>, entry: Object2IntMap.Entry<QueuetypeMenuEntry> ->
                            map.put(
                                entry.key,
                                entry.intValue
                            )
                        },
                        { obj: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry>, m: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> ->
                            obj.putAll(m)
                        })

                clear()

                for (entry in sorted.object2IntEntrySet()) super.put(entry.key, entry.intValue)
            }

            @Deprecated(
                "Deprecated in Java",
                ReplaceWith("super.getInt(key)", "it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap")
            )
            override fun get(key: QueuetypeMenuEntry) = super.getInt(key)

            @Deprecated(
                "Deprecated in Java",
                ReplaceWith("super.removeInt(key)", "it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap")
            )
            override fun remove(key: QueuetypeMenuEntry) = super.removeInt(key)
        }

        // TODO: move to queuetype.yml
        val gametypeConfig = GametypeManager.config

        for (gametype in GametypeManager.gametypes.values) {
            if (!gametypeConfig.getBoolean("Gametype.${gametype.name}.$name.Enabled", false)) continue
            map[gametype] = gametypeConfig.getInt("Gametype.${gametype.name}.$name.Slot", 0)
        }

        // TODO: move to queuetype.yml
        val categoryConfig = CategoryManager.config

        for (category in CategoryManager.categories.values) {
            if (!categoryConfig.getBoolean("Category.${category.name}.$name.Enabled", false)) continue
            map[category] = categoryConfig.getInt("Category.${category.name}.$name.Slot", 0)
        }

        map
    }


    val arenas by lazy {
        val set = ByteOpenHashSet()
        for (a in ArenaManager.arenas.values) if (config.getBoolean(path + "Arenas." + a.name, false)) set.add(a.id)
        set
    }

    fun randomGametype(): Gametype {
        val list = GlueList<Gametype>()

        for (menuEntry in menuEntries.keys) if (menuEntry is Gametype) list.add(menuEntry)

        val rand = Random()
        return list[rand.nextInt(list.size)]
    }

    fun randomGametypeWithBotsEnabled(): Gametype {
        val list = GlueList<Gametype>()

        for (menuEntry in menuEntries.keys) if (menuEntry is Gametype && menuEntry.botsEnabled) list.add(menuEntry)

        val rand = Random()
        return list[rand.nextInt(list.size)]
    }

    // Helper method to filter available arenas based on a Gametype
    fun filterArenasByGametype(g: Gametype): ByteSet {
        val filteredArenas = ByteOpenHashSet(this.arenas)
        filteredArenas
            .retainAll(g.arenas)
        return filteredArenas
    }

    fun getGlobalElo(profile: ProfileData): CompletableFuture<Int> {
        val set: ObjectSortedSet<QueuetypeMenuEntry> = menuEntries.keys

        val futures = mutableListOf<CompletableFuture<Int>>()

        for (menuEntry in set) if (menuEntry is Gametype) {
            futures.add(menuEntry.getElo(profile))
        }

        if (futures.isEmpty()) return CompletableFuture.completedFuture(1000)

        return CompletableFuture.allOf(*futures.toTypedArray()).thenApply {
            var sum = 0
            var divisor = 0

            for (future in futures) {
                sum += future.join()
                divisor++
            }
            if (divisor == 0) return@thenApply 1000
            sum / divisor
        }
    }

    val globalLeaderboardLore: List<String>
        get() {
            val lore = GlueList<String>()

            val leaderboardMap = EloManager.getGlobalEloLeaderboard(this)

            lore.add(CC.WHITE + "The " + CC.SECONDARY + "global" + CC.WHITE + " elo leaderboard.")
            lore.add(" ")

            for (entry in leaderboardMap.entries) lore.add(CC.SECONDARY + entry.key + ": " + CC.WHITE + entry.value)

            if (lore.size <= 2) lore.add(CC.ACCENT + "No Data")

            return lore
        }

    fun enableArena(arena: Arena, enabled: Boolean) {
        if (enabled) arenas.add(arena.id)
        else arenas.remove(arena.id)
        config[path + "Arenas." + arena.name] = arenas.contains(arena.id) && getArenaByName(arena.name) != null
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Queuetype) return false
        return other.name.equals(name, ignoreCase = true)
    }

    fun addMenuEntry(menuEntry: QueuetypeMenuEntry, slot: Int) {
        menuEntries.put(menuEntry, slot)

        //TODO: move to queuetype.yml
        val gametypeConfig = GametypeManager.config

        if (menuEntry is Gametype) {
            gametypeConfig["Gametype.${menuEntry.name}.$name.Enabled"] = true
            gametypeConfig["Gametype.${menuEntry.name}.$name.Slot"] = slot
        }

        //TODO: move to queuetype.yml
        val categoryConfig = QueuetypeManager.config

        if (menuEntry is Category) {
            categoryConfig["Category.${menuEntry.name}.$name.Enabled"] = true
            categoryConfig["Category.${menuEntry.name}.$name.Slot"] = slot
        }
    }

    fun removeMenuEntry(menuEntry: QueuetypeMenuEntry) {
        menuEntries.removeInt(menuEntry)
        //TODO: move to queuetype.yml
        val gametypeConfig = GametypeManager.config

        if (menuEntry is Gametype)
            gametypeConfig["Gametype.${menuEntry.name}.$name.Enabled"] = false

        //TODO: move to queuetype.yml
        val categoryConfig = QueuetypeManager.config

        if (menuEntry is Category)
            categoryConfig["Category.${menuEntry.name}.$name.Enabled"] = false
    }

    fun delete() {
        config.remove("Queue.$name")
        config.save()
    }

    override fun hashCode() = javaClass.hashCode()
}
