package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.arena.Arena;

import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Interaction;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.ArenaManager;

import gg.mineral.practice.queue.QueueSettings;

import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import lombok.RequiredArgsConstructor;

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
        ByteIterator arenas = queuetype.filterArenasByGametype(gametype).iterator();

        QueueSettings queueSettings = viewer.getQueueSettings();

        while (arenas.hasNext()) {
            byte arenaId = arenas.nextByte();
            boolean arenaEnabled = queueSettings.getEnabledArenas().get(arenaId);

            ItemStack item = null;

            Arena arena = ArenaManager.getArenas().get(arenaId);

            if (arena == null)
                continue;

            try {

                if (arenaEnabled) {
                    item = new ItemBuilder(arena.getDisplayItem())
                            .name(CC.SECONDARY + CC.B + arena.getDisplayName())
                            .lore(CC.GREEN + "Click to disable arena.")
                            .build();
                } else {
                    item = ItemStacks.ARENA_DISABLED.name(arena.getDisplayName()).build();
                }
            } catch (Exception e) {
                continue;
            }

            add(item, () -> {
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
