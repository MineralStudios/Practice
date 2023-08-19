package gg.mineral.practice.inventory.menus;

import java.util.function.Consumer;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.AnvilMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class ConfigureDifficultyValueMenu extends AnvilMenu {
    CustomBotDifficultyMenu menu;
    Consumer<Float> value;

    public ConfigureDifficultyValueMenu(CustomBotDifficultyMenu menu, Consumer<Float> value) {
        setClickCancelled(true);
        this.menu = menu;
        this.value = value;
    }

    @Override
    public boolean update() {

        setSlot(1, ItemStacks.APPLY, interaction -> {
            Profile p = interaction.getProfile();

            try {
                value.accept(Float.valueOf(getText().replace(" ", "")));
            } catch (NumberFormatException e) {
                p.message(ErrorMessages.INVALID_NUMBER);
                return;
            }

            p.openMenu(menu);
        });

        setSlot(0, ItemStacks.CANCEL, interaction -> {
            Profile p = interaction.getProfile();
            p.openMenu(menu);
        });

        return true;
    }
}
