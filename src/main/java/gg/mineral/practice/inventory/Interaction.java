package gg.mineral.practice.inventory;

import org.bukkit.event.inventory.ClickType;
import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.practice.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Interaction {
    @NonNull
    private final Profile profile;
    @NonNull
    private ClickType clickType;
}
