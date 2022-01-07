package ms.uk.eclipse.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class DeathListener implements Listener {
    final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDropItems(false);
        e.setDeathMessage(null);
        Profile victim = playerManager.getProfile(e.getEntity());

        if (victim.getPlayerStatus() != PlayerStatus.FIGHTING) {
            Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
                public void run() {
                    victim.bukkit().getHandle().playerConnection
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
