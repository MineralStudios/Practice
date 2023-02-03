package gg.mineral.practice.inventory;

import org.bukkit.event.inventory.ClickType;

import gg.mineral.practice.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Interaction {
    @Getter
    final Profile profile;
    @Getter
    ClickType clickType;
}
