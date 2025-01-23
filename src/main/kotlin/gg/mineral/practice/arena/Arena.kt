package gg.mineral.practice.arena

import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.util.config.ItemStackProp
import gg.mineral.practice.util.config.SpawnLocationProp
import gg.mineral.practice.util.config.StringProp
import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.world.Schematic
import gg.mineral.practice.util.world.SchematicFile
import gg.mineral.practice.util.world.SpawnLocation
import org.bukkit.World
import java.io.File

class Arena(var name: String, val id: Byte) {
    val config: FileConfiguration = ArenaManager.config
    private val path: String = "Arena.$name."
    var displayName by StringProp(config, path + "DisplayName", name)
    private val schematicFile by lazy { Schematic.get(this.name) ?: SchematicFile(File("", this.name + ".schematic")) }

    var location1 by SpawnLocationProp(config, path + "Spawn.1", SpawnLocation(0, 70, 0))
    var location2 by SpawnLocationProp(config, path + "Spawn.2", SpawnLocation(0, 70, 0))
    var waitingLocation by SpawnLocationProp(config, path + "Spawn.Waiting", SpawnLocation(0, 70, 0))
    var displayItem by ItemStackProp(config, path + "DisplayItem", ItemStacks.DEFAULT_ARENA_DISPLAY_ITEM)
    private var currentNameID: Int = 0

    override fun equals(other: Any?): Boolean {
        if (other is Arena) return other.name.equals(this.name, ignoreCase = true)
        return false
    }

    override fun hashCode() = name.hashCode()

    fun generate(): World = schematicFile.generateWorld("_" + currentNameID++)

    fun delete() {
        config.remove("Arena.$name")
        config.save()
    }
}
