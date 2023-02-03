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

		setSlot(10, ItemStacks.SELECT_KIT
				.lore(CC.ACCENT + matchData.getKit().getName())
				.build(), interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new SelectKitMenu(this));
				});

		setSlot(11, ItemStacks.CHANGE_KNOCKBACK
				.lore(CC.ACCENT + matchData.getKnockback().getName())
				.build(), interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new SelectKnockbackMenu(this));
				});

		setSlot(12, ItemStacks.HIT_DELAY
				.lore(CC.ACCENT + matchData.getNoDamageTicks() + " Ticks")
				.build(), interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new HitDelayMenu(this));
				});

		setSlot(13, ItemStacks.TOGGLE_HUNGER
				.lore(CC.ACCENT + matchData.getHunger())
				.build(), () -> {
					matchData.setHunger(!matchData.getHunger());
					reload();
				});

		setSlot(14, ItemStacks.TOGGLE_BUILD
				.lore(CC.ACCENT + matchData.getBuild())
				.build(), () -> {
					matchData.setBuild(!matchData.getBuild());
					reload();
				});

		setSlot(15, ItemStacks.TOGGLE_DAMAGE
				.lore(CC.ACCENT + matchData.getDamage())
				.build(), () -> {
					matchData.setDamage(!matchData.getDamage());
					reload();
				});

		setSlot(16, ItemStacks.TOGGLE_GRIEFING
				.lore(CC.ACCENT + matchData.getGriefing())
				.build(), () -> {
					matchData.setGriefing(!matchData.getGriefing());
					reload();
				});

		setSlot(19, ItemStacks.PEARL_COOLDOWN
				.lore(CC.ACCENT + matchData.getPearlCooldown() + " Seconds")
				.build(), interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new PearlCooldownMenu(this));
				});

		setSlot(20, ItemStacks.ARENA
				.lore(CC.ACCENT + matchData.getArena().getName())
				.build(), interaction -> {
					Profile p = interaction.getProfile();
					p.openMenu(new SelectArenaMenu(this, submitAction));
				});

		setSlot(21, ItemStacks.DEADLY_WATER
				.lore(CC.ACCENT + matchData.getDeadlyWater())
				.build(), () -> {
					matchData.setDeadlyWater(!matchData.getDeadlyWater());
					reload();
				});

		setSlot(22, ItemStacks.REGENERATION
				.lore(CC.ACCENT + matchData.getRegeneration())
				.build(), () -> {
					matchData.setRegeneration(!matchData.getRegeneration());
					reload();
				});

		setSlot(23, ItemStacks.BOXING
				.lore(CC.ACCENT + matchData.getBoxing())
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
