package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.party.Party;

public class OtherPartiesMenu extends Menu {
    PartyManager partyManager = PracticePlugin.INSTANCE.getPartyManager();

    public OtherPartiesMenu() {
        super(new StrikingMessage("Other Parties", CC.PRIMARY, true));
        setClickCancelled(true);
    }

    @Override
    public void update() {
        clear();

        for (Party p : partyManager.getPartys()) {
            if (p.getPartyLeader().getPlayerStatus() == PlayerStatus.IN_LOBBY && !p.equals(viewer.getParty())) {
                Profile partyLeader = p.getPartyLeader();
                ItemStack skull = new ItemBuilder(Material.SKULL_ITEM)
                        .name(new ChatMessage(partyLeader.getName(), CC.SECONDARY, false).toString()).build();
                add(skull, new CommandTask("duel " + partyLeader.getName()));
            }
        }
    }
}
