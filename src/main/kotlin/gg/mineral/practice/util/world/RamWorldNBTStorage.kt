package gg.mineral.practice.util.world

import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit.getDefaultGameMode
import java.io.File
import java.util.*


open class RamWorldNBTStorage(file: File?, private val fileName: String) : IDataManager,
    IPlayerFileData {
    private val directory: File = File(file, fileName)
    private val dataDir: File = File(this.directory, "data")
    private val uuid by lazy { UUID.randomUUID() }
    private val worldDataCached by lazy {
        val worldSettings = WorldSettings(
            0,
            WorldSettings.EnumGamemode.getById(getDefaultGameMode().value), false, false,
            WorldType.FLAT
        )
        worldSettings.setGeneratorSettings("")
        WorldData(worldSettings, fileName)
    }

    override fun getWorldData() = worldDataCached

    override fun checkSession() {}

    override fun createChunkLoader(worldprovider: WorldProvider?): IChunkLoader {
        error("Old Chunk Storage is no longer supported.")
    }

    override fun saveWorldData(worlddata: WorldData, nbttagcompound: NBTTagCompound?) {}

    override fun saveWorldData(worlddata: WorldData) {}

    override fun getPlayerFileData() = this

    override fun save(entityhuman: EntityHuman) {}

    override fun load(entityhuman: EntityHuman) = null

    override fun getSeenPlayers(): Array<out String?> = arrayOfNulls(0)

    override fun a() {}

    override fun getDirectory() = directory

    override fun getDataFile(s: String) = File(this.dataDir, "$s.dat")

    override fun g() = this.fileName

    override fun getUUID(): UUID = uuid
}