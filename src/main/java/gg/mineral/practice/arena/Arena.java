package gg.mineral.practice.arena;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.server.world.Schematic;
import gg.mineral.server.world.SchematicFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Collections;

public class Arena implements SaveableData {
    final FileConfiguration config = ArenaManager.getConfig();
    @Getter
    String name, path, displayName;
    @Getter
    SchematicFile schematicFile;
    @Getter
    SpawnLocation location1, location2, waitingLocation;
    @Getter
    ItemStack displayItem;
    int currentNameID = 0;
    @Getter
    private final byte id;

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class SpawnLocation {
        private int x, y, z;
        private float yaw, pitch;

        public double getX() {
            return x + 0.5;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z + 0.5;
        }

        public Vector getDirection() {
            Vector vector = new Vector();
            double rotX = this.yaw;
            double rotY = this.pitch;
            vector.setY(-Math.sin(Math.toRadians(rotY)));
            double xz = Math.cos(Math.toRadians(rotY));
            vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
            vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
            return vector;
        }

        public void setDirection(Vector vector) {
            double x = vector.getX();
            double z = vector.getZ();
            if (x == (double) 0.0F && z == (double) 0.0F) {
                this.pitch = vector.getY() > (double) 0.0F ? -90.0F : 90.0F;
            } else {
                double theta = Math.atan2(-x, z);
                this.yaw = (float) Math.toDegrees((theta + (Math.PI * 2D)) % (Math.PI * 2D));
                double x2 = NumberConversions.square(x);
                double z2 = NumberConversions.square(z);
                double xz = Math.sqrt(x2 + z2);
                this.pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));
            }
        }

        public Location bukkit(World world) {
            return new Location(world, getX(), getY(), getZ(), yaw, pitch);
        }
    }

    public Arena(String name, byte id) {
        this.name = name;
        this.id = id;
        this.path = "Arena." + getName() + ".";
    }

    public void setLocation1(Location location1) {
        this.location1 = new SpawnLocation(location1.getBlockX(), location1.getBlockY(), location1.getBlockZ(), location1.getYaw(), location1.getPitch());
        save();
    }

    public void setLocation2(Location location2) {
        this.location2 = new SpawnLocation(location2.getBlockX(), location2.getBlockY(), location2.getBlockZ(), location2.getYaw(), location2.getPitch());
        save();
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        save();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        save();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Arena arena)
            return arena.getName().equalsIgnoreCase(getName());
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public void setWaitingLocation(Location waitingLocation) {
        this.waitingLocation = new SpawnLocation(waitingLocation.getBlockX(), waitingLocation.getBlockY(), waitingLocation.getBlockZ(), waitingLocation.getYaw(), waitingLocation.getPitch());
        save();
    }

    public World generate() {
        return schematicFile.generateWorld("_" + currentNameID++);
    }

    @Override
    public void save() {
        config.set(path + "Spawn.Waiting.x", waitingLocation.x());
        config.set(path + "Spawn.Waiting.y", waitingLocation.y());
        config.set(path + "Spawn.Waiting.z", waitingLocation.z());
        config.set(path + "DisplayName", name);
        config.set(path + "DisplayItem", displayItem);
        config.set(path + "Spawn.1.x", location1.x());
        config.set(path + "Spawn.1.y", location1.y());
        config.set(path + "Spawn.1.z", location1.z());
        config.set(path + "Spawn.2.x", location2.x());
        config.set(path + "Spawn.2.y", location2.y());
        config.set(path + "Spawn.2.z", location2.z());
        config.set(path + "Spawn.1.Direction", location1.getDirection());
        config.set(path + "Spawn.2.Direction", location2.getDirection());
        config.save();
    }

    @Override
    public void load() {
        this.location1 = new SpawnLocation(config.getInt(path + "Spawn.1.x", 0),
                config.getInt(path + "Spawn.1.y", 70), config.getInt(path + "Spawn.1.z", 0), 0, 0);
        this.location2 = new SpawnLocation(config.getInt(path + "Spawn.2.x", 0),
                config.getInt(path + "Spawn.2.y", 70), config.getInt(path + "Spawn.2.z", 0), 0, 0);
        location1.setDirection(config.getVector(path + "Spawn.1.Direction", null));
        location2.setDirection(config.getVector(path + "Spawn.2.Direction", null));
        this.waitingLocation = new SpawnLocation(config.getInt(path + "Spawn.Waiting.x", 0),
                config.getInt(path + "Spawn.Waiting.y", 70), config.getInt(path + "Spawn.Waiting.z", 0), 0, 0);
        this.displayItem = config.getItemstack(path + "DisplayItem", ItemStacks.DEFAULT_ARENA_DISPLAY_ITEM);
        this.displayName = config.getString(path + "DisplayName", getName());

        this.schematicFile = Schematic.get(this.name);
        if (schematicFile == null) {
            Bukkit.getLogger().warning("Schematic file not found for arena " + getName());
            this.schematicFile = new SchematicFile(new File("", this.name + ".schematic"), (short) 0, (short) 0, (short) 0, Collections.emptyList());
        }
    }

    @Override
    public void setDefaults() {
        this.schematicFile = new SchematicFile(new File("", this.name + ".schematic"), (short) 0, (short) 0, (short) 0, Collections.emptyList());
        this.location1 = new SpawnLocation(0,
                70, 0, 0, 0);
        this.location2 = new SpawnLocation(0,
                70, 0, 0, 0);
        this.waitingLocation = new SpawnLocation(0,
                70, 0, 0, 0);
        this.displayItem = ItemStacks.DEFAULT_ARENA_DISPLAY_ITEM;
        this.displayName = getName();
    }

    @Override
    public void delete() {
        config.remove("Arena." + getName());
        config.save();
    }
}
