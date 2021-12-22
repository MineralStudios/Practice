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

public class HitDelayMenu extends Menu {
        MechanicsMenu menu;

        public HitDelayMenu(MechanicsMenu menu) {
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
                                .name(new ChatMessage("Hit Delay: " + viewer.getMatchData().getNoDamageTicks(),
                                                CC.PRIMARY, false).toString())
                                .lore(new ChatMessage("Click To Apply Changes", CC.WHITE, false).toString()).build();
                HitDelayMenu hitMenu = this;
                Thread subtractTask = new Thread() {
                        @Override
                        public void run() {
                                if (viewer.getMatchData().getNoDamageTicks() >= 1) {
                                        viewer.getMatchData()
                                                        .setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() - 1);
                                }
                                viewer.openMenu(hitMenu);
                        }
                };
                setSlot(2, item, subtractTask);
                Thread addTask = new Thread() {
                        @Override
                        public void run() {
                                viewer.getMatchData().setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() + 1);
                                viewer.openMenu(hitMenu);
                        }
                };
                setSlot(6, item2, addTask);
                setSlot(4, item3, new MenuTask(menu));
        }
}
