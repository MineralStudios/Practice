package gg.mineral.practice.listeners

import gg.mineral.practice.bukkit.events.PlayerDamageByPlayerEvent
import gg.mineral.practice.bukkit.events.PlayerDamageByProjectileEvent
import gg.mineral.practice.bukkit.events.PlayerDamageEvent
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.managers.ProfileManager.getProfile
import gg.mineral.practice.managers.ProfileManager.lobbyLocation
import gg.mineral.practice.match.TeamMatch
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.Bukkit
import org.bukkit.Instrument
import org.bukkit.Note
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustByEntityEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import java.util.function.Predicate

class DamageListener : Listener, CommandSenderAppender {
    @EventHandler
    fun onEntityDamage(e: EntityDamageEvent) {
        if (e.entity !is Player) return

        val event = if (e is EntityDamageByEntityEvent) {
            if (e.damager is Projectile) PlayerDamageByProjectileEvent(
                e.damager as Projectile, e
            )
            else if (e.damager is Player) PlayerDamageByPlayerEvent(
                e.damager as Player, e
            )
            else PlayerDamageEvent(e)
        } else PlayerDamageEvent(e)

        Bukkit.getPluginManager().callEvent(event)

        e.isCancelled = event.isCancelled
    }

    @EventHandler
    fun onPlayerDamage(e: PlayerDamageEvent) {
        val player = e.player

        val victim = getProfile(
            player.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (victim == null) {
            if (e.cause == DamageCause.VOID) player.teleport(lobbyLocation)

            e.isCancelled = true
            return
        }

        if (victim.inMatchCountdown || victim.match?.ended == true) {
            e.isCancelled = true
            return
        }

        if (e.cause == DamageCause.VOID || e.finalDamage >= player.health) {
            victim.match?.end(victim)
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamageByPlayer(e: PlayerDamageByPlayerEvent) {
        val attacker: Profile? = getProfile(
            e.damager.uniqueId
        ) { p -> p.playerStatus === PlayerStatus.FIGHTING }

        if (attacker == null || attacker.match?.ended == true) {
            e.isCancelled = true
            return
        }

        val victim = getProfile(
            e.player.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (victim == null) {
            e.isCancelled = true
            return
        }

        victim.killer = attacker

        if (victim.match?.ended == true) {
            e.isCancelled = true
            return
        }

        if (attacker.match is TeamMatch
            && (attacker.match as TeamMatch).getTeam(attacker, true).contains(victim)
        ) {
            e.isCancelled = true
            return
        }

        if (victim.player.noDamageTicks <= victim.player.maximumNoDamageTicks / 2.0f
            && attacker.match!!.incrementTeamHitCount(attacker, victim)
        ) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamageByProjectile(e: PlayerDamageByProjectileEvent) {
        val projectile = e.damager

        val shooter = projectile.shooter

        if (shooter !is Player) return

        val attacker: Profile? = getProfile(
            shooter.uniqueId,
            Predicate<Profile> { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING })

        if (attacker == null || attacker.match?.ended == true) {
            e.isCancelled = true
            return
        }

        val victim = getProfile(
            e.player.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (victim == null) {
            e.isCancelled = true
            return
        }

        if (attacker.match is TeamMatch
            && (attacker.match as TeamMatch).getTeam(attacker, true).contains(victim)
        ) {
            e.isCancelled = true
            return
        }

        if (projectile is Arrow) {
            val health = e.player.health.toInt()
            shooter.send(
                ChatMessages.HEALTH.clone().replace("%player%", e.player.name).replace("%health%", "" + health)
            )
            shooter.playNote(shooter.location, Instrument.PIANO, Note(20))
        }

        victim.killer = attacker
    }

    @EventHandler
    fun onEntityCombustByEntity(e: EntityCombustByEntityEvent) {
        val attacker = getProfile(
            e.combuster.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (attacker == null) {
            e.isCancelled = true
            return
        }

        val victim = getProfile(
            e.entity.uniqueId
        ) { p: Profile -> p.playerStatus === PlayerStatus.FIGHTING }

        if (victim == null) {
            e.isCancelled = true
            return
        }

        if (attacker.party != null) e.isCancelled = attacker.match!!.getTeam(attacker, true).contains(victim)
    }
}
