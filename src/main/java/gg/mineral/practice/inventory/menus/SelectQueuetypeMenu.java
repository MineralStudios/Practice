package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectQueuetypeMenu extends PracticeMenu {

    private final SelectGametypeMenu.Type type;

    @Override
    public void update() {
        clear();
        for (val q : QueuetypeManager.getQueuetypes().values())
            add(new ItemBuilder(q.getDisplayItem().clone())
                    .name(CC.SECONDARY + CC.B + q.getDisplayName())
                    .lore(CC.ACCENT + (type == SelectGametypeMenu.Type.KIT_EDITOR ? "Click to edit kit."
                            : "Click to select."))
                    .build(), interaction -> interaction.getProfile().openMenu(new SelectGametypeMenu(q, type)));
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Select Queue";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
