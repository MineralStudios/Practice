package gg.mineral.practice.entity.appender

import gg.mineral.bot.api.BotAPI
import org.bukkit.entity.Player

interface PlayerAppender {
    fun Player.heal() {
        this.foodLevel = 20
        this.health = 20.0
        this.saturation = 20f
        this.fireTicks = 0
    }

    fun Player.removePotionEffects() = this.activePotionEffects.forEach { this.removePotionEffect(it.type) }

    fun Player.isFake() =
        BotAPI.INSTANCE.isFakePlayer(this.uniqueId)
}