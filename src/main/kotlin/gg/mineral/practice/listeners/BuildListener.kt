package gg.mineral.practice.listeners

import gg.mineral.practice.arena.BuildArena
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.getProfile
import gg.mineral.practice.util.messages.impl.ErrorMessages
import gg.mineral.practice.util.world.BlockData
import gg.mineral.practice.util.world.appender.toBlockPosition
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
        )

        (profile?.editingArena as? BuildArena)?.let {
            e.isCancelled = false
            val newList = it.breakableBlockLocations.toMutableList()
            newList.removeIf { data -> data.location.isLocation(e.block.location) }
            it.breakableBlockLocations = newList
            return
        }

        if (profile == null || profile.playerStatus !== PlayerStatus.FIGHTING) {
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
        )

        (profile?.editingArena as? BuildArena)?.let {
            e.isCancelled = false
            val newList = it.breakableBlockLocations.toMutableList()
            newList.add(BlockData(e.blockPlaced.location.toBlockPosition(), e.blockPlaced.type, e.blockPlaced.data))
            it.breakableBlockLocations = newList
            return
        }

        val canPlace = e.player.isOp && e.player.gameMode == GameMode.CREATIVE

        if (profile == null || profile.playerStatus !== PlayerStatus.FIGHTING) {
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
