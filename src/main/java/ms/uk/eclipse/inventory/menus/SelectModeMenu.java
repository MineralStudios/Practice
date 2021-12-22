package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.inventory.SubmitAction;
import ms.uk.eclipse.tasks.MenuTask;

public class SelectModeMenu extends Menu {
        SubmitAction action;

        public SelectModeMenu(SubmitAction action) {
                super(new StrikingMessage("Select Mode", CC.PRIMARY, true));
                setClickCancelled(true);
                this.action = action;
        }

        public void update() {
                viewer.setPreviousSubmitAction(action);
                ItemStack item = new ItemBuilder(Material.GREEN_RECORD)
                                .name(new ChatMessage("Simple Mode", CC.GREEN, true).toString()).lore().build();
                ItemStack item2 = new ItemBuilder(Material.GOLD_RECORD)
                                .name(new ChatMessage("Advanced Mode", CC.GOLD, true).toString()).lore().build();

                if (action == SubmitAction.TOURNAMENT) {
                        ItemStack item3 = new ItemBuilder(Material.RECORD_4)
                                        .name(new ChatMessage("Tournament Mode", CC.RED, true).toString()).lore()
                                        .build();
                        setSlot(4, item3, new MenuTask(new SelectTournamentMenu()));
                }

                setSlot(2, item, new MenuTask(new SelectExistingKitMenu(new SelectArenaMenu(action), true)));
                setSlot(6, item2, new MenuTask(new MechanicsMenu(action)));

        }
}
