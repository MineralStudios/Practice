package gg.mineral.practice.bukkit.events;

import lombok.Getter;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter
public class PlayerDamageByProjectileEvent extends PlayerDamageEvent {

    private final Projectile damager;

    public PlayerDamageByProjectileEvent(Projectile damager, EntityDamageEvent entityDamageEvent) {
        super(entityDamageEvent);
        this.damager = damager;
    }

    public Projectile getActor() {
        return this.getDamager();
    }
}
