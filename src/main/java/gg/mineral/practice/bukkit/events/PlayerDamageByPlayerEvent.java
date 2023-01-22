package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Player;
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
}
