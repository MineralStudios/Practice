package gg.mineral.practice.listeners

import gg.mineral.api.event.PlayerThrowPearlEvent
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.managers.ProfileManager.getProfile
import gg.mineral.practice.match.Match
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.potion.PotionEffectType

class ProjectileListener : Listener {
    @EventHandler
    fun onPotionSplash(e: PotionSplashEvent) {
        if (e.entity.shooter !is Player) return

        val shooter = e.entity.shooter as Player

        for (effect in e.entity.effects) {
            if (effect.type != PotionEffectType.HEAL) continue

            for (entity in e.affectedEntities) {
                if (entity.uniqueId == shooter.uniqueId || entity !is Player) continue

                val uuid = entity.getUniqueId()
                val profile = getProfile(uuid) ?: continue

                profile.match?.let { match ->
                    if (!match.ended) match.stat(
                        uuid
                    ) { it.stolenPotion() }
                }
            }

            val uuid = shooter.uniqueId
            val profile = getProfile(uuid) ?: continue

            profile.match?.let { match ->
                if (!match.ended) match.stat(
                    uuid
                ) { it.thrownPotion(e.getIntensity(shooter) <= 0.5) }
            }
            break
        }
    }

    @EventHandler
    fun onThrowPearl(e: PlayerThrowPearlEvent) {
        val player = e.player
        val profile = getProfile(player)

        if (profile != null && (profile.inMatchCountdown || profile.playerStatus !== PlayerStatus.FIGHTING)) {
            e.isCancelled = true
            return
        }

        val uuid = player.uniqueId

        if (Match.pearlCooldown.isActive(uuid)) {
            e.isCancelled = true
            val timeRemaining = Match.pearlCooldown.getTimeRemaining(uuid)
            ChatMessages.PEARL.clone().replace("%time%", "" + timeRemaining)
                .send(player)
            return
        }

        if (profile == null) return

        val match = profile.match ?: return

        Match.pearlCooldown.cooldowns.put(uuid, match.data.pearlCooldown)
    }
}
