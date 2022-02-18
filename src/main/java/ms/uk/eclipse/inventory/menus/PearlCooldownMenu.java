package ms.uk.eclipse.inventory.menus;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.tasks.MenuTask;

public class PearlCooldownMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Modify Value";

    public PearlCooldownMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        ItemStack item = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                .name("SUBTRACT 1").build();
        ItemStack item2 = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()))
                .name("ADD 1").build();
        ItemStack item3 = new ItemBuilder(Material.STONE_SWORD)
                .name("Pearl Cooldown: " + viewer.getMatchData().getPearlCooldown())
                .lore(CC.ACCENT + "Click To Apply Changes").build();
        PearlCooldownMenu pMenu = this;
        Runnable subtractTask = () -> {
            if (viewer.getMatchData().getPearlCooldown() >= 1) {
                viewer.getMatchData().setPearlCooldown(viewer.getMatchData().getPearlCooldown() - 1);
            }
            viewer.openMenu(pMenu);
        };
        setSlot(2, item, subtractTask);
        Runnable addTask = () -> {
            if (viewer.getMatchData().getPearlCooldown() >= 1) {
                viewer.getMatchData().setPearlCooldown(viewer.getMatchData().getPearlCooldown() + 1);
            }
            viewer.openMenu(pMenu);
        };
        setSlot(6, item2, addTask);
        setSlot(4, item3, new MenuTask(menu));
        return true;
    }
}
