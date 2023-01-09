package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageByArrowEvent extends PlayerDamageEvent {

    private final Arrow damager;

    public PlayerDamageByArrowEvent(Arrow damager, EntityDamageEvent entityDamageEvent) {
        super(entityDamageEvent);
        this.damager = damager;
    }

    public Arrow getDamager() {
        return this.damager;
    }

    public Arrow getActor() {
        return this.getDamager();
    }
}
