package gg.mineral.practice.util.world

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector
import java.lang.ref.WeakReference
import kotlin.math.*

class SpawnLocation(
    val blockX: Int,
    val blockY: Int,
    val blockZ: Int,
    private var yaw: Float = 0f,
    private var pitch: Float = 0f
) {
    private val x: Double
        get() = blockX + 0.5
    val y: Double
        get() = blockY.toDouble()
    private val z: Double
        get() = blockZ + 0.5

    var direction: Vector
        get() {
            val vector = Vector()
            val rotX = yaw.toDouble()
            val rotY = pitch.toDouble()
            vector.setY(-sin(Math.toRadians(rotY)))
            val xz = cos(Math.toRadians(rotY))
            vector.setX(-xz * sin(Math.toRadians(rotX)))
            vector.setZ(xz * cos(Math.toRadians(rotX)))
            return vector
        }
        set(vector) {
            val x = vector.x
            val z = vector.z
            if (x == 0.0 && z == 0.0)
                this.pitch = if (vector.y > 0.0) -90.0f else 90.0f
            else {
                val theta = atan2(-x, z)
                this.yaw =
                    Math.toDegrees((theta + (Math.PI * 2.0)) % (Math.PI * 2.0))
                        .toFloat()
                val x2 = NumberConversions.square(x)
                val z2 = NumberConversions.square(z)
                val xz = sqrt(x2 + z2)
                this.pitch = Math.toDegrees(atan(-vector.y / xz)).toFloat()
            }
        }

    fun bukkit(world: World) = Location(world, x, y, z, yaw, pitch)

    fun bukkit(worldRef: WeakReference<World>) = worldRef.get()?.let { bukkit(it) }
}