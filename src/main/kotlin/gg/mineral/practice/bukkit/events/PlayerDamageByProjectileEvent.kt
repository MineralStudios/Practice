package gg.mineral.practice.bukkit.events

import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageEvent

class PlayerDamageByProjectileEvent(val damager: Projectile, entityDamageEvent: EntityDamageEvent) :
    PlayerDamageEvent(entityDamageEvent)
