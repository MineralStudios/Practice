package ms.uk.eclipse.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;

public class DeathListener implements Listener {
    final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDropItems(false);
        e.setDeathMessage(null);
        Profile victim = playerManager.getProfileFromMatch(e.getEntity());

        if (victim == null) {
            return;
        }

        victim.getMatch().end(victim);
    }
}
