package ms.uk.eclipse.util;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerUtil {
    public static void teleport(CraftPlayer from, Location to) {
        from.getHandle().playerConnection.checkMovement = false;
        from.teleport(to);
    }

    public static void teleport(CraftPlayer from, Player to) {
        from.getHandle().playerConnection.checkMovement = false;
        from.teleport(to);
    }
}
