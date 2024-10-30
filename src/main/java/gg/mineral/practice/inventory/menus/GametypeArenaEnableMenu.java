package gg.mineral.practice.inventory.menus;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ChatMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class GametypeArenaEnableMenu extends PracticeMenu {
    private final Gametype gametype;

    private int numberOfArenas;

    @Override
    public void update() {
        clear();

        val arenas = ArenaManager.getArenas().values();
        numberOfArenas = ArenaManager.getArenas().size();

        for (Arena a : arenas) {
            boolean arenaEnabled = gametype.getArenas().contains(a.getId());
            val color = arenaEnabled ? ChatColor.GREEN : ChatColor.RED;

            ItemStack item;
            try {
                item = new ItemBuilder(a.getDisplayItem())
                        .name(a.getDisplayName()).lore(color.toString() + arenaEnabled).build();
            } catch (Exception e) {
                continue;
            }

            add(item, interaction -> {
                gametype.enableArena(a, !arenaEnabled);
                ChatMessages.GAMETYPE_ARENA_SET.clone().replace("%gametype%", gametype.getName())
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
        return numberOfArenas != ArenaManager.getArenas().size();
    }
}
