package gg.mineral.practice.util.world

import net.minecraft.server.v1_8_R3.*
import org.bukkit.World.Environment

class FastWorldServer(
    dataManager: IDataManager,
    worldData: WorldData,
    i: Int,
    methodProfiler: MethodProfiler,
    environment: Environment
) : WorldServer(
    MinecraftServer.getServer(),
    dataManager,
    worldData,
    i,
    methodProfiler,
    environment,
    voidWorldGenerator
) {

    override fun everyoneDeeplySleeping() = false

    // Update blocks
    override fun h() {}

    companion object {
        val voidWorldGenerator = VoidWorldGenerator()
    }
}