package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.ProfileData;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class CatagorizedEloMenu extends PracticeMenu {
    private final ProfileData arg;
    private final Queuetype queuetype;
    private final Catagory catagory;

    @Override
    public void update() {
        for (Gametype gametype : catagory.getGametypes())
            setSlot(queuetype.getGametypes().getInt(gametype), new ItemBuilder(gametype.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + gametype.getDisplayName())
                    .lore(" ",
                            CC.WHITE + arg.getName() + "'s Elo:",
                            CC.GOLD + gametype.getElo(arg))
                    .build());
    }

    @Override
    public String getTitle() {
        return CC.BLUE + catagory.getDisplayName();
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
