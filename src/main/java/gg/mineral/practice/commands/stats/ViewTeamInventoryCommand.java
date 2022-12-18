package gg.mineral.practice.commands.stats;

import java.util.List;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.InventoryStatsListMenu;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class ViewTeamInventoryCommand extends PlayerCommand {

    public ViewTeamInventoryCommand() {
        super("viewteaminventory");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        Profile player = ProfileManager.getOrCreateProfile(pl);

        if (args.length < 1) {
            player.message(UsageMessages.VIEW_TEAM_INV);
            return;
        }

        List<InventoryStatsMenu> inventoryStatsList = ProfileManager.getTeamInventoryStats(args[0]);

        if (inventoryStatsList == null) {
            player.message(ErrorMessages.TEAM_INVENTORY_NOT_FOUND);
            return;
        }

        player.openMenu(new InventoryStatsListMenu(inventoryStatsList));
    }
}
