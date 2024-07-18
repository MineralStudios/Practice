package gg.mineral.practice.inventory.menus;

import java.util.List;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.QueueEntryManager;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.QueueSearchTask2v2;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

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
		if (type == Type.UNRANKED) {
			if (!viewer.isInParty()) {
				setSlot(2,
						ItemStacks.TEAMFIGHT
								.lore(CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "2v2" + CC.WHITE
										+ " match.",
										" ",
										CC.WHITE + "Currently:",
										viewer.getMatchData().isTeam2v2() ? CC.GREEN + "Enabled" : CC.RED + "Disabled",
										" ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle 2v2.")
								.build(),
						interaction -> {
							viewer.getMatchData().setTeam2v2(!viewer.getMatchData().isTeam2v2());
							reload();
						});
			} else
				viewer.getMatchData().setTeam2v2(true);

			setSlot(viewer.isInParty() ? 2 : 4,
					ItemStacks.BOT_SETTINGS
							.lore(CC.WHITE + "Allows you to configure the " + CC.SECONDARY + "difficulty" + CC.WHITE
									+ ".", " ",
									CC.WHITE + "Selected Difficulty: ",
									viewer.getMatchData().getBotDifficulty().getDisplay(), " ",
									CC.BOARD_SEPARATOR, " ",
									CC.GREEN + "Left Click to change difficulty.",
									CC.RED + "Right Click to create custom difficulty.")
							.build(),
					interaction -> {
						if (interaction.getClickType() == ClickType.LEFT) {

							int index = 0;

							for (int i = 0; i < Difficulty.values().length; i++) {
								Difficulty difficulty = Difficulty.values()[i];

								if (difficulty == viewer.getMatchData().getBotDifficulty()) {
									index = i;
									break;
								}
							}

							Difficulty newDifficulty = Difficulty.values()[(index + 1) % Difficulty.values().length];

							if (newDifficulty == Difficulty.CUSTOM)
								newDifficulty = Difficulty.values()[(index + 2) % Difficulty.values().length];

							viewer.getMatchData()
									.setBotDifficulty(newDifficulty);
						} else if (interaction.getClickType() == ClickType.RIGHT) {
							Profile p = interaction.getProfile();

							if (p.getPlayer().hasPermission("practice.custombot"))
								p.openMenu(new CustomBotDifficultyMenu(this));
							else
								ErrorMessages.RANK_REQUIRED.send(viewer.getPlayer());
							return;
						}

						reload();
					});

			boolean botOpponents = viewer.getMatchData().isBotQueue(),
					botTeammate = viewer.getMatchData().isBotTeammate();

			ItemStack item = ItemStacks.BOT_QUEUE_DISABLED;

			if (viewer.getMatchData().isTeam2v2() && !viewer.isInParty()) {
				if (botOpponents && botTeammate)
					item = ItemStacks.BOT_QUEUE_ENABLED_2V2.lore(
							CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "2v2 bot" + CC.WHITE
									+ " match ",
							CC.WHITE + "with a bot teammate and bot opponents.",
							" ",
							CC.WHITE + "Currently:",
							CC.GREEN + "Enabled",
							" ", CC.WHITE + "Team Settings:", CC.PINK + "Bot Teammate and Opponents", " ",
							CC.BOARD_SEPARATOR,
							CC.GREEN + "Left click to toggle bots.", CC.RED + "Right click to change team settings.")
							.build();
				else if (botOpponents)
					item = ItemStacks.BOT_QUEUE_ENABLED_2V2.lore(
							CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "2v2 bot" + CC.WHITE
									+ " match ",
							CC.WHITE + "with bots as your opponents.",
							" ",
							CC.WHITE + "Currently:",
							CC.GREEN + "Enabled",
							" ", CC.WHITE + "Team Settings:", CC.GOLD + "Bot Opponents", " ", CC.BOARD_SEPARATOR,
							CC.GREEN + "Left click to toggle bots.", CC.RED + "Right click to change team settings.")
							.build();
				else if (botTeammate)
					item = ItemStacks.BOT_QUEUE_ENABLED_2V2.lore(
							CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "2v2 bot" + CC.WHITE
									+ " match ",
							CC.WHITE + "with a bot as your teammate.",
							" ",
							CC.WHITE + "Currently:",
							CC.GREEN + "Enabled",
							" ", CC.WHITE + "Team Settings:", CC.AQUA + "Bot Teammate", " ", CC.BOARD_SEPARATOR,
							CC.GREEN + "Left click to toggle bots.", CC.RED + "Right click to change team settings.")
							.build();
			} else if (viewer.getMatchData().isBotQueue())
				item = ItemStacks.BOT_QUEUE_ENABLED;

			setSlot(6,
					item,
					interaction -> {
						boolean botOpponents1 = viewer.getMatchData().isBotQueue();
						boolean botTeammate1 = viewer.getMatchData().isBotTeammate();

						if (interaction.getClickType() == ClickType.RIGHT) {
							if (botOpponents1 && botTeammate1)
								viewer.getMatchData().setBotTeammate(false);
							else if (botOpponents1) {
								viewer.getMatchData().setBotTeammate(true);
								viewer.getMatchData().setBotQueue(false);
							} else if (botTeammate1) {
								viewer.getMatchData().setBotTeammate(true);
								viewer.getMatchData().setBotQueue(true);
							}
						} else if (interaction.getClickType() == ClickType.LEFT) {
							if (botOpponents1 || botTeammate1) {
								viewer.getMatchData().setBotTeammate(false);
								viewer.getMatchData().setBotQueue(false);
							} else
								viewer.getMatchData().setBotQueue(true);
						}

						reload();
					});

			setSlot(48, ItemStacks.RANDOM_QUEUE, () -> {
				QueueEntry queueEntry = QueueEntryManager.newEntry(queuetype,
						viewer.getMatchData().isBotQueue() ? queuetype.randomGametypeWithBotsEnabled()
								: queuetype.randomGametype());

				queue(queueEntry);

				viewer.getPlayer().closeInventory();
				return;
			});

			boolean arenaSelection = viewer.getMatchData().isArenaSelection();

			setSlot(50, ItemStacks.ARENA.lore(CC.WHITE + "Select an " + CC.SECONDARY + "arena" + CC.WHITE
					+ " when you queue.",
					" ",
					CC.WHITE + "Currently:",
					arenaSelection ? CC.GREEN + "Enabled" : CC.RED + "Disabled",
					" ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle arena selection.").build(), () -> {
						viewer.getMatchData().setArenaSelection(!viewer.getMatchData().isArenaSelection());
						reload();
					});
		}

		for (Gametype g : catagory.getGametypes()) {
			ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem().clone())
					.name(CC.SECONDARY + CC.B + g.getDisplayName());

			QueueEntry queueEntry = QueueEntryManager.newEntry(queuetype, g);

			if (type == Type.QUEUE || type == Type.UNRANKED) {
				List<QueueEntry> queueEntries = viewer.getMatchData().isTeam2v2()
						? QueueSearchTask2v2.getQueueEntries(viewer)
						: QueueSearchTask.getQueueEntries(viewer);

				if (queueEntries != null && queueEntries.contains(queueEntry))
					itemBuild.lore(CC.RED + "Click to leave queue.");
				else
					itemBuild.lore(CC.SECONDARY + "In Queue: " + CC.WHITE + (viewer.getMatchData().isTeam2v2()
							? QueueSearchTask2v2.getNumberInQueue(queuetype, g)
							: QueueSearchTask.getNumberInQueue(queuetype, g)),
							CC.SECONDARY + "In Game: " + CC.WHITE + MatchManager.getInGameCount(queuetype, g),
							CC.BOARD_SEPARATOR,
							CC.ACCENT + "Click to queue.");
			} else
				itemBuild.lore();

			ItemStack item = itemBuild.build();

			setSlot(type == Type.UNRANKED ? queuetype.getGametypes().getInt(g) + 18
					: queuetype.getGametypes().getInt(g), item, () -> {

						if (type == Type.KIT_EDITOR) {
							viewer.getPlayer().closeInventory();
							viewer.sendToKitEditor(queueEntry);
							return;
						}

						queue(queueEntry);

						if (viewer.getPlayerStatus() == PlayerStatus.QUEUEING)
							reload();
					});
		}
	}

	@Override
	public void onClose() {
		if (viewer.getPlayerStatus() == PlayerStatus.FIGHTING || viewer.getMatchData().isArenaSelection())
			return;

		viewer.openMenu(new SelectGametypeMenu(queuetype, type));
	}
}
