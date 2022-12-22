package gg.mineral.practice.util;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerUtil {
    public static void teleport(CraftPlayer from, Location to) {
        from.getHandle().playerConnection.checkMovement = false;
        to.add(to, 0, 0.5, 0);
        from.teleport(to);
    }

    public static void teleport(CraftPlayer from, Player to) {
        teleport(from, to.getLocation());
    }
}
