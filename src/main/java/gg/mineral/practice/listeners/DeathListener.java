package gg.mineral.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDropItems(false);
        e.setDeathMessage(null);
        Profile victim = ProfileManager.getOrCreateProfile(e.getEntity());

        if (victim.getPlayerStatus() != PlayerStatus.FIGHTING) {
            Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
                public void run() {
                    victim.getPlayer().getHandle().playerConnection
                            .a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
                    victim.heal();
                    victim.removePotionEffects();
                    victim.teleportToLobby();
                    if (victim.isInParty()) {
                        victim.setInventoryForParty();
                    } else {
                        victim.setInventoryForLobby();
                    }
                }
            }, 1);
            return;
        }

        victim.getMatch().end(victim);
    }
}
