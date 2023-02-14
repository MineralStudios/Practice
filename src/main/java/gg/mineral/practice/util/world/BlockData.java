package gg.mineral.practice.util.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
public class BlockData {
    @Getter
    Location location;
    @Getter
    Material type;
    @Getter
    byte data;

    public void update(Player player) {
        player.sendBlockChange(location, type, data);
    }

    public void remove(Player player) {
        Block block = location.getBlock();
        player.sendBlockChange(location, block.getType(), block.getData());
    }
}
