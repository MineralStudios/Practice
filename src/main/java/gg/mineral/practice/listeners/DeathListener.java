package gg.mineral.practice.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import gg.mineral.practice.util.messages.CC;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDropItems(false);
        e.setDeathMessage(null);
        e.getEntity().kickPlayer(CC.RED + "Player death is not allowed.");
    }
}
