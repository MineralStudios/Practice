package gg.mineral.practice.util.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BlockData {
    private final Location location;
    private Material type;
    private byte data;

    public BlockData clone() {
        return new BlockData(location.clone(), type, data);
    }

    public BlockData setType(Material type) {
        this.type = type;
        return this;
    }

    public BlockData translate(int x, int y, int z) {
        location.add(x, y, z);
        return this;
    }

    @SuppressWarnings("deprecation")
    public void update(Player player) {
        player.sendBlockChange(location, type, data);
    }

    @SuppressWarnings("deprecation")
    public void remove(Player player) {
        Block block = location.getBlock();
        player.sendBlockChange(location, block.getType(), block.getData());
    }
}
