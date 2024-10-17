package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.managers.MatchManager;

import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.QueueSettings.QueueEntry;
import gg.mineral.practice.queue.QueueSystem;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

@ClickCancelled(true)
public class SelectCategorizedGametypeMenu extends SelectGametypeMenu {
	private final Catagory catagory;

	public SelectCategorizedGametypeMenu(Queuetype queuetype, Catagory catagory, Type type) {
		super(queuetype, type);
		this.catagory = catagory;
	}

	@Override
	public String getTitle() {
		return CC.BLUE + catagory.getName();
	}

	@Override
	public void update() {
		QueueSettings queueSettings = viewer.getQueueSettings();
		if (!queueSettings.isOpponentBot() && !queueSettings.isTeammateBot()) {
			queueSettings.setOpponentBot(true);
			queueSettings.setTeammateBot(true);
		}
		boolean botOpponents = queueSettings.isOpponentBot();
		boolean botTeammate = queueSettings.isTeammateBot();

		if (type == Type.UNRANKED) {

			if (!viewer.isInParty()) {
				int teamSize = queueSettings.getTeamSize();
				setSlot(2,
						ItemStacks.TEAMFIGHT
								.lore(CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "team" + CC.WHITE
										+ " match.",
										" ",
										CC.WHITE + "Currently:",
										teamSizeColors.get(teamSize),
										" ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change team size.")
								.build(),
						TEAM_SIZE_INTERACTION);
			} else
				queueSettings.setTeamSize((byte) (viewer.getParty().getPartyMembers().size()));

			if (queueSettings.getTeamSize() == 1) {
				byte difficulty = queueSettings.getDifficulty();
				setSlot(viewer.isInParty() ? 2 : 4,
						ItemStacks.BOT_SETTINGS
								.lore(CC.WHITE + "Allows you to configure the " + CC.SECONDARY + "difficulty" + CC.WHITE
										+ ".", " ",
										CC.WHITE + "Selected Difficulty: ",
										Difficulty.values()[difficulty].getDisplay(), " ",
										CC.BOARD_SEPARATOR, " ",
										CC.GREEN + "Left Click to change difficulty.",
										CC.RED + "Right Click to create custom difficulty.")
								.build(),
						DIFFICULTY_INTERACTION);
			}

			ItemStack item = ItemStacks.BOT_QUEUE_DISABLED;

			if (viewer.getQueueSettings().isBotQueue()) {
				if (queueSettings.getTeamSize() > 1 && !viewer.isInParty()) {
					if (botOpponents && botTeammate)
						item = ItemStacks.BOT_QUEUE_ENABLED_TEAM.lore(
								CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "2v2 bot" + CC.WHITE
										+ " match ",
								CC.WHITE + "with a bot teammate and bot opponents.",
								" ",
								CC.WHITE + "Currently:",
								CC.GREEN + "Enabled",
								" ", CC.WHITE + "Team Settings:", CC.PINK + "Bot Teammate and Opponents", " ",
								CC.BOARD_SEPARATOR,
								CC.GREEN + "Left click to toggle bots.",
								CC.RED + "Right click to change team settings.")
								.build();
					else if (botOpponents)
						item = ItemStacks.BOT_QUEUE_ENABLED_TEAM.lore(
								CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "2v2 bot" + CC.WHITE
										+ " match ",
								CC.WHITE + "with bots as your opponents.",
								" ",
								CC.WHITE + "Currently:",
								CC.GREEN + "Enabled",
								" ", CC.WHITE + "Team Settings:", CC.GOLD + "Bot Opponents", " ", CC.BOARD_SEPARATOR,
								CC.GREEN + "Left click to toggle bots.",
								CC.RED + "Right click to change team settings.")
								.build();
					else if (botTeammate)
						item = ItemStacks.BOT_QUEUE_ENABLED_TEAM.lore(
								CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "2v2 bot" + CC.WHITE
										+ " match ",
								CC.WHITE + "with a bot as your teammate.",
								" ",
								CC.WHITE + "Currently:",
								CC.GREEN + "Enabled",
								" ", CC.WHITE + "Team Settings:", CC.AQUA + "Bot Teammate", " ", CC.BOARD_SEPARATOR,
								CC.GREEN + "Left click to toggle bots.",
								CC.RED + "Right click to change team settings.")
								.build();
				} else
					item = ItemStacks.BOT_QUEUE_ENABLED;
			}

			setSlot(6,
					item,
					BOT_QUEUE_INTERACTION);

			setSlot(48, ItemStacks.RANDOM_QUEUE, interaction -> {
				Gametype gametype = viewer.getQueueSettings().isBotQueue() ? queuetype.randomGametypeWithBotsEnabled()
						: queuetype.randomGametype();

				queue(gametype, interaction);

				if (viewer.getOpenMenu() instanceof SelectGametypeMenu)
					viewer.getPlayer().closeInventory();
			});

			boolean arenaSelection = viewer.getQueueSettings().isArenaSelection();

			setSlot(50, ItemStacks.ARENA.lore(CC.WHITE + "Select an " + CC.SECONDARY + "arena" + CC.WHITE
					+ " when you queue.",
					" ",
					CC.WHITE + "Currently:",
					arenaSelection ? CC.GREEN + "Enabled" : CC.RED + "Disabled",
					" ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle arena selection.").build(),
					ARENA_INTERACTION);
		}

		for (Gametype g : catagory.getGametypes()) {

			if (!g.isInCatagory())
				continue;

			ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem().clone())
					.name(CC.SECONDARY + CC.B + g.getDisplayName());

			byte teamSize = queueSettings.getTeamSize();
			QueueEntry queueEntry = QueueSettings.toEntry(queuetype, g, teamSize,
					botTeammate,
					botOpponents, queueSettings.getEnabledArenas());

			if (type == Type.QUEUE || type == Type.UNRANKED) {
				@Nullable
				QueueEntry queueEntries = QueueSystem.getQueueEntry(viewer, queuetype, g);

				if (queueEntries != null)
					itemBuild.lore(CC.RED + "Click to leave queue.");
				else
					itemBuild.lore(
							CC.SECONDARY + "In Queue: " + CC.WHITE
									+ QueueSystem.getCompatibleQueueCount(queueEntry.queuetype(),
											queueEntry.gametype()),
							CC.SECONDARY + "In Game: " + CC.WHITE + MatchManager.getInGameCount(queuetype, g),
							CC.BOARD_SEPARATOR,
							CC.ACCENT + "Click to queue.");
			} else
				itemBuild.lore();

			ItemStack item = itemBuild.build();

			setSlot(type == Type.UNRANKED ? queuetype.getGametypes().getInt(g) + 18
					: queuetype.getGametypes().getInt(g), item, interaction -> {

						if (type == Type.KIT_EDITOR) {
							viewer.getPlayer().closeInventory();
							viewer.sendToKitEditor(queuetype, g);
							return;
						}

						queue(g, interaction);

						if (viewer.getPlayerStatus() == PlayerStatus.QUEUEING)
							reload();
					});
		}
	}

	@Override
	public void onClose() {
		if (viewer.getPlayerStatus() == PlayerStatus.FIGHTING || viewer.getQueueSettings().isArenaSelection())
			return;

		viewer.openMenu(new SelectGametypeMenu(queuetype, type));
	}
}
