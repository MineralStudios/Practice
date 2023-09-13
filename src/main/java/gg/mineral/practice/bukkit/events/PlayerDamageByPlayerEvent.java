package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import lombok.Getter;

public class PlayerDamageByPlayerEvent extends PlayerDamageEvent {

    @Getter
    private final Player damager;

    public PlayerDamageByPlayerEvent(Player damager, EntityDamageEvent entityDamageEvent) {
        super(entityDamageEvent);
        this.damager = damager;
    }

    public Player getActor() {
        return this.getDamager();
    }
}
