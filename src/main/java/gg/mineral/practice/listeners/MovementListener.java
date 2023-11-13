package gg.mineral.practice.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
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
        Location fromLocation = event.getFrom();

        pearlLocation.setY((int) (pearlLocation.getY() + 0.5));
        pearlLocation.setX(pearlLocation.getBlockX() + 0.5);
        pearlLocation.setZ(pearlLocation.getBlockZ() + 0.5);

        if (fromLocation.distanceSquared(pearlLocation) <= 2.25) {
            event.setTo(fromLocation);
            return;
        }

        if (isInsideBlock(pearlLocation)) {
            // Find the nearest safe location in the direction from where the pearl was
            // thrown
            Location safeLocation = findNearestSafeLocation(pearlLocation, fromLocation);
            if (safeLocation != null) {
                event.setTo(safeLocation);
            } else {
                event.setTo(fromLocation); // Reset to the original location if no safe spot found
            }
            return;
        }

        event.setTo(pearlLocation);
    }

    private Location findNearestSafeLocation(Location to, Location from) {
        Vector direction = to.toVector().subtract(from.toVector()).normalize();
        Location currentLocation = to.clone();
        int safetyCounter = 100;

        while (safetyCounter-- > 0) {
            currentLocation.add(direction.multiply(-0.5));
            currentLocation.setY(Math.round(currentLocation.getY() + 0.5));
            if (!isInsideBlock(currentLocation) && hasBlockBelow(currentLocation, 10))
                return currentLocation;

        }

        return null;
    }

    private boolean hasBlockBelow(Location loc, int depth) {
        Location checkLoc = loc.clone();
        for (int i = 0; i < depth; i++) {
            checkLoc.subtract(0, 1, 0);
            if (checkLoc.getBlock().getType() != Material.AIR)
                return true;

        }
        return false;
    }

    private boolean isInsideBlock(Location loc) {
        World world = loc.getWorld();

        // Get the four corners of the player's bounding box
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        // The following offsets are for the player's bounding box. You might need to
        // adjust based on the exact size you're considering.
        double halfWidth = 0.3; // Half the player's width
        double height = 1.8; // Player's height

        Location[] corners = new Location[] {
                new Location(world, x + halfWidth, y, z + halfWidth),
                new Location(world, x + halfWidth, y, z - halfWidth),
                new Location(world, x - halfWidth, y, z + halfWidth),
                new Location(world, x - halfWidth, y, z - halfWidth),
                new Location(world, x + halfWidth, y + height, z + halfWidth),
                new Location(world, x + halfWidth, y + height, z - halfWidth),
                new Location(world, x - halfWidth, y + height, z + halfWidth),
                new Location(world, x - halfWidth, y + height, z - halfWidth)
        };

        for (Location corner : corners) {
            if (corner.getBlock().getType() != Material.AIR) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        Profile profile = ProfileManager.getProfile(
                p -> p.getUuid().equals(e.getPlayer().getUniqueId()) && p.getPlayerStatus() == PlayerStatus.FIGHTING);

        if (profile == null) {
            return;
        }

        if (profile.getMatch().getData().getDeadlyWater()) {
            Material type = profile.getPlayer().getLocation().getBlock().getType();
            if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                profile.getMatch().end(profile);
            }
        }
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent e) {
        Profile profile = ProfileManager.getProfile(
                p -> p.getUuid().equals(e.getEntity().getUniqueId()) && p.getPlayerStatus() == PlayerStatus.FIGHTING);

        if (profile == null) {
            return;
        }

        if (profile.isInMatchCountdown()) {
            e.setCancelled(true);
        }
    }
}
