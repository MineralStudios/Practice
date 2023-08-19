package gg.mineral.practice.inventory.menus;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.QueueEntryManager;
import gg.mineral.practice.match.BotMatch;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.QueueSearchTask2v2;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class SelectGametypeMenu extends PracticeMenu {

	public enum Type {
		QUEUE, KIT_EDITOR, UNRANKED
	}

	Queuetype queuetype;
	Type type;

	public SelectGametypeMenu(Queuetype queuetype, Type type) {
		super(CC.BLUE + queuetype.getDisplayName());
		this.type = type;
		this.queuetype = queuetype;
		setClickCancelled(true);
	}

	public SelectGametypeMenu(Queuetype queuetype, Type type, boolean botQueue) {
		super(CC.BLUE + queuetype.getDisplayName());
		this.type = type;
		this.queuetype = queuetype;
		setClickCancelled(true);
	}

	public void queue(QueueEntry queueEntry) {
		List<QueueEntry> queueEntries = viewer.getMatchData().getTeam2v2()
				? QueueSearchTask2v2.getQueueEntries(viewer)
				: QueueSearchTask.getQueueEntries(viewer);

		if (queueEntries != null && queueEntries.contains(queueEntry)) {
			viewer.removeFromQueue(queueEntry);
		} else {
			if (viewer.getMatchData().getTeam2v2()) {

				boolean botOpponents1 = viewer.getMatchData().getBotQueue();
				boolean botTeammate1 = viewer.getMatchData().getBotTeammate();

				if (botOpponents1 && botTeammate1) {

					if (!(queueEntry.getGametype().getBotsEnabled()
							&& queueEntry.getQueuetype().getBotsEnabled())) {
						ErrorMessages.COMING_SOON.send(viewer.getPlayer());
						return;
					}

					if (viewer.getMatchData().getArenaSelection()) {
						viewer.openMenu(new QueueArenaEnableMenu(queueEntry));
						return;
					}

					List<Difficulty> playerTeam = new GlueList<>();
					playerTeam.add(viewer.getMatchData().getBotDifficulty());

					List<Difficulty> opponentTeam = new GlueList<>();
					opponentTeam.add(Difficulty.RANDOM);
					opponentTeam.add(Difficulty.RANDOM);

					TeamMatch m = new TeamMatch(Arrays.asList(viewer), new GlueList<>(), playerTeam,
							opponentTeam,
							new MatchData(queueEntry));
					m.start();
				} else if (botOpponents1 || botTeammate1) {
					if (!(queueEntry.getGametype().getBotsEnabled()
							&& queueEntry.getQueuetype().getBotsEnabled())) {
						ErrorMessages.COMING_SOON.send(viewer.getPlayer());
						return;
					}

					if (viewer.isInParty()) {

						if (viewer.getMatchData().getArenaSelection()) {
							viewer.openMenu(new QueueArenaEnableMenu(queueEntry));
							return;
						}

						List<Difficulty> opponentTeam = new GlueList<>();
						opponentTeam.add(viewer.getMatchData().getBotDifficulty());
						opponentTeam.add(viewer.getMatchData().getBotDifficulty());

						TeamMatch m = new TeamMatch(viewer.getParty().getPartyMembers(), new GlueList<>(),
								new GlueList<>(),
								opponentTeam,
								new MatchData(queueEntry));
						m.start();
						return;
					}

					viewer.addPlayerToQueue(queueEntry);
				} else {
					viewer.addPlayerToQueue(queueEntry);
				}

				return;
			}

			if (viewer.getMatchData().getBotQueue()) {
				if (!(queueEntry.getGametype().getBotsEnabled()
						&& queueEntry.getQueuetype().getBotsEnabled())) {
					ErrorMessages.COMING_SOON.send(viewer.getPlayer());
					return;
				}

				if (viewer.getMatchData().getArenaSelection()) {
					viewer.openMenu(new QueueArenaEnableMenu(queueEntry));
					return;
				}

				viewer.getPlayer().closeInventory();
				BotMatch m = new BotMatch(viewer, viewer.getMatchData().getBotDifficulty(),
						new MatchData(queueEntry));
				m.start();
				return;
			} else {
				viewer.addPlayerToQueue(queueEntry);
			}
		}
	}

	@Override
	public boolean update() {

		if (type == Type.UNRANKED) {
			if (!viewer.isInParty()) {
				setSlot(2, ItemStacks.TEAMFIGHT.lore("2v2: " + viewer.getMatchData().getTeam2v2(), " ",
						CC.GREEN + "Left Click to toggle 2v2").build(), interaction -> {
							viewer.getMatchData().setTeam2v2(!viewer.getMatchData().getTeam2v2());
							reload();
						});
			} else {
				viewer.getMatchData().setTeam2v2(true);
			}

			setSlot(viewer.isInParty() ? 2 : 4,
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

			if (viewer.getMatchData().getTeam2v2() && !viewer.isInParty()) {
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
			} else if (viewer.getMatchData().getBotQueue()) {
				item = ItemStacks.BOT_QUEUE_ENABLED.lore().build();
			}

			setSlot(6,
					item,
					interaction -> {
						if (interaction.getClickType() == ClickType.RIGHT && viewer.getMatchData().getTeam2v2()
								&& !viewer.isInParty()) {
							if (botOpponents && botTeammate) {
								viewer.getMatchData().setBotTeammate(false);
							} else if (botOpponents) {
								viewer.getMatchData().setBotTeammate(true);
								viewer.getMatchData().setBotQueue(false);
							} else if (botTeammate) {
								viewer.getMatchData().setBotTeammate(true);
								viewer.getMatchData().setBotQueue(true);
							}
						} else if (interaction.getClickType() == ClickType.LEFT) {
							if (botOpponents || botTeammate) {
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
			});

			boolean arenaSelection = viewer.getMatchData().getArenaSelection();

			String color = arenaSelection ? CC.GREEN : CC.RED;

			setSlot(50, ItemStacks.ARENA.name("Toggle Arena Selection").lore(color + arenaSelection).build(), () -> {
				viewer.getMatchData().setArenaSelection(!viewer.getMatchData().getArenaSelection());
				reload();
			});
		}

		for (Entry<Gametype, Integer> entry : queuetype.getGametypes().object2IntEntrySet()) {

			Gametype g = entry.getKey();

			if (g.isInCatagory()) {
				continue;
			}

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

			setSlot(type == Type.UNRANKED ? entry.getValue() + 18 : entry.getValue(), item, () -> {

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

		for (Entry<Catagory, Integer> entry : queuetype.getCatagories().object2IntEntrySet()) {
			Catagory c = entry.getKey();
			ItemBuilder itemBuild = new ItemBuilder(c.getDisplayItem())
					.name(c.getDisplayName());

			List<String> sb = new GlueList<String>();
			sb.add(CC.SECONDARY + "Includes:");

			for (Gametype g : c.getGametypes()) {
				sb.add(g.getDisplayName());
			}

			itemBuild.lore(sb.toArray(new String[0]));
			ItemStack item = itemBuild.build();
			setSlot(type == Type.UNRANKED ? entry.getValue() + 18 : entry.getValue(), item, interaction -> {
				Profile p = interaction.getProfile();
				p.openMenu(new SelectCategorizedGametypeMenu(queuetype, c, type));
			});
		}

		return true;
	}
}
