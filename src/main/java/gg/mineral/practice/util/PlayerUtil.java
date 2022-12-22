package gg.mineral.practice.util;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerUtil {
    public static void teleport(CraftPlayer from, Location to) {
        from.getHandle().playerConnection.checkMovement = false;
        double newY = Math.ceil(to.getY());
        to.setY(newY);
        from.teleport(to);
    }

    public static void teleport(CraftPlayer from, Player to) {
        teleport(from, to.getLocation());
    }
}
