package gg.mineral.practice.util.world;

import org.bukkit.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BlockData {
    @Getter
    Material type;
    @Getter
    byte data;
}
