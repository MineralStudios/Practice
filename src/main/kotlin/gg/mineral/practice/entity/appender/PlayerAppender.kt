package gg.mineral.practice.entity.appender

import org.bukkit.entity.Player

interface PlayerAppender {
    fun Player.heal() {
        this.foodLevel = 20
        this.health = 20.0
        this.saturation = 20f
        this.fireTicks = 0
    }

    fun Player.removePotionEffects() = this.activePotionEffects.forEach { this.removePotionEffect(it.type) }
}