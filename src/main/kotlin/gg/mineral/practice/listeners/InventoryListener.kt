package gg.mineral.practice.listeners

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.managers.ProfileManager.getOrCreateProfile
import gg.mineral.practice.managers.ProfileManager.getProfile
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent

class InventoryListener : Listener {
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val profile = getOrCreateProfile(e.whoClicked as Player)
        val menu = profile.openMenu

        val canClick = profile.player.isOp && profile.player.gameMode == GameMode.CREATIVE

        val clickType = e.click

        e.isCancelled = e.currentItem != null && e.currentItem.type == Material.TNT

        if (profile.inventory.inventoryClickCancelled) e.isCancelled = !canClick

        if (menu == null) return

        if (e.clickedInventory == null || e.clickedInventory != e.view.topInventory) return

        if (e.inventory == null || e.inventory != menu.inventory) return

        if (e.slot < e.view.topInventory.size) e.isCancelled = menu.clickCancelled

        val predicate = menu.getTask(e.slot) ?: return

        predicate.accept(Interaction(profile, clickType))
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val profile = getOrCreateProfile(e.player as Player)
        val oldMenu = profile.openMenu
        profile.openMenu = null

        if (oldMenu != null && !oldMenu.closed && e.inventory == oldMenu.inventory) {
            oldMenu.closed = true
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                PracticePlugin.INSTANCE,
                { oldMenu.onClose() }, 1
            )
        }
    }

    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        val player = e.player
        val canDrop = player.isOp && player.gameMode == GameMode.CREATIVE

        val profile = getProfile(player)

        if (profile != null) {
            if (profile.playerStatus === PlayerStatus.KIT_CREATOR
                || profile.playerStatus === PlayerStatus.KIT_EDITOR
            ) {
                e.isCancelled = false
                Bukkit.getScheduler().runTaskLater(
                    PracticePlugin.INSTANCE,
                    { e.itemDrop.remove() }, 20L
                )
                return
            }

            val match = profile.match

            if (match != null) {
                match.itemRemovalQueue.add(e.itemDrop)
                return
            }
        }

        e.isCancelled = !canDrop
    }

    @EventHandler
    fun onPlayerPickupItem(e: PlayerPickupItemEvent) {
        val canPickup = e.player.isOp && e.player.gameMode == GameMode.CREATIVE

        if (canPickup) return

        e.isCancelled = getProfile(
            e.player.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING } == null
    }
}
