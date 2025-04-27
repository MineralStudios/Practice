package gg.mineral.practice.arena

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.util.config.*
import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.world.Schematic
import gg.mineral.practice.util.world.SchematicFile
import gg.mineral.practice.util.world.SpawnLocation
import org.bukkit.World
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

open class ArenaImpl(override var name: String, override val id: Byte) : EventArena, BedFightArena {
    val config: FileConfiguration = ArenaManager.config
    protected val path: String = "Arena.$name."
    override var displayName by StringProp(config, path + "DisplayName", name)
    private val schematicFile by lazy { Schematic.get(this.name) ?: SchematicFile(File("", this.name + ".schematic")) }

    override var location1 by SpawnLocationProp(config, path + "Spawn.1", SpawnLocation(0, 70, 0))
    override var location2 by SpawnLocationProp(config, path + "Spawn.2", SpawnLocation(0, 70, 0))
    override var waitingLocation by SpawnLocationProp(config, path + "Spawn.Waiting", SpawnLocation(0, 70, 0))
    override var displayItem by ItemStackProp(config, path + "DisplayItem", ItemStacks.DEFAULT_ARENA_DISPLAY_ITEM)

    override var bedFightArena by BoolProp(config, path + "BedFight", false)
    override var bedLocation1Head by SpawnLocationProp(config, path + "Bed.1.Head", SpawnLocation(0, 70, 0))
    override var bedLocation1Foot by SpawnLocationProp(config, path + "Bed.1.Foot", SpawnLocation(1, 70, 0))
    override var bedLocation2Head by SpawnLocationProp(config, path + "Bed.2.Head", SpawnLocation(0, 70, 0))
    override var bedLocation2Foot by SpawnLocationProp(config, path + "Bed.2.Foot", SpawnLocation(1, 70, 0))
    override var breakableBlockLocations by BlockDataListProp(config, path + "BreakableBlocks", emptyList())

    private var currentNameID = AtomicInteger(0)

    override fun equals(other: Any?): Boolean {
        if (other is ArenaImpl) return other.name.equals(this.name, ignoreCase = true)
        return false
    }

    override fun hashCode() = name.hashCode()

    override fun spectateArena(viewer: Profile) = viewer.spectate(SpectatableArena(this))

    override fun generate(): World = schematicFile.generateWorld("_" + currentNameID.incrementAndGet())

    override fun generateBaseWorld(): World = schematicFile.generateWorld("")

    override fun delete() {
        config.remove("Arena.$name")
        config.save()
    }
}
