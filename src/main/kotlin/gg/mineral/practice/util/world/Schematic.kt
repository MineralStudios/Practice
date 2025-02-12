package gg.mineral.practice.util.world

import gg.mineral.server.config.GlobalConfig
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.server.v1_8_R3.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException


object Schematic {
    private val SCHEMATICS: Map<String, SchematicFile?> by lazy {
        val schematicFolder = GlobalConfig.getInstance().schematicWorldsFolder
        val map = Object2ObjectOpenHashMap<String, SchematicFile>()
        if (schematicFolder != null) {
            // Iterate over files
            val files = File(schematicFolder).listFiles()


            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        val name = file.name
                        if (name.endsWith(".schematic")) {
                            val worldName = name.substring(0, name.length - ".schematic".length)
                            try {
                                val schematic = load(file)
                                map[worldName] = schematic
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
        map
    }

    fun get(name: String) = SCHEMATICS[name]

    /**
     * Loads a schematic from a source file, returning a [SchematicFile]
     * containing all blocks in a single list.
     *
     * @param source The .schematic file to load
     * @return [SchematicFile] containing dimensions and block data
     * @throws IOException if an I/O error occurs during reading
     */
    @Throws(IOException::class)
    fun load(source: File): SchematicFile {
        FileInputStream(source).use { stream ->
            val nbt: NBTTagCompound = NBTCompressedStreamTools.readNBT(stream)
            val nbtValue: Map<String, NBTBase> = nbt.map

            val widthTag: NBTBase? = nbtValue["Width"]
            val heightTag: NBTBase? = nbtValue["Height"]
            val lengthTag: NBTBase? = nbtValue["Length"]

            require(!(widthTag == null || heightTag == null || lengthTag == null)) { "Invalid schematic file: missing dimensions" }
            require(!((widthTag !is NBTTagShort) || (heightTag !is NBTTagShort) || (lengthTag !is NBTTagShort))) { "Invalid schematic file: invalid dimension types" }

            val width: Short = widthTag.e()
            val height: Short = heightTag.e()
            val length: Short = lengthTag.e()

            val blocksTag: NBTBase? = nbtValue["Blocks"]
            val dataTag: NBTBase? = nbtValue["Data"]
            require(!(blocksTag == null || dataTag == null)) { "Invalid schematic file: missing block data" }
            require(!(blocksTag !is NBTTagByteArray || dataTag !is NBTTagByteArray)) { "Invalid schematic file: block/data tags not byte arrays" }

            val blockArray: ByteArray = blocksTag.c()
            val dataArray: ByteArray = dataTag.c()

            // Prepare a list to hold all the blocks
            val schemFile = SchematicFile(source)

            // Convert the block/data arrays into a list of SchematicBlock objects
            for (x in 0..<width) {
                for (y in 0..<height) {
                    for (z in 0..<length) {
                        val index = (y * length + z) * width + x
                        val type = blockArray[index].toInt() and 0xFF // Convert signed byte to unsigned int
                        val data = (dataArray[index].toInt() and 0x0F).toByte()

                        schemFile.addBlock(x, y, z, type, data)
                    }
                }
            }
            return schemFile
        }
    }
}