package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectModeMenu extends PracticeMenu {
	private final SubmitAction action;

	@Override
	public void update() {

		setSlot(2, ItemStacks.SIMPLE_MODE, interaction -> {
			val p = interaction.getProfile();
			p.resetQueueSettings();
			p.openMenu(new SelectExistingKitMenu(new SelectArenaMenu(action), true));
		});

		setSlot(6, ItemStacks.ADVANCED_MODE,
				interaction -> interaction.getProfile().openMenu(new MechanicsMenu(action)));
	}

	@Override
	public String getTitle() {
		return CC.BLUE + "Select Mode";
	}

	@Override
	public boolean shouldUpdate() {
		return true;
	}
}
