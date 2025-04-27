package gg.mineral.practice.arena

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.world.SpawnLocation
import org.bukkit.World
import org.bukkit.inventory.ItemStack

interface Arena {
    /**
     * The id of the arena.
     */
    val id: Byte

    /**
     * The name of the arena.
     */
    val name: String

    /**
     * The display name of the arena.
     */
    var displayName: String

    /**
     * The first spawn location of the arena.
     */
    var location1: SpawnLocation

    /**
     * The second spawn location of the arena.
     */
    var location2: SpawnLocation

    /**
     * The display item of the arena.
     */
    var displayItem: ItemStack

    /**
     * Delete the arena.
     */
    fun delete()

    /**
     * Generate the arena world.
     */
    fun generate(): World

    /**
     * Spectate the arena.
     */
    fun spectateArena(viewer: Profile)

    /**
     * Generate the base world of the arena.
     */
    fun generateBaseWorld(): World
}
