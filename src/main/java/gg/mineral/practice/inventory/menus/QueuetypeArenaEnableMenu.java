package gg.mineral.practice.inventory.menus;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class QueuetypeArenaEnableMenu extends PracticeMenu {
    final static String TITLE = CC.BLUE + "Toggle Arena";
    Queuetype queuetype;

    public QueuetypeArenaEnableMenu(Queuetype queuetype) {
        super(TITLE);
        setClickCancelled(true);
        this.queuetype = queuetype;
    }

    @Override
    public boolean update() {
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
        return true;
    }
}
