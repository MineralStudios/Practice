package gg.mineral.practice.inventory.menus;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class QueuetypeArenaEnableMenu extends PracticeMenu {
    private final Queuetype queuetype;

    @Override
    public void update() {
        clear();
        Iterator<Arena> arenas = ArenaManager.getArenas().iterator();

        while (arenas.hasNext()) {
            Arena a = arenas.next();
            boolean arenaEnabled = queuetype.getArenas().contains(a);
            ChatColor color = arenaEnabled ? ChatColor.GREEN : ChatColor.RED;

            ItemStack item;
            try {
                item = new ItemBuilder(a.getDisplayItem())
                        .name(CC.SECONDARY + CC.B + a.getDisplayName()).lore(color.toString() + arenaEnabled).build();
            } catch (Exception e) {
                continue;
            }

            add(item, () -> {
                queuetype.enableArena(a, !arenaEnabled);
                ChatMessages.QUEUETYPE_ARENA_SET.clone().replace("%queuetype%", queuetype.getName())
                        .replace("%toggled%", "" + !arenaEnabled).replace("%arena%", a.getName())
                        .send(viewer.getPlayer());
                reload();
            });
        }
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
