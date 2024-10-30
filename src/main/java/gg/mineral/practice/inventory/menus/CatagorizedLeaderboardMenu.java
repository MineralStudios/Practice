package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.catagory.Catagory;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class CatagorizedLeaderboardMenu extends PracticeMenu {
    private final Queuetype queuetype;
    private final Catagory catagory;

    private int numberOfGametypes;

    @Override
    public void update() {
        numberOfGametypes = catagory.getGametypes().size();
        for (val gametype : catagory.getGametypes()) {

            val item = new ItemBuilder(gametype.getDisplayItem().clone())
                    .name(CC.SECONDARY + CC.B + gametype.getDisplayName()).build();
            val meta = item.getItemMeta();

            try {
                meta.setLore(gametype.getLeaderboardLore());
            } catch (Exception e) {
                meta.setLore(null);
            }

            item.setItemMeta(meta);
            setSlot(queuetype.getGametypes().getInt(gametype), item);
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + catagory.getDisplayName();
    }

    @Override
    public boolean shouldUpdate() {
        return numberOfGametypes != catagory.getGametypes().size();
    }
}
