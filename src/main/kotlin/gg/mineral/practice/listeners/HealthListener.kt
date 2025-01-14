package gg.mineral.practice.listeners

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.getProfile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import org.bukkit.event.entity.FoodLevelChangeEvent

class HealthListener : Listener {
    @EventHandler
    fun onEntityRegainHealth(e: EntityRegainHealthEvent) {
        val profile = getProfile(
            e.entity.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (profile == null) return

        if (profile.match?.data?.regeneration == false) if (e.regainReason == RegainReason.SATIATED || e.regainReason == RegainReason.REGEN) e.isCancelled =
            true
    }

    @EventHandler
    fun onFoodLevelChange(e: FoodLevelChangeEvent) {
        val profile = getProfile(
            e.entity.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (profile == null) {
            e.isCancelled = true
            return
        }

        if (profile.inMatchCountdown) {
            e.isCancelled = true
            return
        }

        e.isCancelled = profile.match?.data?.hunger == false
    }
}
