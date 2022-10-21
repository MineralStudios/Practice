package gg.mineral.practice.inventory.menus;

import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.match.MatchData;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.party.Party;

import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class MechanicsMenu implements InventoryBuilder {
	SubmitAction action;
	final static String TITLE = CC.BLUE + "Game Mechanics";

	public MechanicsMenu(SubmitAction action) {
		super(TITLE);
		setItemDragging(true);
		this.action = action;
	}

	@Override
	public MineralInventory build(Profile profile) {
		MatchData match = viewer.getMatchData();
		ItemStack kit = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
				.lore(CC.ACCENT + match.getKitName())
				.name("Select Kit").build();
		ItemStack kb = new ItemBuilder(Material.STICK)
				.lore(CC.ACCENT + match.getKnockback().getName())
				.name("Change Knockback").build();
		ItemStack hitDelay = new ItemBuilder(Material.WATCH)
				.lore(CC.ACCENT + match.getNoDamageTicks() + " Ticks")
				.name("Hit Delay").build();
		ItemStack hunger = new ItemBuilder(Material.COOKED_BEEF)
				.lore(CC.ACCENT + match.getHunger())
				.name("Toggle Hunger").build();
		ItemStack deadlyWater = new ItemBuilder(Material.BLAZE_ROD)
				.lore(CC.ACCENT + match.getDeadlyWater())
				.name("Deadly Water").build();
		ItemStack build = new ItemBuilder(Material.BRICK)
				.lore(CC.ACCENT + match.getBuild())
				.name("Toggle Build").build();
		ItemStack damage = new ItemBuilder(Material.DIAMOND_AXE)
				.lore(CC.ACCENT + match.getDamage())
				.name("Toggle Damage").build();
		ItemStack boxing = new ItemBuilder(Material.IRON_CHESTPLATE)
				.lore(CC.ACCENT + match.getBoxing())
				.name("Toggle Boxing").build();
		ItemStack griefing = new ItemBuilder(Material.TNT)
				.lore(CC.ACCENT + match.getGriefing())
				.name("Toggle Griefing").build();
		ItemStack pearlcd = new ItemBuilder(Material.ENDER_PEARL)
				.lore(CC.ACCENT + match.getPearlCooldown() + " Seconds")
				.name("Pearl Cooldown").build();
		ItemStack arena = new ItemBuilder(Material.WATER_LILY)
				.lore(CC.ACCENT + match.getArena().getName())
				.name("Arena").build();
		ItemStack regen = new ItemBuilder(Material.GOLDEN_APPLE)
				.lore(CC.ACCENT + match.getRegeneration())
				.name("Regeneration").build();
		ItemStack sendDuel = new ItemBuilder(Material.STICK)
				.name("Submit").build();
		ItemStack resetMeta = new ItemBuilder(Material.PAPER)
				.name("Reset Settings").build();
		set(10, kit, new MenuTask(new SelectKitMenu(this)));
		set(11, kb, new MenuTask(new SelectKnockbackMenu(this)));
		set(12, hitDelay, new MenuTask(new HitDelayMenu(this)));
		MechanicsMenu menu = this;
		Runnable hungerTask = () -> {
			match.setHunger(!match.getHunger());
			viewer.openMenu(menu);
		};
		set(13, hunger, hungerTask);
		Runnable buildTask = () -> {
			match.setBuild(!match.getBuild());
			viewer.openMenu(menu);
		};
		set(14, build, buildTask);
		Runnable damageTask = () -> {
			match.setDamage(!match.getDamage());
			viewer.openMenu(menu);
		};
		set(15, damage, damageTask);
		Runnable griefingTask = () -> {
			match.setGriefing(!match.getGriefing());
			viewer.openMenu(menu);
		};
		set(16, griefing, griefingTask);
		set(19, pearlcd, new MenuTask(new PearlCooldownMenu(this)));
		set(20, arena, new MenuTask(new SelectArenaMenu(this, action)));
		Runnable deadlyWaterTask = () -> {
			match.setDeadlyWater(!match.getDeadlyWater());
			viewer.openMenu(menu);
		};
		set(21, deadlyWater, deadlyWaterTask);
		Runnable regenTask = () -> {
			match.setRegeneration(!match.getRegeneration());
			viewer.openMenu(menu);
		};
		set(22, regen, regenTask);
		Runnable boxingTask = () -> {
			match.setBoxing(!match.getBoxing());
			viewer.openMenu(menu);
		};
		set(23, boxing, boxingTask);
		Runnable submitTask = () -> {
			viewer.sendDuelRequest(viewer.getDuelReciever());
		};

		if (action == SubmitAction.P_SPLIT) {
			submitTask = () -> {
				viewer.bukkit().closeInventory();
				Party p = viewer.getParty();

				if (!p.getPartyLeader().equals(viewer)) {
					viewer.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
					return;
				}

				if (p.getPartyMembers().size() < 2) {
					viewer.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH);
					return;
				}

				PartyMatch m = new PartyMatch(p, viewer.getMatchData());
				try {
					m.start();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			};
		} else if (action == SubmitAction.TOURNAMENT) {
			submitTask = () -> {
				viewer.bukkit().closeInventory();
				Tournament t = new Tournament(viewer);
				t.start();
			};
		}

		set(31, sendDuel, submitTask);
		Runnable resetTask = () -> {
			viewer.resetMatchData();
			viewer.openMenu(menu);
		};
		set(27, resetMeta, resetTask);
		return true;
	}

	public SubmitAction getAction() {
		return action;
	}
}
