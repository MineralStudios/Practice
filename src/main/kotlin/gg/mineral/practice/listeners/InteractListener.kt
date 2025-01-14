package gg.mineral.practice.listeners

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.inventory.menus.AddItemsMenu
import gg.mineral.practice.inventory.menus.SaveLoadKitsMenu
import gg.mineral.practice.managers.ProfileManager.getOrCreateProfile
import gg.mineral.practice.util.items.ItemStacks
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.function.Consumer

class InteractListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val action = e.action

        if (action == Action.PHYSICAL
            && e.player.location.block.getRelative(BlockFace.DOWN).type == Material.SOIL
        ) {
            e.isCancelled = true
            return
        }

        val uuid = e.player.uniqueId
        val profile = getOrCreateProfile(e.player)
        val match = profile.match

        if (match != null
            && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
        ) match.stat(
            uuid,
            Consumer { it.click() })

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return

        val predicate = profile.inventory.getTask(profile.inventory.heldItemSlot)

        if (predicate != null && predicate.test(profile)) return

        if (profile.inMatchCountdown) {
            e.isCancelled = true
            return
        }

        if (profile.playerStatus === PlayerStatus.KIT_CREATOR
            || profile.playerStatus === PlayerStatus.KIT_EDITOR
        ) {
            e.isCancelled = true

            if (e.clickedBlock == null) return

            if (e.clickedBlock.type == Material.ANVIL) {
                profile.openMenu(SaveLoadKitsMenu())
                return
            }

            if (e.clickedBlock.type == Material.CHEST
                && profile.playerStatus === PlayerStatus.KIT_EDITOR
            ) {
                profile.openMenu(AddItemsMenu())
                return
            }

            if (e.clickedBlock.type == Material.WOODEN_DOOR) {
                profile.kitCreator = null
                profile.kitEditor = null
                return
            }

            return
        }

        if (e.material != null) {
            if (e.material == Material.MUSHROOM_SOUP) {
                object : BukkitRunnable() {
                    override fun run() {
                        if (profile.player.health >= 20) return

                        profile.inventory.itemInHand = ItemStacks.EMPTY_BOWL

                        if (profile.player.health <= 14.0) {
                            profile.player.health += 6.0
                            return
                        }

                        profile.player.health = 20.0
                    }
                }.runTaskLater(PracticePlugin.INSTANCE, 1)
                return
            }
        }

        if (e.clickedBlock != null && e.clickedBlock.type == Material.TNT) {


            if (profile.playerStatus !== PlayerStatus.FIGHTING) {
                e.isCancelled = true
                return
            }

            val type = profile.inventory.itemInHand.type
            if (type != Material.FLINT_AND_STEEL && type != Material.FIREBALL) return

            e.isCancelled = profile.match?.data?.griefing == false
        }
    }

    @EventHandler
    fun onEntityInteract(e: EntityInteractEvent) {
        e.isCancelled = e.block.type == Material.SOIL
    }
}
