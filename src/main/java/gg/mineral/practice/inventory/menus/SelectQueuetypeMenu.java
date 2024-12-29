package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectQueuetypeMenu extends PracticeMenu {

    private final SelectGametypeMenu.Type type;

    @Override
    public void update() {
        clear();
        setSlot(18, new ItemStack(Material.AIR));
        int queueCount = QueuetypeManager.getQueuetypes().size();
        int horizontalSpacing = 9 / queueCount;
        val queuetypeArray = QueuetypeManager.getQueuetypes().values().toArray(new Queuetype[0]);
        for (int i = 0; i < queueCount; i++) {
            val q = queuetypeArray[i];
            addAfter(11 + i * horizontalSpacing, new ItemBuilder(q.getDisplayItem().clone())
                    .name(CC.SECONDARY + CC.B + q.getDisplayName())
                    .lore(CC.ACCENT + (type == SelectGametypeMenu.Type.KIT_EDITOR ? "Click to edit kit."
                            : "Click to select."))
                    .build(), interaction -> interaction.getProfile().openMenu(new SelectGametypeMenu(q, type, this)));
        }

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
