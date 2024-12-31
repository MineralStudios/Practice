package gg.mineral.practice.bukkit.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter
public class PlayerDamageByPlayerEvent extends PlayerDamageEvent {

    private final Player damager;

    public PlayerDamageByPlayerEvent(Player damager, EntityDamageEvent entityDamageEvent) {
        super(entityDamageEvent);
        this.damager = damager;
    }

    public Player getActor() {
        return this.getDamager();
    }
}
