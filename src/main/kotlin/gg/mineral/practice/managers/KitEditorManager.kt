package gg.mineral.practice.managers

import gg.mineral.practice.util.config.*
import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.world.Schematic
import gg.mineral.practice.util.world.SchematicFile
import gg.mineral.practice.util.world.SpawnLocation
import org.bukkit.Location
import java.io.File

object KitEditorManager {
    val config: FileConfiguration = FileConfiguration("kiteditor.yml", "plugins/Practice")
    var displayName by StringProp(config, "KitEditor.DisplayName", "Kit Editor")
    var displayItem by ItemStackProp(config, "KitEditor.DisplayItem", ItemStacks.DEFAULT_KIT_EDITOR_DISPLAY_ITEM)
    var slot by IntProp(config, "KitEditor.Slot", 0)
    var enabled by BoolProp(config, "KitEditor.Enable", true)
    var location by SpawnLocationProp(config, "KitEditor.Location", SpawnLocation(0, 70, 0))

    private val schematicFile by lazy {
        val worldName = config.getString("KitEditor.Location.World", "KitEditorWorld")
        Schematic.get(worldName) ?: SchematicFile(File("", "$worldName.schematic"))
    }

    private val lobbyWorld by lazy { schematicFile.generateWorld("") }

    val bukkitLocation: Location
        get() = location.bukkit(lobbyWorld)
            ?: throw IllegalStateException("KitEditor world not found")
}
