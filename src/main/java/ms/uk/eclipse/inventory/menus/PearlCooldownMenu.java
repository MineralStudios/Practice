package ms.uk.eclipse.inventory.menus;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.tasks.MenuTask;

public class PearlCooldownMenu extends Menu {
    MechanicsMenu menu;

    public PearlCooldownMenu(MechanicsMenu menu) {
        super(new StrikingMessage("Modify Value", CC.PRIMARY, true));
        setClickCancelled(true);
        this.menu = menu;
    }

    public void update() {
        ItemStack item = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                .name(new ChatMessage("SUBTRACT 1", CC.PRIMARY, false).toString()).build();
        ItemStack item2 = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()))
                .name(new ChatMessage("ADD 1", CC.GREEN, false).toString()).build();
        ItemStack item3 = new ItemBuilder(Material.STONE_SWORD)
                .name(new ChatMessage("Pearl Cooldown: " + viewer.getMatchData().getPearlCooldown(), CC.PRIMARY, false)
                        .toString())
                .lore(new ChatMessage("Click To Apply Changes", CC.WHITE, false).toString()).build();
        PearlCooldownMenu pMenu = this;
        Thread subtractTask = new Thread() {
            @Override
            public void run() {
                if (viewer.getMatchData().getPearlCooldown() >= 1) {
                    viewer.getMatchData().setPearlCooldown(viewer.getMatchData().getPearlCooldown() - 1);
                }
                viewer.openMenu(pMenu);
            }
        };
        setSlot(2, item, subtractTask);
        Thread addTask = new Thread() {
            @Override
            public void run() {
                if (viewer.getMatchData().getPearlCooldown() >= 1) {
                    viewer.getMatchData().setPearlCooldown(viewer.getMatchData().getPearlCooldown() + 1);
                }
                viewer.openMenu(pMenu);
            }
        };
        setSlot(6, item2, addTask);
        setSlot(4, item3, new MenuTask(menu));
    }
}
