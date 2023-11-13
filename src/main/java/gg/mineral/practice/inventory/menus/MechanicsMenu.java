package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.items.ItemStacks;
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

		setSlot(10,
				ItemStacks.SELECT_KIT
						.lore(CC.WHITE + "The " + CC.SECONDARY + "items" + CC.WHITE + " in your inventory.", " ",
								CC.WHITE + "Currently:", CC.GOLD + matchData.getKit().getName(),
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change kit.")
						.build(),
				interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new SelectKitMenu(this));
				});

		setSlot(11,
				ItemStacks.CHANGE_KNOCKBACK
						.lore(CC.WHITE + "Changes the amount of" + CC.SECONDARY + " knockback" + CC.WHITE
								+ " you recieve.", " ",
								CC.WHITE + "Currently:", CC.GOLD + matchData.getKnockback().getName(),
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change knockback.")
						.build(),
				interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new SelectKnockbackMenu(this));
				});

		setSlot(12, ItemStacks.HIT_DELAY
				.lore(CC.WHITE + "Changes how " + CC.SECONDARY + "frequently " + CC.WHITE
						+ "you can attack.", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getNoDamageTicks() + " Ticks",
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change hit delay.")
				.build(), interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new HitDelayMenu(this));
				});

		setSlot(13, ItemStacks.TOGGLE_HUNGER
				.lore(CC.WHITE + "Changes if you lose " + CC.SECONDARY + "hunger" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getHunger(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle hunger.")
				.build(), () -> {
					matchData.setHunger(!matchData.getHunger());
					reload();
				});

		setSlot(14, ItemStacks.TOGGLE_BUILD
				.lore(CC.WHITE + "Changes if you can " + CC.SECONDARY + "build" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getBuild(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle build.")
				.build(), () -> {
					matchData.setBuild(!matchData.getBuild());
					reload();
				});

		setSlot(15, ItemStacks.TOGGLE_DAMAGE
				.lore(CC.WHITE + "Changes if you can " + CC.SECONDARY + "lose health" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getDamage(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle damage.")
				.build(), () -> {
					matchData.setDamage(!matchData.getDamage());
					reload();
				});

		setSlot(16, ItemStacks.TOGGLE_GRIEFING
				.lore(CC.WHITE + "Changes if you can " + CC.SECONDARY + "break the map" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getGriefing(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle griefing.")
				.build(), () -> {
					matchData.setGriefing(!matchData.getGriefing());
					reload();
				});

		setSlot(19, ItemStacks.PEARL_COOLDOWN
				.lore(CC.WHITE + "Changes how frequently you can " + CC.SECONDARY + "throw a pearl" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getPearlCooldown() + " Seconds",
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change cooldown.")
				.build(), interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new PearlCooldownMenu(this));
				});

		setSlot(20, ItemStacks.ARENA
				.lore(CC.WHITE + "Changes the " + CC.SECONDARY + "arena" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getArena().getName(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change arena.")
				.build(), interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new SelectArenaMenu(this, submitAction));
				});

		setSlot(21, ItemStacks.DEADLY_WATER
				.lore(CC.WHITE + "Changes if " + CC.SECONDARY + "water can kill you" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getDeadlyWater(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle deadly water.")
				.build(), () -> {
					matchData.setDeadlyWater(!matchData.getDeadlyWater());
					reload();
				});

		setSlot(22, ItemStacks.REGENERATION
				.lore(CC.WHITE + "Changes if you " + CC.SECONDARY + "regenerate" + CC.WHITE
						+ " health.", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getRegeneration(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle regeneration.")
				.build(), () -> {
					matchData.setRegeneration(!matchData.getRegeneration());
					reload();
				});

		setSlot(23, ItemStacks.BOXING
				.lore(CC.WHITE + "Changes if you die after " + CC.SECONDARY + "100 hits" + CC.WHITE
						+ ".", " ",
						CC.WHITE + "Currently:", CC.GOLD + matchData.getBoxing(),
						CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle boxing.")
				.build(), () -> {
					matchData.setBoxing(!matchData.getBoxing());
					reload();
				});

		setSlot(27, ItemStacks.RESET_SETTINGS, () -> {
			viewer.resetMatchData();
			reload();
		});

		setSlot(31, ItemStacks.SUBMIT, () -> {
			viewer.getPlayer().closeInventory();

			switch (submitAction) {
				case DUEL:
					viewer.getRequestHandler().sendDuelRequest(viewer.getRequestHandler().getDuelRequestReciever());
					break;
				case P_SPLIT:
					Party party = viewer.getParty();

					if (!viewer.getParty().getPartyLeader().equals(viewer)) {
						viewer.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
						return;
					}

					if (party.getPartyMembers().size() < 2) {
						viewer.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH);
						return;
					}

					PartyMatch partyMatch = new PartyMatch(party, viewer.getMatchData());
					partyMatch.start();
					break;
				case TOURNAMENT:
					Tournament tournament = new Tournament(viewer);
					tournament.start();
					break;
				default:
					break;

			}

		});

		return true;
	}
}
