package gg.mineral.practice.listeners

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.getProfile
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent

class BuildListener : Listener {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val profile = getProfile(
            e.player.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (profile == null) {
            e.isCancelled = !(e.player.isOp && e.player.gameMode == GameMode.CREATIVE)
            return
        }

        val match = profile.match

        val location = e.block.location

        if (match!!.buildLog.contains(location)) e.isCancelled = !match.data.build
        else e.isCancelled = !match.data.griefing

        if (e.block.type == Material.TNT && !e.isCancelled) match.decreasePlacedTnt()
    }

    @EventHandler
    fun onBlockBurn(e: BlockBurnEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onFramePlace(e: HangingPlaceEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val profile = getProfile(
            e.player.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }
        val canPlace = e.player.isOp && e.player.gameMode == GameMode.CREATIVE

        if (profile == null) {
            e.isCancelled = !canPlace
            return
        }

        val match = profile.match
        e.isCancelled = !match!!.data.build || e.blockPlaced.y > match.buildLimit

        if (e.isCancelled) return

        if (e.blockPlaced.type == Material.TNT) {
            if (match.placedTnt > 128) {
                profile.message(ErrorMessages.MAX_TNT)
                e.isCancelled = true
                return
            }

            match.increasePlacedTnt()
        }

        match.buildLog.add(e.blockPlaced.location)
    }

    @EventHandler
    fun onPlayerBucketEmpty(e: PlayerBucketEmptyEvent) {
        val profile = getProfile(
            e.player.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }
        val canPlace = e.player.isOp && e.player.gameMode == GameMode.CREATIVE

        if (profile == null) {
            e.isCancelled = !canPlace
            return
        }

        e.isCancelled = !profile.match!!.data.build
    }

    @EventHandler
    fun onBlockIgnite(event: BlockIgniteEvent) {
        event.isCancelled = event.cause != IgniteCause.FLINT_AND_STEEL
    }
}
