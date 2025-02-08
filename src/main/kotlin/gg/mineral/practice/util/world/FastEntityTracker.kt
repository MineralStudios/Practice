package gg.mineral.practice.util.world

import gg.mineral.practice.util.collection.FastUtilIntHashMap
import gg.mineral.server.config.GlobalConfig
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.server.v1_8_R3.*
import org.spigotmc.TrackingRange

class FastEntityTracker(private val world: WorldServer) : EntityTracker(world) {
    private fun PlayerList.renderDistance() = this.d()
    private fun EntityTrackerEntry.removePlayer(player: EntityPlayer) = this.a(player)
    private fun EntityTrackerEntry.remove() = this.a()
    private fun <V> IntHashMap<V>.remove(key: Int) = this.d(key)
    private fun <V> IntHashMap<V>.containsKey(key: Int): Boolean = this.b(key)
    private fun <V> IntHashMap<V>.put(key: Int, value: V) = this.a(key, value)

    private val playerChunkRange: Int = world.minecraftServer.playerList.renderDistance()
    private val entrySet: MutableSet<EntityTrackerEntry> = ObjectOpenHashSet()

    init {
        this.trackedEntities = FastUtilIntHashMap()
    }

    override fun track(entity: Entity) {
        when (entity) {
            is EntityPlayer -> this.addEntity(entity, 512, GlobalConfig.getInstance().relativeMoveFrequency)
                .also { entrySet.filter { it.tracker !== entity }.forEach { it.updatePlayer(entity) } }

            is EntityFishingHook -> this.addEntity(entity, 64, 5, true)
            is EntityArrow -> this.addEntity(entity, 64, 20, true)
            is EntitySmallFireball -> this.addEntity(entity, 64, 10, false)
            is EntityFireball -> this.addEntity(entity, 64, 10, false)
            is EntitySnowball -> this.addEntity(entity, 64, 10, true)
            is EntityEnderPearl -> this.addEntity(entity, 64, 10, true)
            is EntityEnderSignal -> this.addEntity(entity, 64, 4, true)
            is EntityEgg -> this.addEntity(entity, 64, 10, true)
            is EntityPotion -> this.addEntity(entity, 64, 10, true)
            is EntityThrownExpBottle -> this.addEntity(entity, 64, 10, true)
            is EntityFireworks -> this.addEntity(entity, 64, 10, true)
            is EntityItem -> this.addEntity(entity, 64, 20, true)
            is EntityMinecartAbstract -> this.addEntity(entity, 80, 3, false)
            is EntityBoat -> this.addEntity(entity, 80, 3, false)
            is EntitySquid -> this.addEntity(entity, 64, 3, false)
            is EntityWither -> this.addEntity(entity, 80, 3, false)
            is EntityBat -> this.addEntity(entity, 80, 3, false)
            is EntityEnderDragon -> this.addEntity(entity, 160, 3, false)
            is IAnimal -> this.addEntity(entity, 80, 3, false)
            is EntityTNTPrimed -> this.addEntity(entity, 160, 10, true)
            is EntityFallingBlock -> this.addEntity(entity, 160, 20, true)
            is EntityHanging -> this.addEntity(entity, 160, Int.MAX_VALUE, false)
            is EntityArmorStand -> this.addEntity(entity, 160, 3, false)
            is EntityExperienceOrb -> this.addEntity(entity, 160, 20, true)
            is EntityEnderCrystal -> this.addEntity(entity, 256, Int.MAX_VALUE, false)
        }
    }

    override fun addEntity(entity: Entity, maxRange: Int, updateFreq: Int, disableRelMove: Boolean) {
        val range = TrackingRange.getEntityTrackingRange(entity, maxRange)
            .let { if (it > this.playerChunkRange) this.playerChunkRange else it }

        check(!trackedEntities.containsKey(entity.id)) { "Entity is already tracked!" }

        EntityTrackerEntry(entity, range, updateFreq, disableRelMove).let {
            entrySet.add(it)
            trackedEntities.put(entity.id, it)
            it.scanPlayers(world.players)
        }
    }

    override fun untrackEntity(entity: Entity) {
        if (entity is EntityPlayer) entrySet.forEach { it.removePlayer(entity) }

        trackedEntities.remove(entity.id)?.let {
            entrySet.remove(it)
            it.remove()
        }
    }

    override fun updatePlayers() {
        val players = mutableListOf<EntityPlayer>()

        entrySet.forEach {
            it.track(world.players)
            val tracker = it.tracker
            if (it.n && tracker is EntityPlayer)
                players.add(tracker)
        }

        for (player in players)
            entrySet.forEach { if (it.tracker !== player) it.updatePlayer(player) }
    }

    override fun a(player: EntityPlayer) {
        val iterator: Iterator<EntityTrackerEntry> = entrySet.iterator()

        while (iterator.hasNext()) {
            val entitytrackerentry: EntityTrackerEntry = iterator.next()

            if (entitytrackerentry.tracker === player) {
                entitytrackerentry.scanPlayers(world.players)
            } else {
                entitytrackerentry.updatePlayer(player)
            }
        }
    }

    override fun a(entity: Entity, packet: Packet<PacketListener>) {
        trackedEntities.get(entity.id)?.broadcast(packet)
    }

    override fun sendPacketToEntity(entity: Entity, packet: Packet<PacketListener>) {
        trackedEntities.get(entity.id)?.broadcastIncludingSelf(packet)
    }

    override fun untrackPlayer(player: EntityPlayer) {
        entrySet.forEach { it.clear(player) }
    }

    override fun a(player: EntityPlayer, chunk: Chunk) {
        val iterator: Iterator<EntityTrackerEntry> = entrySet.iterator()

        while (iterator.hasNext()) {
            val entitytrackerentry: EntityTrackerEntry = iterator.next()

            if (entitytrackerentry.tracker !== player && entitytrackerentry.tracker.ae == chunk.locX && entitytrackerentry.tracker.ag == chunk.locZ)
                entitytrackerentry.updatePlayer(player)

        }
    }
}