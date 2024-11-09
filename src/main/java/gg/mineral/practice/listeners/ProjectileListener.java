package gg.mineral.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffectType;

import gg.mineral.api.event.PlayerThrowPearlEvent;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.val;

public class ProjectileListener implements Listener {
    @EventHandler
    public void onPotionSplash(final PotionSplashEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player))
            return;

        val shooter = (Player) e.getEntity().getShooter();

        for (val effect : e.getEntity().getEffects()) {
            if (!effect.getType().equals(PotionEffectType.HEAL))
                continue;

            for (val entity : e.getAffectedEntities()) {
                if (entity.getUniqueId().equals(shooter.getUniqueId()) || !(entity instanceof Player))
                    continue;

                val uuid = entity.getUniqueId();
                val profile = ProfileManager.getProfile(uuid);
                if (profile == null || profile.getPlayerStatus() != PlayerStatus.FIGHTING)
                    continue;
                val match = profile.getMatch();

                if (match == null || match.isEnded())
                    continue;

                match.stat(uuid, collector -> collector.stolenPotion());
            }

            val uuid = shooter.getUniqueId();
            val profile = ProfileManager.getProfile(uuid);
            if (profile == null || profile.getPlayerStatus() != PlayerStatus.FIGHTING)
                continue;
            val match = profile.getMatch();

            if (match == null || match.isEnded())
                continue;

            if (match == null || match.isEnded())
                continue;

            match.stat(uuid,
                    collector -> collector.thrownPotion(e.getIntensity(shooter) <= 0.5));
            break;
        }
    }

    @EventHandler
    public void onThrowPearl(final PlayerThrowPearlEvent e) {
        val player = e.getPlayer();
        val profile = ProfileManager.getProfile(player);

        if (profile != null && (profile.isInMatchCountdown() || profile.getPlayerStatus() != PlayerStatus.FIGHTING)) {
            e.setCancelled(true);
            return;
        }

        val uuid = player.getUniqueId();

        if (Match.getPearlCooldown().isActive(uuid)) {
            e.setCancelled(true);
            int timeRemaining = Match.getPearlCooldown().getTimeRemaining(uuid);
            ChatMessages.PEARL.clone().replace("%time%", "" + timeRemaining)
                    .send(player);
            return;
        }

        if (profile == null)
            return;

        val match = profile.getMatch();

        if (match == null)
            return;
        Match.getPearlCooldown().getCooldowns().put(uuid, match.getData().getPearlCooldown());
    }
}
