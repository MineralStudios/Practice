package gg.mineral.practice.util.world

import com.google.common.collect.Lists
import gg.mineral.server.config.GlobalConfig
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.server.v1_8_R3.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.spigotmc.AsyncCatcher
import org.spigotmc.TrackingRange
import java.util.concurrent.Callable

class FastEntityTracker(private val world: WorldServer) : EntityTracker(world) {
    private val c: MutableSet<EntityTrackerEntry> = ObjectOpenHashSet()
    private val e: Int = world.minecraftServer.playerList.d()

    override fun track(entity: Entity) {
        when (entity) {
            is EntityPlayer -> {
                this.addEntity(entity, 512, GlobalConfig.getInstance().relativeMoveFrequency)
                val entityplayer: EntityPlayer = entity
                val iterator: Iterator<EntityTrackerEntry> = c.iterator()

                while (iterator.hasNext()) {
                    val entitytrackerentry: EntityTrackerEntry = iterator.next()

                    if (entitytrackerentry.tracker !== entityplayer) {
                        entitytrackerentry.updatePlayer(entityplayer)
                    }
                }
            }

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

    override fun addEntity(entity: Entity, i: Int, j: Int) = this.addEntity(entity, i, j, false)

    override fun addEntity(entity: Entity, i: Int, j: Int, flag: Boolean) {
        var i = i
        AsyncCatcher.catchOp("entity track") // Spigot
        i = TrackingRange.getEntityTrackingRange(entity, i) // Spigot
        if (i > this.e) {
            i = this.e
        }

        try {
            check(!trackedEntities.b(entity.id)) { "Entity is already tracked!" }

            val entitytrackerentry = EntityTrackerEntry(entity, i, j, flag)

            c.add(entitytrackerentry)
            trackedEntities.a(entity.id, entitytrackerentry)
            entitytrackerentry.scanPlayers(world.players)
        } catch (throwable: Throwable) {
            val crashreport: CrashReport = CrashReport.a(throwable, "Adding entity to track")
            val crashreportsystemdetails: CrashReportSystemDetails = crashreport.a("Entity To Track")

            crashreportsystemdetails.a("Tracking range", ("$i blocks") as Any)
            val finalI = i // CraftBukkit - fix decompile error
            crashreportsystemdetails.a("Update interval", object : Callable<Any?> {
                @Throws(Exception::class)
                fun a(): String {
                    var s = "Once per $finalI ticks" // CraftBukkit

                    if (finalI == Int.MAX_VALUE) { // CraftBukkit
                        s = "Maximum ($s)"
                    }

                    return s
                }

                @Throws(Exception::class)
                override fun call(): Any {
                    return this.a()
                }
            })
            entity.appendEntityCrashDetails(crashreportsystemdetails)
            val crashreportsystemdetails1: CrashReportSystemDetails = crashreport.a("Entity That Is Already Tracked")

            trackedEntities.get(entity.id)?.tracker
                ?.appendEntityCrashDetails(crashreportsystemdetails1)

            try {
                throw ReportedException(crashreport)
            } catch (reportedexception: ReportedException) {
                a.error("\"Silently\" catching entity tracking error.", reportedexception)
            }
        }
    }

    override fun untrackEntity(entity: Entity) {
        AsyncCatcher.catchOp("entity untrack") // Spigot
        if (entity is EntityPlayer) {
            val entityplayer: EntityPlayer = entity
            val iterator: Iterator<*> = c.iterator()

            while (iterator.hasNext()) {
                val entitytrackerentry: EntityTrackerEntry = iterator.next() as EntityTrackerEntry

                entitytrackerentry.a(entityplayer)
            }
        }

        val entitytrackerentry1: EntityTrackerEntry? = trackedEntities.d(entity.id)

        if (entitytrackerentry1 != null) {
            c.remove(entitytrackerentry1)
            entitytrackerentry1.a()
        }
    }

    override fun updatePlayers() {
        val arraylist: ArrayList<EntityPlayer> = Lists.newArrayList()
        val iterator: Iterator<EntityTrackerEntry> = c.iterator()

        while (iterator.hasNext()) {
            val entitytrackerentry: EntityTrackerEntry = iterator.next()

            entitytrackerentry.track(world.players)
            if (entitytrackerentry.n && entitytrackerentry.tracker is EntityPlayer) {
                arraylist.add(entitytrackerentry.tracker as EntityPlayer)
            }
        }

        for (i in arraylist.indices) {
            val entityplayer: EntityPlayer = arraylist[i]
            val iterator1: Iterator<EntityTrackerEntry> = c.iterator()

            while (iterator1.hasNext()) {
                val entitytrackerentry1: EntityTrackerEntry = iterator1.next()

                if (entitytrackerentry1.tracker !== entityplayer) {
                    entitytrackerentry1.updatePlayer(entityplayer)
                }
            }
        }
    }

    override fun a(entityplayer: EntityPlayer) {
        val iterator: Iterator<EntityTrackerEntry> = c.iterator()

        while (iterator.hasNext()) {
            val entitytrackerentry: EntityTrackerEntry = iterator.next()

            if (entitytrackerentry.tracker === entityplayer) {
                entitytrackerentry.scanPlayers(world.players)
            } else {
                entitytrackerentry.updatePlayer(entityplayer)
            }
        }
    }

    override fun a(entity: Entity, packet: Packet<PacketListener>?) {
        trackedEntities.get(entity.id)?.broadcast(packet)
    }

    override fun sendPacketToEntity(entity: Entity, packet: Packet<PacketListener>?) {
        trackedEntities.get(entity.id)?.broadcastIncludingSelf(packet)
    }

    override fun untrackPlayer(entityplayer: EntityPlayer?) {
        val iterator: Iterator<*> = c.iterator()

        while (iterator.hasNext()) {
            val entitytrackerentry: EntityTrackerEntry = iterator.next() as EntityTrackerEntry

            entitytrackerentry.clear(entityplayer)
        }
    }

    override fun a(entityplayer: EntityPlayer, chunk: Chunk) {
        val iterator: Iterator<EntityTrackerEntry> = c.iterator()

        while (iterator.hasNext()) {
            val entitytrackerentry: EntityTrackerEntry = iterator.next()

            if (entitytrackerentry.tracker !== entityplayer && entitytrackerentry.tracker.ae == chunk.locX && entitytrackerentry.tracker.ag == chunk.locZ) {
                entitytrackerentry.updatePlayer(entityplayer)
            }
        }
    }

    companion object {
        private val a: Logger = LogManager.getLogger()
    }
}