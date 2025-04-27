package gg.mineral.practice.util.world

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.server.v1_8_R3.*
import net.minecraft.server.v1_8_R3.BlockPosition
import java.io.File
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair.a as longHash

class RamChunkRegionLoader(private val chunkMap: Long2ObjectOpenHashMap<ChunkSnapshot?> = Long2ObjectOpenHashMap()) :
    ChunkRegionLoader(File("")) {

    // Always return false to trigger vanilla chunk loading
    override fun chunkExists(world: World?, i: Int, j: Int) = false

    override fun a(world: World, chunkX: Int, chunkZ: Int) = loadChunk(world, chunkX, chunkZ)?.get(0) as Chunk?

    override fun loadChunk(world: World, i: Int, j: Int): Array<Any>? {
        synchronized(chunkMap) {
            val snapshot: ChunkSnapshot = chunkMap[longHash(i, j)] ?: return null
            return arrayOf(chunkFromSnapshot(world, snapshot), NBTTagCompound())
        }
    }

    override fun a(world: World, chunk: Chunk) {}

    override fun c(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun b(world: World?, chunk: Chunk?) {}

    override fun a() {}

    override fun b() {}

    private fun Chunk.setHeightMap(intArray: IntArray) = this.a(intArray)

    private fun Chunk.setTerrainPopulated(populated: Boolean) = this.d(populated)

    private fun Chunk.setLightPopulated(populated: Boolean) = this.e(populated)

    private fun Chunk.setInhabitedTime(time: Long) = this.c(time)

    private fun Chunk.setBiomes(biomes: ByteArray) = this.a(biomes)

    private fun Chunk.setSections(sections: Array<ChunkSection?>) = this.a(sections)

    data class ChunkSectionSnapshot(
        val yPos: Int,
        val blocks: CharArray = CharArray(4096),
        val blockLight: NibbleArray = NibbleArray(),
        val hasSkylight: Boolean = true,
        val skyLight: NibbleArray? = if (hasSkylight) NibbleArray() else null
    ) {

        fun NibbleArray.clone() = NibbleArray(a().clone())

        fun toChunkSection(): ChunkSection {
            val chunkSection = ChunkSection(yPos, true)
            chunkSection.a(blocks.clone())
            chunkSection.a(blockLight.clone())
            skyLight?.let { chunkSection.b(it.clone()) }
            chunkSection.recalcBlockCounts()
            return chunkSection
        }

        fun setType(x: Int, y: Int, z: Int, blockData: IBlockData) {
            this.blocks[y shl 8 or (z shl 4) or x] = Block.d.b(blockData).toChar()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ChunkSectionSnapshot

            if (yPos != other.yPos) return false
            if (!blocks.contentEquals(other.blocks)) return false
            if (blockLight != other.blockLight) return false
            if (skyLight != other.skyLight) return false

            return true
        }

        override fun hashCode(): Int {
            var result = yPos
            result = 31 * result + blocks.contentHashCode()
            result = 31 * result + blockLight.hashCode()
            result = 31 * result + skyLight.hashCode()
            return result
        }
    }

    data class ChunkSnapshot(
        val xPos: Int,
        val zPos: Int,
        val sections: Array<ChunkSectionSnapshot?> = arrayOfNulls(16),
        val heightMap: IntArray = IntArray(256),
        val terrainPopulated: Boolean = true,
        val lightPopulated: Boolean = true,
        val timeInhabited: Long = 0,
        val biomes: ByteArray = ByteArray(256)
    ) {
        fun setTypeAndData(x: Int, y: Int, z: Int, type: Int, data: Byte) {
            if (y >= 0 && y shr 4 < sections.size) {
                val chunksection: ChunkSectionSnapshot = sections[y shr 4] ?: run {
                    val chunksection = ChunkSectionSnapshot(
                        y shr 4,
                        CharArray(4096),
                        NibbleArray(),
                        true,
                        NibbleArray(ByteArray(2048) { 0xFF.toByte() })
                    )
                    sections[y shr 4] = chunksection
                    chunksection
                }
                chunksection.setType(x and 15, y and 15, z and 15, Block.getById(type).fromLegacyData(data.toInt()))
                sections[y shr 4] = chunksection
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return other is ChunkSnapshot && xPos == other.xPos && zPos == other.zPos
        }

        override fun hashCode(): Int {
            var result = xPos
            result = 31 * result + zPos
            return result
        }
    }

    private fun chunkFromSnapshot(world: World, snapshot: ChunkSnapshot): Chunk {
        val chunk = Chunk(world, snapshot.xPos, snapshot.zPos)

        chunk.setHeightMap(snapshot.heightMap)
        chunk.setTerrainPopulated(snapshot.terrainPopulated)
        chunk.setLightPopulated(snapshot.lightPopulated)
        chunk.setInhabitedTime(snapshot.timeInhabited)
        chunk.setSections(snapshot.sections.map { it?.toChunkSection() }.toTypedArray())
        chunk.setBiomes(snapshot.biomes)

        return chunk
    }

    override fun loadEntities(chunk: Chunk, nbttagcompound: NBTTagCompound, world: World) {
        nbttagcompound.getList("Entities", 10)?.let {
            for (i2 in 0..<it.size()) {
                val nbttagcompound2: NBTTagCompound = it.get(i2)
                val entity: Entity? = EntityTypes.a(nbttagcompound2, world)

                chunk.g(true)
                if (entity != null) {
                    chunk.a(entity)
                    var entity1: Entity? = entity

                    var nbttagcompound3: NBTTagCompound = nbttagcompound2
                    while (nbttagcompound3.hasKeyOfType(
                            "Riding",
                            10
                        )
                    ) {
                        val entity2: Entity? = EntityTypes.a(nbttagcompound3.getCompound("Riding"), world)

                        if (entity2 != null) {
                            chunk.a(entity2)
                            entity1?.mount(entity2)
                        }

                        entity1 = entity2
                        nbttagcompound3 = nbttagcompound3.getCompound("Riding")
                    }
                }
            }
        }

        nbttagcompound.getList("TileEntities", 10)?.let {
            for (j2 in 0..<it.size()) {
                val nbttagcompound4: NBTTagCompound = it.get(j2)
                val tileentity: TileEntity? = TileEntity.c(nbttagcompound4)

                if (tileentity != null) chunk.a(tileentity)
            }
        }

        if (nbttagcompound.hasKeyOfType("TileTicks", 9)) {
            nbttagcompound.getList("TileTicks", 10)?.let {
                for (k2 in 0..<it.size()) {
                    val nbttagcompound5: NBTTagCompound = it.get(k2)

                    val block: Block =
                        if (nbttagcompound5.hasKeyOfType("i", 8)) Block.getByName(nbttagcompound5.getString("i"))
                        else Block.getById(nbttagcompound5.getInt("i"))

                    world.b(
                        BlockPosition(
                            nbttagcompound5.getInt("x"), nbttagcompound5.getInt("y"),
                            nbttagcompound5.getInt("z")
                        ), block, nbttagcompound5.getInt("t"),
                        nbttagcompound5.getInt("p")
                    )
                }
            }
        }
    }
}