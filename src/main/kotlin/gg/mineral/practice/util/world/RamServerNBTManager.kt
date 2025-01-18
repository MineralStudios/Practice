package gg.mineral.practice.util.world

import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.WorldData
import net.minecraft.server.v1_8_R3.WorldProvider
import java.io.File

class RamServerNBTManager(private val schematicFile: SchematicFile, file: File?, s: String) :
    RamWorldNBTStorage(file, s) {
    override fun createChunkLoader(worldprovider: WorldProvider?) = RamChunkRegionLoader(schematicFile.chunkMap)

    override fun saveWorldData(worlddata: WorldData, nbttagcompound: NBTTagCompound?) {
        worlddata.e(19133)
        super.saveWorldData(worlddata, nbttagcompound)
    }
}