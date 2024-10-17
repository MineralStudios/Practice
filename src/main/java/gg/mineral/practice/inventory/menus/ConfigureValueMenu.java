package gg.mineral.practice.inventory.menus;

import java.util.function.Consumer;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.AnvilMenu;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Menu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ConfigureValueMenu<T> extends AnvilMenu {
    private final Menu menu;
    private final Consumer<T> value;
    private final Class<T> type;

    @Override
    @SuppressWarnings("unchecked")
    public void update() {

        setSlot(1, ItemStacks.APPLY, interaction -> {
            Profile p = interaction.getProfile();

            String text = getText();

            if (text == null) {
                p.message(ErrorMessages.INVALID_NUMBER);
                return;
            }

            try {
                if (type.equals(Double.class) || type.equals(double.class))
                    value.accept((T) Double.valueOf(text.replace(" ", "")));
                else if (type.equals(Float.class) || type.equals(float.class))
                    value.accept((T) Float.valueOf(text.replace(" ", "")));
                else if (type.equals(Integer.class) || type.equals(int.class))
                    value.accept((T) Integer.valueOf(text.replace(" ", "")));
                else if (type.equals(Long.class) || type.equals(long.class))
                    value.accept((T) Long.valueOf(text.replace(" ", "")));
                else if (type.equals(Short.class) || type.equals(short.class))
                    value.accept((T) Short.valueOf(text.replace(" ", "")));
                else if (type.equals(Byte.class) || type.equals(byte.class))
                    value.accept((T) Byte.valueOf(text.replace(" ", "")));
                else
                    throw new IllegalArgumentException("Unsupported type: " + type.getTypeName());
            } catch (NumberFormatException e) {
                p.message(ErrorMessages.INVALID_NUMBER);
                return;
            }
            p.openMenu(menu);
        });

        setSlot(0, ItemStacks.CANCEL, interaction -> interaction.getProfile().openMenu(menu));
    }

    public static <T> ConfigureValueMenu<T> of(Menu menu, Consumer<T> value, Class<T> type) {
        return new ConfigureValueMenu<>(menu, value, type);
    }
}
