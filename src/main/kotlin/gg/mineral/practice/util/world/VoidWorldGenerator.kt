package gg.mineral.practice.util.world

import gg.mineral.api.collection.GlueList
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import java.util.*

class VoidWorldGenerator : ChunkGenerator() {
    override fun getDefaultPopulators(world: World): List<BlockPopulator> = GlueList()

    override fun canSpawn(world: World, x: Int, z: Int) = true

    @Deprecated("Deprecated in Java", ReplaceWith("ByteArray(32768)"))
    override fun generate(world: World, rand: Random, chunkx: Int, chunkz: Int) = ByteArray(32768)

    override fun getFixedSpawnLocation(world: World, random: Random) = Location(world, 0.0, 128.0, 0.0)
}
