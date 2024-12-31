package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.category.Category;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import lombok.val;

@ClickCancelled(true)
public class SelectCategorizedExistingKitMenu extends SelectExistingKitMenu {
    private final Category category;

    public SelectCategorizedExistingKitMenu(Category category, PracticeMenu menu, boolean simple) {
        super(menu, simple);
        this.category = category;
    }

    @Override
    public String getTitle() {
        return CC.BLUE + category.getName();
    }

    @Override
    public void update() {
        for (val g : category.getGametypes()) {
            val item = new ItemBuilder(g.getDisplayItem().clone())
                    .name(CC.SECONDARY + CC.B + g.getDisplayName()).lore(CC.ACCENT + "Click to select.").build();
            add(item, interaction -> {
                if (viewer.getPlayerStatus() == PlayerStatus.KIT_CREATOR) {
                    viewer.giveKit(g.getKit());
                    return;
                }

                if (simple)
                    viewer.getDuelSettings().setGametype(g);
                else
                    viewer.getDuelSettings().setKit(g.getKit());

                viewer.openMenu(menu);
            });
        }
    }
}
