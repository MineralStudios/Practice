package gg.mineral.practice.listeners

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.getProfile
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.spigotmc.event.entity.EntityDismountEvent
import kotlin.math.round

class MovementListener : Listener {
    class BoundingBox(
        private var minX: Double, private var minY: Double,
        private var minZ: Double,
        private var maxX: Double,
        private var maxY: Double,
        private var maxZ: Double
    ) {
        val centerX: Double
            get() = (minX + maxX) / 2

        val centerY: Double
            get() = (minY + maxY) / 2

        val centerZ: Double
            get() = (minZ + maxZ) / 2

        fun shift(x: Double, y: Double, z: Double) {
            minX += x
            minY += y
            minZ += z
            maxX += x
            maxY += y
            maxZ += z
        }
    }

    @EventHandler
    fun onPearlTeleport(event: PlayerTeleportEvent) {
        if (event.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            val profile = getProfile(
                event.player.uniqueId
            )

            if (profile == null) return

            profile.updateVisibility()
            return
        }

        val player = event.player

        val pearlLandingLocation = event.to
        val patchedPearlYLocation = round(pearlLandingLocation.y)
        pearlLandingLocation.y = patchedPearlYLocation
        event.to = pearlLandingLocation

        val playerLoc = player.location
        // Create a bounding box with and set its center at the pearl landing location
        val playerHitBox = BoundingBox(
            playerLoc.x - 0.3, playerLoc.y,
            playerLoc.z - 0.3, playerLoc.x + 0.3, playerLoc.y + 1.8,
            playerLoc.z + 0.3
        )
        // Subtract the original bounding box coordinates to set them to zero. Then add
        // the new bounding box coordinates to match the pearl land location.
        playerHitBox.shift(
            -playerHitBox.centerX + pearlLandingLocation.x,
            -playerHitBox.centerY + pearlLandingLocation.y,
            -playerHitBox.centerZ + pearlLandingLocation.z
        )

        val pearlLandingBlock = pearlLandingLocation.block
        val pearlLandingBlockNorth = pearlLandingBlock.getRelative(BlockFace.NORTH)
        val pearlLandingBlockSouth = pearlLandingBlock.getRelative(BlockFace.SOUTH)
        val pearlLandingBlockEast = pearlLandingBlock.getRelative(BlockFace.EAST)
        val pearlLandingBlockWest = pearlLandingBlock.getRelative(BlockFace.WEST)

        // Patch carpets.
        if (pearlLandingBlock.type.name.contains("CARPET")) {
            pearlLandingLocation.y += 0.08
            playerHitBox.shift(0.0, 0.08, 0.0)
        }

        val pearlLandingBlock1 = pearlLandingLocation.clone().add(0.0, 1.0, 0.0).block
        val pearlLandingBlockNorth1 = pearlLandingBlock1.getRelative(BlockFace.NORTH)
        val pearlLandingBlockSouth1 = pearlLandingBlock1.getRelative(BlockFace.SOUTH)
        val pearlLandingBlockEast1 = pearlLandingBlock1.getRelative(BlockFace.EAST)
        val pearlLandingBlockWest1 = pearlLandingBlock1.getRelative(BlockFace.WEST)

        // If the pearl lands somewhere without any blocks adjacent to the landing spot
        // then return.
        val bottomRowNotAir =
            pearlLandingBlockNorth.type != Material.AIR || pearlLandingBlockSouth.type != Material.AIR || pearlLandingBlockEast.type != Material.AIR || pearlLandingBlockWest.type != Material.AIR

        val topRowNotAir =
            pearlLandingBlockNorth1.type != Material.AIR || pearlLandingBlockSouth1.type != Material.AIR || pearlLandingBlockEast1.type != Material.AIR || pearlLandingBlockWest1.type != Material.AIR

        if (bottomRowNotAir && topRowNotAir) {
            pearlLandingLocation.x = pearlLandingBlock.x + 0.5
            pearlLandingLocation.z = pearlLandingBlock.z + 0.5

            event.to = pearlLandingLocation
        }
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val profile = getProfile(
            e.player.uniqueId
        ) { it.playerStatus === PlayerStatus.FIGHTING }

        if (profile == null) return

        if (profile.match?.data?.deadlyWater == true) {
            val type = profile.player?.location?.block?.type ?: return
            if (type == Material.WATER || type == Material.STATIONARY_WATER) profile.match!!.end(profile)
        }
    }

    @EventHandler
    fun onPlayerDismount(e: EntityDismountEvent) {
        val profile = getProfile(
            e.entity.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (profile == null) return

        if (profile.inMatchCountdown) e.isCancelled = true
    }
}
