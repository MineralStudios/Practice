package gg.mineral.practice.listeners;

import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;

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
    public void onPlayerMove(PlayerMoveEvent e) throws SQLException {
        Profile profile = PlayerManager
                .get(p -> p.getUUID().equals(e.getPlayer().getUniqueId())
                        && p.getPlayerStatus() == PlayerStatus.FIGHTING);

        if (profile == null) {
            return;
        }

        if (profile.getMatch().getData().getDeadlyWater()) {
            Material type = profile.bukkit().getLocation().getBlock().getType();
            if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                profile.getMatch().end(profile);
            }
        }
    }

    @EventHandler
    public void onPlayerDismount(VehicleExitEvent e) {
        Profile profile = PlayerManager
                .get(p -> p.getUUID().equals(e.getExited().getUniqueId())
                        && p.getPlayerStatus() == PlayerStatus.FIGHTING);

        if (profile == null) {
            return;
        }

        if (profile.isInMatchCountdown()) {
            e.setCancelled(true);
        }
    }
}
