package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class SettingsMenu extends PracticeMenu {

	public SettingsMenu() {
		super(CC.BLUE + "Settings");
		setClickCancelled(true);
	}

	@Override
	public boolean update() {
		setSlot(0,
				ItemStacks.TOGGLE_PLAYER_VISIBILITY,
				interaction -> {
					Profile p = interaction.getProfile();
					p.getPlayer().performCommand("toggleplayervisibility");
				});
		setSlot(1,
				ItemStacks.TOGGLE_DUEL_REQUESTS,
				interaction -> {
					Profile p = interaction.getProfile();
					p.getPlayer().performCommand("toggleduelrequests");
				});
		setSlot(2,
				ItemStacks.TOGGLE_PARTY_REQUESTS,
				interaction -> {
					Profile p = interaction.getProfile();
					p.getPlayer().performCommand("togglepartyrequests");
				});
		return true;
	}
}
