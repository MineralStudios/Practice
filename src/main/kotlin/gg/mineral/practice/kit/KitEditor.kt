package gg.mineral.practice.kit

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.KitEditorManager.bukkitLocation
import gg.mineral.practice.managers.ProfileManager.playerConfig
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.scoreboard.impl.KitEditorScoreboard
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.messages.impl.ChatMessages
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.bukkit.inventory.ItemStack

class KitEditor(val gametype: Gametype, val queuetype: Queuetype, val profile: Profile) {

    fun save(loadoutSlot: Int) {
        val newKitContents: Array<ItemStack?> = profile.inventory.contents

        val hash = (queuetype.id.toInt() shl 8 or gametype.id.toInt()).toShort()
        val kitLoadouts: Int2ObjectOpenHashMap<Array<ItemStack?>> = profile.getCustomKits(
            queuetype, gametype, hash
        )

        kitLoadouts.put(loadoutSlot, newKitContents)
        profile.customKits.put(hash, kitLoadouts)
        val config = playerConfig
        val path = (profile.name + ".KitData." + gametype.name + "."
                + queuetype.name + "." + loadoutSlot + ".")

        for (f in newKitContents.indices) {
            val newItem = newKitContents[f]
            val oldItem: ItemStack? = gametype.kit.contents[f]

            val newItemNull = newItem == null

            if (newItemNull && oldItem == null) continue

            if (newItemNull) {
                config[path + f] = "empty"
                continue
            }

            if (newItem?.isSimilar(oldItem) == true) continue

            config[path + f] = newItem
        }

        config.save()

        ChatMessages.KIT_SAVED.send(profile.player)
    }

    fun start() {
        profile.playerStatus = PlayerStatus.KIT_EDITOR
        profile.scoreboard = KitEditorScoreboard.INSTANCE
        PlayerUtil.teleportNoGlitch(profile, bukkitLocation)
        profile.inventory.inventoryClickCancelled = false
        profile.inventory.clear()

        profile.inventory.setContents(gametype.kit.contents)
    }

    fun delete(loadoutSlot: Int) {
        val hash = (queuetype.id.toInt() shl 8 or gametype.id.toInt()).toShort()
        val kitLoadouts = profile.getCustomKits(
            queuetype, gametype, hash
        )

        kitLoadouts.remove(loadoutSlot)
        profile.customKits.put(hash, kitLoadouts)
        val config = playerConfig
        val path = (profile.name + ".KitData." + gametype.name + "."
                + queuetype.name + "." + loadoutSlot)

        config[path] = null

        config.save()

        ChatMessages.KIT_DELETED.send(profile.player)
    }
}
