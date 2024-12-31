package gg.mineral.practice.util;

import gg.mineral.practice.entity.Profile;
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

    public static void teleportNoGlitch(CraftPlayer from, Location to) {
        from.getHandle().playerConnection.checkMovement = false;
        double newY = Math.ceil(to.getY());
        to.setY(newY + 0.5);
        from.teleport(to);
    }

    public static void teleport(Profile from, Location to) {
        teleport(from.getPlayer(), to);
    }

    public static void teleportNoGlitch(Profile from, Location to) {
        teleportNoGlitch(from.getPlayer(), to);
    }

    public static void teleport(CraftPlayer from, Player to) {
        teleport(from, to.getLocation());
    }
}
