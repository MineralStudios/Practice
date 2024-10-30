package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Interaction;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.ArenaManager;

import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.function.Consumer;

@ClickCancelled(true)
@RequiredArgsConstructor
public class QueueArenaEnableMenu extends PracticeMenu {
    private final Queuetype queuetype;
    private final Gametype gametype;
    private final Consumer<Interaction> queueInteraction;

    @Override
    public void update() {
        clear();
        val arenas = queuetype.filterArenasByGametype(gametype).iterator();
        val queueSettings = viewer.getQueueSettings();

        while (arenas.hasNext()) {
            byte arenaId = arenas.nextByte();
            boolean arenaEnabled = queueSettings.getEnabledArenas().get(arenaId);

            val arena = ArenaManager.getArenas().get(arenaId);

            if (arena == null)
                continue;

            val displayItem = arena.getDisplayItem();

            if (displayItem == null)
                continue;

            val displayName = arena.getDisplayName();

            if (displayName == null)
                continue;

            val item = arenaEnabled ? new ItemBuilder(displayItem)
                    .name(CC.SECONDARY + CC.B + displayName)
                    .lore(CC.GREEN + "Click to disable arena.")
                    .build() : ItemStacks.ARENA_DISABLED.name(displayName).build();

            add(item, interaction -> {
                queueSettings.enableArena(arena, !arenaEnabled);
                reload();
            });
        }

        setSlot(29, ItemStacks.DESELECT_ALL, interaction -> {
            queueSettings.getEnabledArenas().clear();
            reload();
        });

        setSlot(31, ItemStacks.APPLY, queueInteraction);

        setSlot(33, ItemStacks.SELECT_ALL, interaction -> {
            for (byte arenaId : queuetype.filterArenasByGametype(gametype))
                viewer.getQueueSettings().enableArena(arenaId, true);

            reload();
        });
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Toggle Arena";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
