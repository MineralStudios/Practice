package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageEvent;

import lombok.Getter;

public class PlayerDamageByArrowEvent extends PlayerDamageEvent {

    @Getter
    private final Arrow damager;

    public PlayerDamageByArrowEvent(Arrow damager, EntityDamageEvent entityDamageEvent) {
        super(entityDamageEvent);
        this.damager = damager;
    }

    public Arrow getActor() {
        return this.getDamager();
    }
}
