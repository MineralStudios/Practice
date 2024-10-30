package gg.mineral.practice.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffectType;

import gg.mineral.api.event.PlayerThrowPearlEvent;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.val;

public class ProjectileListener implements Listener {
    @EventHandler
    public void onPotionSplash(final PotionSplashEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player))
            return;

        val shooter = (Player) e.getEntity().getShooter();
        val shooterProfile = ProfileManager.getProfile(shooter.getUniqueId());

        if (shooterProfile == null)
            return;

        for (val effect : e.getEntity().getEffects()) {
            if (!effect.getType().equals(PotionEffectType.HEAL))
                continue;

            for (val entity : e.getAffectedEntities()) {
                if (entity.getUniqueId().equals(shooter.getUniqueId()) || !(entity instanceof Player))
                    continue;

                val entityProfile = ProfileManager.getProfile(entity.getUniqueId());

                if (entityProfile == null)
                    continue;

                entityProfile.getMatchStatisticCollector().stolenPotion();
            }

            shooterProfile.getMatchStatisticCollector().thrownPotion(e.getIntensity((LivingEntity) shooter) <= 0.5);
            break;
        }
    }

    @EventHandler
    public void onThrowPearl(final PlayerThrowPearlEvent e) {
        val profile = ProfileManager.getOrCreateProfile(e.getPlayer());

        if (profile.isInMatchCountdown() || profile.getPlayerStatus() != PlayerStatus.FIGHTING) {
            e.setCancelled(true);
            return;
        }

        if (profile.getPearlCooldown().isActive()) {
            e.setCancelled(true);
            ChatMessages.PEARL.clone().replace("%time%", "" + profile.getPearlCooldown().getTimeRemaining())
                    .send(profile.getPlayer());
            return;
        }

        profile.getPearlCooldown().setTimeRemaining(profile.getMatch().getData().getPearlCooldown());
    }
}
