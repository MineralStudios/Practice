package gg.mineral.practice.inventory

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.menus.QueueManagerMenu
import gg.mineral.practice.inventory.menus.SelectGametypeMenu
import gg.mineral.practice.inventory.menus.SelectModeMenu
import gg.mineral.practice.inventory.menus.SelectQueuetypeMenu
import gg.mineral.practice.managers.*
import gg.mineral.practice.managers.QueuetypeManager.queuetypes
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryPlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

class PlayerInventory(var holder: Profile) :
    CraftInventoryPlayer((holder.player?.inventory as CraftInventoryPlayer).inventory) {
    private val dataMap: ConcurrentHashMap<Int, Predicate<Profile>> = ConcurrentHashMap()
    var inventoryClickCancelled: Boolean = false

    override fun setItem(slot: Int, i: ItemStack?) {
        dataMap.remove(slot)
        super.setItem(slot, i)
    }

    fun setItem(slot: Int, i: ItemStack?, predicate: Predicate<Profile>) {
        dataMap[slot] = predicate
        super.setItem(slot, i)
    }

    fun setItem(slot: Int, itemStack: ItemStack?, runnable: Runnable) {
        dataMap[slot] =
            Predicate {
                runnable.run()
                true
            }
        super.setItem(slot, itemStack)
    }

    fun getTask(slot: Int) = dataMap[slot]

    fun getNumber(material: Material, durability: Short) =
        contents.count { it != null && it.type == material && it.durability == durability }

    fun getNumberAndAmount(material: Material, durability: Short) =
        contents.filter { it != null && it.type == material && it.durability == durability }
            .sumOf { it.amount }

    fun getNumber(material: Material) = contents.count { it != null && it.type == material }

    override fun clear() {
        this.dataMap.clear()
        super.clear()
        this.helmet = null
        this.chestplate = null
        this.leggings = null
        this.boots = null
    }

    override fun setContents(items: Array<ItemStack?>) {
        val mcItems = getInventory().contents
        for (i in mcItems.indices) {
            if (i >= items.size) setItem(i, null)
            else setItem(i, items[i])
        }
    }

    fun setInventoryToFollow() {
        inventoryClickCancelled = true
        clear()
        setItem(0, ItemStacks.STOP_FOLLOWING, Runnable { holder.following = null })
    }

    fun setInventoryForTournament() {
        inventoryClickCancelled = true
        clear()
        setItem(
            0, ItemStacks.WAIT_TO_LEAVE,
            Runnable { holder.message(ErrorMessages.CAN_NOT_LEAVE_YET) })

        object : BukkitRunnable() {
            override fun run() {
                setItem(0, ItemStacks.LEAVE_TOURNAMENT, Runnable { holder.tournament = null })
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 20)
    }

    fun setInventoryForEvent() {
        inventoryClickCancelled = true
        clear()
        setItem(
            0, ItemStacks.WAIT_TO_LEAVE,
            Runnable { holder.message(ErrorMessages.CAN_NOT_LEAVE_YET) })

        object : BukkitRunnable() {
            override fun run() {
                setItem(0, ItemStacks.LEAVE_EVENT, Runnable { holder.event = null })
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 20)
    }

    fun setInventoryForParty() {
        if (this.holder.playerStatus === PlayerStatus.QUEUEING) return

        if (this.holder.match?.ended == false) return
        this.inventoryClickCancelled = true
        this.clear()
        this.setItem(8, ItemStacks.WAIT_TO_LEAVE, Runnable { holder.message(ErrorMessages.CAN_NOT_LEAVE_YET) })

        object : BukkitRunnable() {
            override fun run() {
                if (holder.playerStatus === PlayerStatus.QUEUEING) return

                if (holder.match?.ended == false) return
                setItem(
                    8, ItemStacks.LEAVE_PARTY
                ) { p: Profile -> p.player?.performCommand("p leave") == true }
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 20)

        this.setItem(
            7, ItemStacks.LIST_PLAYERS
        ) { profile: Profile -> profile.player?.performCommand("p list") == true }
        this.setItem(3, ItemStacks.OPEN_PARTY) { p: Profile -> p.player?.performCommand("p open") == true }
        this.setItem(4, ItemStacks.DUEL) { p: Profile -> p.player?.performCommand("duel") == true }
        this.setItem(5, ItemStacks.PARTY_SPLIT) { p: Profile ->
            p.openMenu(SelectModeMenu(SubmitAction.P_SPLIT))
            true
        }

        for (queuetype in queuetypes.values) {
            queuetype ?: continue
            if (!queuetype.unranked) continue

            val item = ItemBuilder(queuetype.displayItem)
                .name(CC.SECONDARY + CC.B + queuetype.displayName)
                .lore(CC.ACCENT + "Right click to queue.")
                .build()
            this.setItem(
                queuetype.slotNumber, item
            ) { profile: Profile ->
                profile.party?.let {
                    if (it.partyLeader != profile) {
                        profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER)
                        return@setItem true
                    }
                    if (it.partyMembers.size < 2) {
                        profile.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH)
                        return@setItem true
                    }
                }

                profile.openMenu(SelectGametypeMenu(queuetype, SelectGametypeMenu.Type.UNRANKED, null))
                true
            }
        }
    }

    fun setInventoryForLobby() {
        if (this.holder.playerStatus === PlayerStatus.QUEUEING) return

        if (this.holder.match?.ended == false) return

        this.inventoryClickCancelled = true
        this.clear()

        for (queuetype in queuetypes.values) {
            queuetype ?: continue
            val item = ItemBuilder(queuetype.displayItem)
                .name(CC.SECONDARY + CC.B + queuetype.displayName)
                .lore(CC.ACCENT + "Right click to queue.").build()
            setItem(
                queuetype.slotNumber, item
            ) { p: Profile ->
                if (queuetype.community) {
                    p.message(ErrorMessages.COMING_SOON)
                    return@setItem true
                }
                p.openMenu(
                    SelectGametypeMenu(
                        queuetype,
                        if (queuetype.unranked)
                            SelectGametypeMenu.Type.UNRANKED
                        else
                            SelectGametypeMenu.Type.QUEUE, null
                    )
                )
                true
            }
        }

        if (KitEditorManager.enabled) {
            val editor = ItemBuilder(KitEditorManager.displayItem)
                .name(CC.SECONDARY + CC.B + KitEditorManager.displayName)
                .lore(CC.ACCENT + "Right click to edit a kit.")
                .build()
            setItem(
                KitEditorManager.slot, editor
            ) { profile: Profile ->
                profile.openMenu(SelectQueuetypeMenu(SelectGametypeMenu.Type.KIT_EDITOR))
                true
            }
        }

        if (PartyManager.enabled) {
            val parties = ItemBuilder(PartyManager.displayItem)
                .name(CC.SECONDARY + CC.B + PartyManager.displayName)
                .lore(CC.ACCENT + "Right click to create a party.")
                .build()
            setItem(
                PartyManager.slot, parties
            ) { profile: Profile -> profile.player?.performCommand("p create") == true }
        }

        if (PlayerSettingsManager.enabled) {
            val settings = ItemBuilder(PlayerSettingsManager.displayItem)
                .name(CC.SECONDARY + CC.B + PlayerSettingsManager.displayName)
                .lore(CC.ACCENT + "Right click to open settings.")
                .build()
            setItem(
                PlayerSettingsManager.slot, settings
            ) { p: Profile -> p.player?.performCommand("settings") == true }
        }

        if (SpectateManager.enabled) {
            val spectate = ItemBuilder(SpectateManager.displayItem)
                .name(CC.SECONDARY + CC.B + SpectateManager.displayName)
                .lore(CC.ACCENT + "Right click to spectate.")
                .build()
            setItem(
                SpectateManager.slot, spectate
            ) { p: Profile -> p.player?.performCommand("spectate") == true }
        }

        if (LeaderboardManager.enabled) {
            val leaderboard = ItemBuilder(LeaderboardManager.displayItem)
                .name(CC.SECONDARY + CC.B + LeaderboardManager.displayName)
                .lore(CC.ACCENT + "Right click to view.")
                .build()
            setItem(
                LeaderboardManager.slot, leaderboard
            ) { p: Profile -> p.player?.performCommand("leaderboard") == true }
        }
    }

    fun setInventoryForQueue() {
        this.inventoryClickCancelled = true
        this.clear()

        this.setItem(
            0, ItemStacks.LEAVE_QUEUE,
            Runnable {
                holder.removeFromQueue()
                if (holder.party != null) setInventoryForParty()
                else setInventoryForLobby()
            })

        this.setItem(
            4, ItemStacks.QUEUE_MANAGER,
            Runnable { holder.openMenu(QueueManagerMenu()) })

        this.setItem(
            8, ItemStacks.QUEUE,
            Runnable { holder.openMenu(SelectQueuetypeMenu(SelectGametypeMenu.Type.QUEUE)) })
    }

    fun setInventoryForSpectating() {
        this.inventoryClickCancelled = true
        this.clear()
        this.setItem(0, ItemStacks.STOP_SPECTATING, holder::stopSpectating)
    }
}
