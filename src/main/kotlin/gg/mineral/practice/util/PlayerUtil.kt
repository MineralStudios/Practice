package gg.mineral.practice.util

import gg.mineral.practice.entity.Profile
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import kotlin.math.ceil

object PlayerUtil {
    fun teleport(from: CraftPlayer, to: Location) {
        from.handle.playerConnection.checkMovement = false
        val newY = ceil(to.y)
        to.y = newY
        from.teleport(to)
    }

    private fun teleportNoGlitch(from: CraftPlayer, to: Location) {
        from.handle.playerConnection.checkMovement = false
        val newY = ceil(to.y)
        to.y = newY + 0.5
        from.teleport(to)
    }

    fun teleport(from: Profile, to: Location) {
        from.player?.let { teleport(it, to) }
    }

    fun teleportNoGlitch(from: Profile, to: Location) {
        from.player?.let { teleportNoGlitch(it, to) }
    }

    fun teleport(from: CraftPlayer, to: Player) {
        teleport(from, to.location)
    }
}
