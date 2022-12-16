package gg.mineral.practice.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;

public class MovementListener implements Listener {

    @EventHandler
    public void onPearlTeleport(PlayerTeleportEvent event) {

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        Location pearlLocation = event.getTo();
        Location playerLocation = event.getFrom();

        if (playerLocation.getBlockY() < pearlLocation.getBlockY()) {
            Block block = pearlLocation.getBlock();

            for (BlockFace face : BlockFace.values()) {
                Material type = block.getRelative(face).getType();

                if (type == Material.GLASS) {
                    pearlLocation.setY(pearlLocation.getBlockY());
                    pearlLocation.setX(pearlLocation.getBlockX());
                    pearlLocation.setZ(pearlLocation.getBlockZ());
                    break;
                }
            }
        }

        event.setTo(pearlLocation);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Profile player = ProfileManager.getProfile(
                p -> p.getUUID().equals(e.getPlayer().getUniqueId()) && p.getPlayerStatus() == PlayerStatus.FIGHTING);

        if (player == null) {
            return;
        }

        if (player.getMatch().getData().getDeadlyWater()) {
            Material type = player.getPlayer().getLocation().getBlock().getType();
            if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                player.getMatch().end(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent e) {
        Profile player = ProfileManager.getProfile(
                p -> p.getUUID().equals(e.getEntity().getUniqueId()) && p.getPlayerStatus() == PlayerStatus.FIGHTING);

        if (player == null) {
            return;
        }

        if (player.isInMatchCountdown()) {
            e.setCancelled(true);
        }
    }
}
