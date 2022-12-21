package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;

public class MechanicsMenu extends PracticeMenu {
	@Getter
	SubmitAction submitAction;
	final static String TITLE = CC.BLUE + "Game Mechanics";

	public MechanicsMenu(SubmitAction submitAction) {
		super(TITLE);
		setClickCancelled(true);
		this.submitAction = submitAction;
	}

	@Override
	public boolean update() {
		MatchData matchData = viewer.getMatchData();
		ItemStack kit = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
				.lore(CC.ACCENT + matchData.getKit().getName())
				.name("Select Kit").build();
		ItemStack kb = new ItemBuilder(Material.STICK)
				.lore(CC.ACCENT + matchData.getKnockback().getName())
				.name("Change Knockback").build();
		ItemStack hitDelay = new ItemBuilder(Material.WATCH)
				.lore(CC.ACCENT + matchData.getNoDamageTicks() + " Ticks")
				.name("Hit Delay").build();
		ItemStack hunger = new ItemBuilder(Material.COOKED_BEEF)
				.lore(CC.ACCENT + matchData.getHunger())
				.name("Toggle Hunger").build();
		ItemStack deadlyWater = new ItemBuilder(Material.BLAZE_ROD)
				.lore(CC.ACCENT + matchData.getDeadlyWater())
				.name("Deadly Water").build();
		ItemStack build = new ItemBuilder(Material.BRICK)
				.lore(CC.ACCENT + matchData.getBuild())
				.name("Toggle Build").build();
		ItemStack damage = new ItemBuilder(Material.DIAMOND_AXE)
				.lore(CC.ACCENT + matchData.getDamage())
				.name("Toggle Damage").build();
		ItemStack griefing = new ItemBuilder(Material.TNT)
				.lore(CC.ACCENT + matchData.getGriefing())
				.name("Toggle Griefing").build();
		ItemStack pearlcd = new ItemBuilder(Material.ENDER_PEARL)
				.lore(CC.ACCENT + matchData.getPearlCooldown() + " Seconds")
				.name("Pearl Cooldown").build();
		ItemStack arena = new ItemBuilder(Material.WATER_LILY)
				.lore(CC.ACCENT + matchData.getArena().getName())
				.name("Arena").build();
		ItemStack regen = new ItemBuilder(Material.GOLDEN_APPLE)
				.lore(CC.ACCENT + matchData.getRegeneration())
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
			matchData.setHunger(!matchData.getHunger());
			viewer.openMenu(menu);
		};
		setSlot(13, hunger, hungerTask);
		Runnable buildTask = () -> {
			matchData.setBuild(!matchData.getBuild());
			viewer.openMenu(menu);
		};
		setSlot(14, build, buildTask);
		Runnable damageTask = () -> {
			matchData.setDamage(!matchData.getDamage());
			viewer.openMenu(menu);
		};
		setSlot(15, damage, damageTask);
		Runnable griefingTask = () -> {
			matchData.setGriefing(!matchData.getGriefing());
			viewer.openMenu(menu);
		};
		setSlot(16, griefing, griefingTask);
		setSlot(19, pearlcd, p -> {
			p.openMenu(new PearlCooldownMenu(this));
			return true;
		});
		setSlot(20, arena, p -> {
			p.openMenu(new SelectArenaMenu(this, submitAction));
			return true;
		});
		Runnable deadlyWaterTask = () -> {
			matchData.setDeadlyWater(!matchData.getDeadlyWater());
			viewer.openMenu(menu);
		};
		setSlot(21, deadlyWater, deadlyWaterTask);
		Runnable regenTask = () -> {
			matchData.setRegeneration(!matchData.getRegeneration());
			viewer.openMenu(menu);
		};
		setSlot(22, regen, regenTask);
		Runnable submitTask = () -> {
			viewer.getRequestHandler().sendDuelRequest(viewer.getRequestHandler().getDuelRequestReciever());
		};

		if (submitAction == SubmitAction.P_SPLIT) {
			submitTask = () -> {
				viewer.getPlayer().closeInventory();
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
			};
		} else if (submitAction == SubmitAction.TOURNAMENT) {
			submitTask = () -> {
				viewer.getPlayer().closeInventory();
				Tournament tournament = new Tournament(viewer);
				tournament.start();
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
}
