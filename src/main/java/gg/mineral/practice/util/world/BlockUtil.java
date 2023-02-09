package gg.mineral.practice.util.world;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import gg.mineral.practice.entity.Profile;

public class BlockUtil {
    public static void sendFakeBlock(Profile profile, Location location, Material material) {
        profile.getPlayer().sendBlockChange(location, material, (byte) 0);
        profile.getFakeBlockLocations().add(location);
    }

    public static void clearFakeBlocks(Profile profile) {
        Iterator<Location> iter = profile.getFakeBlockLocations().iterator();

        while (iter.hasNext()) {
            Location location = iter.next();
            Block block = location.getBlock();
            profile.getPlayer().sendBlockChange(location, block.getType(), block.getData());
            iter.remove();
        }
    }
}
