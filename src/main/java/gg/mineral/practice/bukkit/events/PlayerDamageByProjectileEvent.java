package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

import lombok.Getter;

public class PlayerDamageByProjectileEvent extends PlayerDamageEvent {

    @Getter
    private final Projectile damager;

    public PlayerDamageByProjectileEvent(Projectile damager, EntityDamageEvent entityDamageEvent) {
        super(entityDamageEvent);
        this.damager = damager;
    }

    public Projectile getActor() {
        return this.getDamager();
    }
}
