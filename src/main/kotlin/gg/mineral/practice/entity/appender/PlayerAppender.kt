package gg.mineral.practice.entity.appender

import com.github.retrooper.packetevents.PacketEvents
import gg.mineral.bot.api.BotAPI
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

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

    fun Player.isDay(): Boolean {
        val time = this.playerTimeOffset % 24000
        return time in 0..12299
    }

    fun Player.isNight() = !isDay()

    fun Player.setKnockbackSync(enabled: Boolean) {
        try {
            val hasPlayerData = me.caseload.knockbacksync.manager.PlayerDataManager.containsPlayerData(this.uniqueId)
            if (hasPlayerData && !enabled) {
                if (me.caseload.knockbacksync.manager.CombatManager.getPlayers()
                        .contains(this.uniqueId)
                ) me.caseload.knockbacksync.manager.CombatManager.removePlayer(this.uniqueId)
                me.caseload.knockbacksync.manager.PlayerDataManager.removePlayerData(this.uniqueId)
            } else if (!hasPlayerData && enabled) {
                me.caseload.knockbacksync.manager.PlayerDataManager.addPlayerData(
                    this.uniqueId,
                    me.caseload.knockbacksync.player.PlayerData(
                        me.caseload.knockbacksync.Base.INSTANCE.platformServer.getPlayer(
                            this.uniqueId
                        )
                    )
                )
            }
        } catch (ignored: Exception) {
            Bukkit.getLogger().warning("Failed to set knockback sync for ${this.name}")
        }
    }

    fun Player.getPing(): Int {
        try {
            val hasPlayerData = me.caseload.knockbacksync.manager.PlayerDataManager.containsPlayerData(this.uniqueId)
            if (hasPlayerData) {
                val playerData = me.caseload.knockbacksync.manager.PlayerDataManager.getPlayerData(this.uniqueId)
                if (playerData != null) return playerData.notNullPing.toInt()
            } else return PacketEvents.getAPI().playerManager.getPing(this)
        } catch (ignored: Exception) {
        }
        return PacketEvents.getAPI().playerManager.getPing(this)
    }

    fun Player.setBacktrack(enabled: Boolean) {
        try {
            (this as CraftPlayer).handle.backtrackSystem.isEnabled = enabled
        } catch (ignored: Exception) {
            Bukkit.getLogger().warning("Failed to set backtrack for ${this.name}")
        }
    }

    fun Player.kill() {
        val ede = EntityDamageEvent(this, EntityDamageEvent.DamageCause.SUICIDE, 1000)
        Bukkit.getPluginManager().callEvent(ede)
        if (ede.isCancelled) return

        ede.entity.lastDamageCause = ede

        this.health = 0.0
    }
}