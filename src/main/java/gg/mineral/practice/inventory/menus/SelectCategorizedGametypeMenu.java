package gg.mineral.practice.inventory.menus;

import java.util.List;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
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

public class SelectCategorizedGametypeMenu extends SelectGametypeMenu {
	Catagory c;

	public SelectCategorizedGametypeMenu(Queuetype q, Catagory c, Type type) {
		super(q, type);
		setTitle(CC.BLUE + c.getName());
		this.c = c;
	}

	@Override
	public boolean update() {
		if (type == Type.UNRANKED) {
			setSlot(2, ItemStacks.TEAMFIGHT.lore("2v2: " + viewer.getMatchData().getTeam2v2(), " ",
					CC.GREEN + "Left Click to toggle 2v2").build(), interaction -> {
						viewer.getMatchData().setTeam2v2(!viewer.getMatchData().getTeam2v2());
						reload();
					});

			setSlot(4,
					ItemStacks.BOT_SETTINGS
							.lore("Difficulty: " + viewer.getMatchData().getBotDifficulty().getDisplay(), " ",
									CC.GREEN + "Left Click to Change Difficulty",
									CC.RED + "Right Click to create custom difficulty")
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

							if (p.getPlayer().hasPermission("practice.custombot")) {
								p.openMenu(new CustomBotDifficultyMenu(this));
							} else {
								ErrorMessages.RANK_REQUIRED.send(viewer.getPlayer());
							}
							return;
						}

						reload();
					});

			boolean botOpponents = viewer.getMatchData().getBotQueue();
			boolean botTeammate = viewer.getMatchData().getBotTeammate();

			ItemStack item = ItemStacks.BOT_QUEUE_DISABLED;

			if (botOpponents && botTeammate) {
				item = ItemStacks.BOT_QUEUE_ENABLED.lore(
						"2v2 Settings: Bot Opponents & Teammate", " ",
						CC.RED + "Right Click to change 2v2 Settings").build();
			} else if (botOpponents) {
				item = ItemStacks.BOT_QUEUE_ENABLED.lore(
						"2v2 Settings: Bot Opponents", " ",
						CC.RED + "Right Click to change 2v2 Settings").build();
			} else if (botTeammate) {
				item = ItemStacks.BOT_QUEUE_ENABLED.lore(
						"2v2 Settings: Bot Teammate", " ",
						CC.RED + "Right Click to change 2v2 Settings").build();
			}

			setSlot(6,
					item,
					interaction -> {
						boolean botOpponents1 = viewer.getMatchData().getBotQueue();
						boolean botTeammate1 = viewer.getMatchData().getBotTeammate();

						if (interaction.getClickType() == ClickType.RIGHT) {

							if (botOpponents1 && botTeammate1) {
								viewer.getMatchData().setBotTeammate(false);
							} else if (botOpponents1) {
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
							} else {
								viewer.getMatchData().setBotQueue(true);
							}
						}

						reload();
					});

			setSlot(48, ItemStacks.RANDOM_QUEUE, () -> {
				QueueEntry queueEntry = QueueEntryManager.newEntry(queuetype, queuetype.randomGametype());

				queue(queueEntry);

				viewer.getPlayer().closeInventory();
				return;
			});

			boolean arenaSelection = viewer.getMatchData().getArenaSelection();

			String color = arenaSelection ? CC.GREEN : CC.RED;

			setSlot(50, ItemStacks.ARENA.name("Toggle Arena Selection").lore(color + arenaSelection).build(), () -> {
				viewer.getMatchData().setArenaSelection(!viewer.getMatchData().getArenaSelection());
				return;
			});
		}

		for (Gametype g : c.getGametypes()) {
			ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem().clone())
					.name(g.getDisplayName());

			QueueEntry queueEntry = QueueEntryManager.newEntry(queuetype, g);

			if (type == Type.QUEUE || type == Type.UNRANKED) {
				List<QueueEntry> queueEntries = viewer.getMatchData().getTeam2v2()
						? QueueSearchTask2v2.getQueueEntries(viewer)
						: QueueSearchTask.getQueueEntries(viewer);

				if (queueEntries != null && queueEntries.contains(queueEntry)) {
					itemBuild.lore(CC.RED + "Click to leave queue.");
				} else {

					itemBuild.lore(CC.ACCENT + "In Queue: " + (viewer.getMatchData().getTeam2v2()
							? QueueSearchTask2v2.getNumberInQueue(queuetype, g)
							: QueueSearchTask.getNumberInQueue(queuetype, g)),
							CC.ACCENT + "In Game: " + MatchManager.getInGameCount(queuetype, g));
				}
			} else {
				itemBuild.lore();
			}
			ItemStack item = itemBuild.build();

			setSlot(type == Type.UNRANKED ? queuetype.getGametypes().getInt(g) + 18
					: queuetype.getGametypes().getInt(g), item, () -> {

						if (type == Type.KIT_EDITOR) {
							viewer.getPlayer().closeInventory();
							viewer.sendToKitEditor(queueEntry);
							return;
						}

						queue(queueEntry);

						if (viewer.getPlayerStatus() == PlayerStatus.QUEUEING) {
							reload();
						}
					});
		}

		return true;
	}

	@Override
	public void onClose() {
		if (viewer.getPlayerStatus() == PlayerStatus.FIGHTING || viewer.getMatchData().getArenaSelection())
			return;

		viewer.openMenu(new SelectGametypeMenu(queuetype, type));
	}
}
