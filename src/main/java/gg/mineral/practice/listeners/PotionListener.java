package gg.mineral.practice.listeners;

import java.util.Collection;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;

public class PotionListener implements Listener {
    @EventHandler
    public void onPotionSplash(final PotionSplashEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        for (final PotionEffect effect : e.getEntity().getEffects()) {
            if (!effect.getType().equals(PotionEffectType.HEAL)) {
                continue;
            }

            final Player shooter = (Player) e.getEntity().getShooter();
            final Profile shooterProfile = ProfileManager.getProfile(p -> p.getUUID().equals(shooter.getUniqueId()));

            for (LivingEntity entity : e.getAffectedEntities()) {
                if (entity.getUniqueId().equals(shooter.getUniqueId())) {
                    continue;
                }

                if (!(entity instanceof Player)) {
                    return;
                }

                Profile entityProfile = ProfileManager.getProfile(p -> p.getUUID().equals(entity.getUniqueId()));
                entityProfile.stolenPotion();
            }

            shooterProfile.thrownPotion(e.getIntensity((LivingEntity) shooter) <= 0.5);
            break;
        }
    }
}
