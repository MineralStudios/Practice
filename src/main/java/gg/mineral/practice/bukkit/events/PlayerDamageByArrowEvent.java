package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Arrow;
import org.bukkit.event.HandlerList;
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

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
