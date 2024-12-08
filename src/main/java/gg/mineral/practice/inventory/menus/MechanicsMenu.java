package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfileList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class MechanicsMenu extends PracticeMenu {
	@Getter
	private final SubmitAction submitAction;

	@Override
	public void update() {
		val duelSettings = viewer.getDuelSettings();
		val noDamageTicks = duelSettings.getNoDamageTicks();
		val knockback = duelSettings.getKnockback() == null
				? noDamageTicks < 10 ? KnockbackProfileList.getComboKnockbackProfile()
						: KnockbackProfileList.getDefaultKnockbackProfile()
				: duelSettings.getKnockback();

		val kit = duelSettings.getKit() == null ? GametypeManager.getGametypes().get((byte) 0).getKit()
				: duelSettings.getKit();

		setSlot(10,
				ItemStacks.SELECT_KIT
						.lore(CC.WHITE + "The " + CC.SECONDARY + "items" + CC.WHITE + " in your inventory.", " ",
								CC.WHITE + "Currently:", CC.GOLD + kit.getName(),
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change kit.")
						.build(),
				interaction -> interaction.getProfile().openMenu(new SelectKitMenu(this)));

		setSlot(11,
				ItemStacks.CHANGE_KNOCKBACK
						.lore(CC.WHITE + "Changes the amount of" + CC.SECONDARY + " knockback" + CC.WHITE
								+ " you recieve.", " ",
								CC.WHITE + "Currently:", CC.GOLD + knockback.getName(),
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change knockback.")
						.build(),
				interaction -> interaction.getProfile().openMenu(new CreateCustomKnockbackMenu(this)));

		setSlot(12, ItemStacks.HIT_DELAY
				.lore(CC.WHITE + "Changes how " + CC.SECONDARY + "frequently " + CC.WHITE
						+ "you can attack.", " ",
						CC.WHITE + "Currently:", CC.GOLD + noDamageTicks + " Ticks",
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change hit delay.")
				.build(),
				interaction -> interaction.getProfile()
						.openMenu(
								ConfigureValueMenu.of(this, value -> duelSettings.setNoDamageTicks(value), int.class)));

		setSlot(13, ItemStacks.TOGGLE_HUNGER
				.lore(CC.WHITE + "Changes if you lose " + CC.SECONDARY + "hunger" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + duelSettings.isHunger(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle hunger.")
				.build(), interaction -> {
					duelSettings.setHunger(!duelSettings.isHunger());
					reload();
				});

		setSlot(14, ItemStacks.TOGGLE_BUILD
				.lore(CC.WHITE + "Changes if you can " + CC.SECONDARY + "build" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + duelSettings.isBuild(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle build.")
				.build(), interaction -> {
					duelSettings.setBuild(!duelSettings.isBuild());
					reload();
				});

		setSlot(15, ItemStacks.TOGGLE_DAMAGE
				.lore(CC.WHITE + "Changes if you can " + CC.SECONDARY + "lose health" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + duelSettings.isDamage(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle damage.")
				.build(), interaction -> {
					duelSettings.setDamage(!duelSettings.isDamage());
					reload();
				});

		setSlot(16, ItemStacks.TOGGLE_GRIEFING
				.lore(CC.WHITE + "Changes if you can " + CC.SECONDARY + "break the map" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + duelSettings.isGriefing(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle griefing.")
				.build(), interaction -> {
					duelSettings.setGriefing(!duelSettings.isGriefing());
					reload();
				});

		setSlot(19, ItemStacks.PEARL_COOLDOWN
				.lore(CC.WHITE + "Changes how frequently you can " + CC.SECONDARY + "throw a pearl" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + duelSettings.getPearlCooldown() + " Seconds",
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change cooldown.")
				.build(),
				interaction -> interaction.getProfile()
						.openMenu(
								ConfigureValueMenu.of(this, value -> duelSettings.setPearlCooldown(value), int.class)));

		val arena = ArenaManager.getArenas().get(duelSettings.getArenaId());
		setSlot(20, ItemStacks.ARENA
				.lore(CC.WHITE + "Changes the " + CC.SECONDARY + "arena" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + arena.getName(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change arena.")
				.build(), interaction -> interaction.getProfile().openMenu(new SelectArenaMenu(this, submitAction)));

		setSlot(21, ItemStacks.DEADLY_WATER
				.lore(CC.WHITE + "Changes if " + CC.SECONDARY + "water can kill you" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + duelSettings.isDeadlyWater(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle deadly water.")
				.build(), interaction -> {
					duelSettings.setDeadlyWater(!duelSettings.isDeadlyWater());
					reload();
				});

		setSlot(22, ItemStacks.REGENERATION
				.lore(CC.WHITE + "Changes if you " + CC.SECONDARY + "regenerate" + CC.WHITE
						+ " health.", " ",
						CC.WHITE + "Currently:", CC.GOLD + duelSettings.isRegeneration(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle regeneration.")
				.build(), interaction -> {
					duelSettings.setRegeneration(!duelSettings.isRegeneration());
					reload();
				});

		setSlot(23, ItemStacks.BOXING
				.lore(CC.WHITE + "Changes if you die after " + CC.SECONDARY + "100 hits" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + duelSettings.isBoxing(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle boxing.")
				.build(), interaction -> {
					duelSettings.setBoxing(!duelSettings.isBoxing());
					reload();
				});

		setSlot(27, ItemStacks.RESET_SETTINGS, interaction -> {
			viewer.resetDuelSettings();
			reload();
		});

		setSlot(31, ItemStacks.SUBMIT, interaction -> {
			viewer.getPlayer().closeInventory();
			submitAction.execute(viewer);
		});

	}

	@Override
	public String getTitle() {
		return CC.BLUE + "Game Mechanics";
	}

	@Override
	public boolean shouldUpdate() {
		return true;
	}
}
