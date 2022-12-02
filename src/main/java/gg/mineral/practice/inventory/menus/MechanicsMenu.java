package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.tournaments.Tournament;

public class MechanicsMenu extends PracticeMenu {
	SubmitAction action;
	final static String TITLE = CC.BLUE + "Game Mechanics";

	public MechanicsMenu(SubmitAction action) {
		super(TITLE);
		setClickCancelled(true);
		this.action = action;
	}

	@Override
	public boolean update() {
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
		setSlot(10, kit, p -> {
			p.openMenu(new SelectKitMenu(this));
			return true;
		});
		setSlot(11, kb, p -> {
			p.openMenu(new SelectKnockbackMenu(this));
			return true;
		});
		setSlot(12, hitDelay, p -> {
			p.openMenu(new HitDelayMenu(this));
			return true;
		});
		MechanicsMenu menu = this;
		Runnable hungerTask = () -> {
			match.setHunger(!match.getHunger());
			viewer.openMenu(menu);
		};
		setSlot(13, hunger, hungerTask);
		Runnable buildTask = () -> {
			match.setBuild(!match.getBuild());
			viewer.openMenu(menu);
		};
		setSlot(14, build, buildTask);
		Runnable damageTask = () -> {
			match.setDamage(!match.getDamage());
			viewer.openMenu(menu);
		};
		setSlot(15, damage, damageTask);
		Runnable griefingTask = () -> {
			match.setGriefing(!match.getGriefing());
			viewer.openMenu(menu);
		};
		setSlot(16, griefing, griefingTask);
		setSlot(19, pearlcd, p -> {
			p.openMenu(new PearlCooldownMenu(this));
			return true;
		});
		setSlot(20, arena, p -> {
			p.openMenu(new SelectArenaMenu(this, action));
			return true;
		});
		Runnable deadlyWaterTask = () -> {
			match.setDeadlyWater(!match.getDeadlyWater());
			viewer.openMenu(menu);
		};
		setSlot(21, deadlyWater, deadlyWaterTask);
		Runnable regenTask = () -> {
			match.setRegeneration(!match.getRegeneration());
			viewer.openMenu(menu);
		};
		setSlot(22, regen, regenTask);
		Runnable submitTask = () -> {
			viewer.sendDuelRequest(viewer.getDuelReciever());
		};

		if (action == SubmitAction.P_SPLIT) {
			submitTask = new Runnable() {
				@Override
				public void run() {
					viewer.bukkit().closeInventory();
					Party p = viewer.getParty();

					if (!viewer.getParty().getPartyLeader().equals(viewer)) {
						viewer.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
						return;
					}

					if (p.getPartyMembers().size() < 2) {
						viewer.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH);
						return;
					}

					PartyMatch m = new PartyMatch(p, viewer.getMatchData());
					m.start();
				}
			};
		} else if (action == SubmitAction.TOURNAMENT) {
			submitTask = new Runnable() {
				@Override
				public void run() {
					viewer.bukkit().closeInventory();
					Tournament t = new Tournament(viewer);
					t.start();
				}
			};
		}

		setSlot(31, sendDuel, submitTask);
		Runnable resetTask = () -> {
			viewer.resetMatchData();
			viewer.openMenu(menu);
		};
		setSlot(27, resetMeta, resetTask);
		return true;
	}

	public SubmitAction getAction() {
		return action;
	}
}
