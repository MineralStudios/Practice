package gg.mineral.practice.listeners;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.managers.ProfileManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class MovementListener implements Listener {

    @Data
    @AllArgsConstructor
    private static class BoundingBox {
        private double minX, minY, minZ, maxX, maxY, maxZ;

        public double getCenterX() {
            return (minX + maxX) / 2;
        }

        public double getCenterY() {
            return (minY + maxY) / 2;
        }

        public double getCenterZ() {
            return (minZ + maxZ) / 2;
        }

        public void shift(double x, double y, double z) {
            minX += x;
            minY += y;
            minZ += z;
            maxX += x;
            maxY += y;
            maxZ += z;
        }
    }

    @EventHandler
    public void onPearlTeleport(PlayerTeleportEvent event) {

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            val profile = ProfileManager.getProfile(
                    event.getPlayer().getUniqueId());

            if (profile == null)
                return;

            profile.updateVisibility();
            return;
        }

        val player = event.getPlayer();

        val pearlLandingLocation = event.getTo();
        double patchedPearlYLocation = Math.rint(pearlLandingLocation.getY());
        pearlLandingLocation.setY(patchedPearlYLocation);
        event.setTo(pearlLandingLocation);

        val playerLoc = player.getLocation();
        // Create a bounding box with and set its center at the pearl landing location
        val playerHitBox = new BoundingBox(playerLoc.getX() - 0.3, playerLoc.getY(),
                playerLoc.getZ() - 0.3, playerLoc.getX() + 0.3, playerLoc.getY() + 1.8,
                playerLoc.getZ() + 0.3);
        // Subtract the original bounding box coordinates to set them to zero. Then add
        // the new bounding box coordinates to match the pearl land location.
        playerHitBox.shift(-playerHitBox.getCenterX() + pearlLandingLocation.getX(),
                -playerHitBox.getCenterY() + pearlLandingLocation.getY(),
                -playerHitBox.getCenterZ() + pearlLandingLocation.getZ());

        val pearlLandingBlock = pearlLandingLocation.getBlock();
        val pearlLandingBlockNorth = pearlLandingBlock.getRelative(BlockFace.NORTH);
        val pearlLandingBlockSouth = pearlLandingBlock.getRelative(BlockFace.SOUTH);
        val pearlLandingBlockEast = pearlLandingBlock.getRelative(BlockFace.EAST);
        val pearlLandingBlockWest = pearlLandingBlock.getRelative(BlockFace.WEST);

        // Patch carpets.
        if (pearlLandingBlock.getType().name().contains("CARPET")) {
            pearlLandingLocation.setY(pearlLandingLocation.getY() + 0.08);
            playerHitBox.shift(0.0, 0.08, 0.0);
        }

        val pearlLandingBlock1 = pearlLandingLocation.clone().add(0, 1, 0).getBlock();
        val pearlLandingBlockNorth1 = pearlLandingBlock1.getRelative(BlockFace.NORTH);
        val pearlLandingBlockSouth1 = pearlLandingBlock1.getRelative(BlockFace.SOUTH);
        val pearlLandingBlockEast1 = pearlLandingBlock1.getRelative(BlockFace.EAST);
        val pearlLandingBlockWest1 = pearlLandingBlock1.getRelative(BlockFace.WEST);

        // If the pearl lands somewhere without any blocks adjacent to the landing spot
        // then return.
        boolean bottomRowNotAir = pearlLandingBlockNorth.getType() != Material.AIR ||
                pearlLandingBlockSouth.getType() != Material.AIR ||
                pearlLandingBlockEast.getType() != Material.AIR ||
                pearlLandingBlockWest.getType() != Material.AIR;

        boolean topRowNotAir = pearlLandingBlockNorth1.getType() != Material.AIR ||
                pearlLandingBlockSouth1.getType() != Material.AIR ||
                pearlLandingBlockEast1.getType() != Material.AIR ||
                pearlLandingBlockWest1.getType() != Material.AIR;

        if (bottomRowNotAir && topRowNotAir) {

            pearlLandingLocation.setX(pearlLandingBlock.getX() + 0.5);
            pearlLandingLocation.setZ(pearlLandingBlock.getZ() + 0.5);

            event.setTo(pearlLandingLocation);
        }
    }

    // Implemented getBoundingBox method for Bukkit 1.8
    public BoundingBox getBoundingBox(Block block) {
        try {
            // Get the NMS World
            val world = ((CraftWorld) block.getWorld()).getHandle();
            // Get the block position
            val blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
            // Get the NMS IBlockData (the block state)
            val blockData = world.getType(blockPosition);
            // Get the NMS Block
            val nmsBlock = blockData.getBlock();
            // Get the AxisAlignedBB
            val aabb = nmsBlock.a(world, blockPosition, blockData);
            // If null, return empty bounding box
            if (aabb == null)
                return new BoundingBox(0, 0, 0, 0, 0, 0);

            // Convert the AxisAlignedBB to our BoundingBox class
            return new BoundingBox(
                    aabb.a, // minX
                    aabb.b, // minY
                    aabb.c, // minZ
                    aabb.d, // maxX
                    aabb.e, // maxY
                    aabb.f // maxZ
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new BoundingBox(0, 0, 0, 0, 0, 0);
        }
    }

    // Added isPassable method
    public boolean isPassable(Block block) {
        try {
            // Get the NMS World
            val world = ((CraftWorld) block.getWorld()).getHandle();
            // Get the block position
            val blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
            // Get the NMS IBlockData (the block state)
            val blockData = world.getType(blockPosition);
            // Get the NMS Block
            val nmsBlock = blockData.getBlock();
            // Use the NMS method to check if the block is passable
            return !nmsBlock.getMaterial().isSolid();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        val profile = ProfileManager.getProfile(
                e.getPlayer().getUniqueId(), p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

        if (profile == null)
            return;

        if (profile.getMatch().getData().isDeadlyWater()) {
            val type = profile.getPlayer().getLocation().getBlock().getType();
            if (type == Material.WATER || type == Material.STATIONARY_WATER)
                profile.getMatch().end(profile);
        }
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent e) {
        val profile = ProfileManager.getProfile(
                e.getEntity().getUniqueId(), p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

        if (profile == null)
            return;

        if (profile.isInMatchCountdown())
            e.setCancelled(true);

    }
}
