package gg.mineral.practice.bukkit.events

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class PlayerDamageByPlayerEvent(val damager: Player, entityDamageEvent: EntityDamageEvent) :
    PlayerDamageEvent(entityDamageEvent)
