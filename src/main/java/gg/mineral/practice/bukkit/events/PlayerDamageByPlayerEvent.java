package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageByPlayerEvent extends PlayerDamageEvent {

    private final Player damager;

    public PlayerDamageByPlayerEvent(Player damager, EntityDamageEvent entityDamageEvent) {
        super(entityDamageEvent);
        this.damager = damager;
    }

    public Player getDamager() {
        return this.damager;
    }

    public Player getActor() {
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
