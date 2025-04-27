package gg.mineral.practice.util.world

import org.bukkit.Material

data class BlockData(val location: BlockPosition, var type: Material, val data: Byte)
