package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.party.Party;

public class OtherPartiesMenu extends PracticeMenu {
    PartyManager partyManager = PracticePlugin.INSTANCE.getPartyManager();
    final static String TITLE = CC.BLUE + "Other Parties";

    public OtherPartiesMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        clear();

        for (Party p : partyManager.getPartys()) {
            if (p.getPartyLeader().getPlayerStatus() == PlayerStatus.IN_LOBBY && !p.equals(viewer.getParty())) {
                Profile partyLeader = p.getPartyLeader();
                ItemStack skull = new ItemBuilder(Material.SKULL_ITEM)
                        .name(partyLeader.getName()).build();
                add(skull, new CommandTask("duel " + partyLeader.getName()));
            }
        }

        return true;
    }
}
