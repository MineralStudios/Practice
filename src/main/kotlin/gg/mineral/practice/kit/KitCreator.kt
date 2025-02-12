package gg.mineral.practice.kit

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.SubmitAction
import gg.mineral.practice.managers.KitEditorManager.bukkitLocation
import gg.mineral.practice.scoreboard.impl.KitCreatorScoreboard
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.messages.impl.ChatMessages

class KitCreator(val profile: Profile, val submitAction: SubmitAction) {
    fun save() {
        profile.duelSettings
            .kit = Kit(profile.inventory.contents, profile.inventory.armorContents)
        profile.player.closeInventory()
        ChatMessages.KIT_SAVED.send(profile.player)
    }

    fun start() {
        profile.playerStatus = PlayerStatus.KIT_CREATOR
        profile.scoreboard = KitCreatorScoreboard.INSTANCE
        PlayerUtil.teleportNoGlitch(profile, bukkitLocation)
        profile.inventory.inventoryClickCancelled = false
        profile.inventory.clear()
    }
}
