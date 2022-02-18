package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.inventory.SubmitAction;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.match.MatchData;
import ms.uk.eclipse.match.PartyMatch;
import ms.uk.eclipse.party.Party;
import ms.uk.eclipse.tasks.MenuTask;
import ms.uk.eclipse.tournaments.Tournament;
import ms.uk.eclipse.util.messages.ErrorMessages;

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
		setSlot(10, kit, new MenuTask(new SelectKitMenu(this)));
		setSlot(11, kb, new MenuTask(new SelectKnockbackMenu(this)));
		setSlot(12, hitDelay, new MenuTask(new HitDelayMenu(this)));
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
		setSlot(19, pearlcd, new MenuTask(new PearlCooldownMenu(this)));
		setSlot(20, arena, new MenuTask(new SelectArenaMenu(this, action)));
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
