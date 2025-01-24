package gg.mineral.practice.util.world

import gg.mineral.practice.PracticePlugin
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit.*
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard
import java.io.File
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair.a as longHash

class SchematicFile(
    private val source: File
) {
    val isEmpty: Boolean
        get() = chunkMap.isEmpty()
    val chunkMap: Long2ObjectOpenHashMap<RamChunkRegionLoader.ChunkSnapshot?> = Long2ObjectOpenHashMap()

    fun addBlock(x: Int, y: Int, z: Int, type: Int, data: Byte) {
        val chunkX = x shr 4
        val chunkZ = z shr 4
        val chunkCoord = longHash(chunkX, chunkZ)
        val snapshot =
            chunkMap.computeIfAbsent(chunkCoord, Long2ObjectFunction { _: Long ->
                RamChunkRegionLoader.ChunkSnapshot(
                    chunkX,
                    chunkZ
                )
            })
        snapshot?.setTypeAndData(x, y, z, type, data)
    }

    fun generateWorld(suffix: String): World {
        val schemName = source.name

        val name = schemName.substring(0, schemName.length - ".schematic".length) + suffix
        val world: World? = getWorld(name)
        val type: WorldType = WorldType.FLAT
        val generateStructures = false

        if (world != null)
            return world

        var dimension: Int = CraftWorld.CUSTOM_DIMENSION_OFFSET + MinecraftServer.getServer().worlds.size
        var used = false
        do {
            for (server in MinecraftServer.getServer().worlds) {
                used = server.dimension == dimension
                if (used) {
                    dimension++
                    break
                }
            }
        } while (used)
        val hardcore = false

        val sdm: IDataManager = RamServerNBTManager(this, getWorldContainer(), name)
        var worlddata = sdm.worldData
        if (worlddata == null) {
            val worldSettings = WorldSettings(
                0,
                WorldSettings.EnumGamemode.getById(getDefaultGameMode().value), generateStructures, hardcore,
                type
            )
            worldSettings.setGeneratorSettings("")
            worlddata = WorldData(worldSettings, name)
        }
        worlddata.checkName(name)
        val internal = FastWorldServer(
            sdm, worlddata, dimension, MinecraftServer.getServer().methodProfiler,
            World.Environment.NORMAL
        ).b() as WorldServer
        internal.keepSpawnInMemory = false
        internal.savingDisabled = true
        val gameRules = internal.gameRules
        gameRules.set("doFireTick", "false")
        gameRules.set("mobGriefing", "false")
        gameRules.set("keepInventory", "false")
        gameRules.set("doMobSpawning", "true")
        gameRules.set("doMobLoot", "true")
        gameRules.set("doTileDrops", "true")
        gameRules.set("doEntityDrops", "true")
        gameRules.set("commandBlockOutput", "false")
        gameRules.set("naturalRegeneration", "true")
        gameRules.set("doDaylightCycle", "false")
        gameRules.set("logAdminCommands", "true")
        gameRules.set("showDeathMessages", "true")
        gameRules.set("randomTickSpeed", "3")
        gameRules.set("sendCommandFeedback", "true")
        gameRules.set("reducedDebugInfo", "false")

        internal.scoreboard = (getScoreboardManager().mainScoreboard as CraftScoreboard).handle

        internal.tracker = FastEntityTracker(internal)
        internal.addIWorldAccess(WorldManager(MinecraftServer.getServer(), internal))
        internal.worldData.difficulty = EnumDifficulty.EASY
        internal.setSpawnFlags(false, false)
        MinecraftServer.getServer().worlds.add(internal)

        //getPluginManager().callEvent(WorldInitEvent(internal.world))
        //getPluginManager().callEvent(WorldLoadEvent(internal.world))
        PracticePlugin.INSTANCE.logger.info("Loading schematic with ${chunkMap.size} chunks (${name})")
        return internal.world
    }
}