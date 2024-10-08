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

import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.QueueSystem;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import java.util.UUID;

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
			QueueSettings queueSettings = viewer.getQueueSettings();
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
						interaction -> {
							queueSettings.setTeamSize((byte) (teamSize % 8 + 1));
							reload();
						});
			} else
				queueSettings.setTeamSize((byte) (viewer.getParty().getPartyMembers().size()));

			if (queueSettings.getTeamSize() == 1) {
				int difficulty = queueSettings.getOpponentDifficulties()[0];
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
						interaction -> {
							if (interaction.getClickType() == ClickType.LEFT) {

								Difficulty newDifficulty = Difficulty.values()[(difficulty + 1)
										% Difficulty.values().length];

								if (newDifficulty == Difficulty.CUSTOM)
									newDifficulty = Difficulty.values()[(difficulty + 2) % Difficulty.values().length];

								viewer.getQueueSettings()
										.setOpponentDifficulty(0, newDifficulty);
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
			}

			ItemStack item = ItemStacks.BOT_QUEUE_DISABLED;

			if (viewer.getQueueSettings().isBotQueue()) {
				if (queueSettings.getTeamSize() > 1 && !viewer.isInParty()) {
					item = ItemStacks.BOT_QUEUE_ENABLED_TEAM.lore(
							CC.WHITE + "Allows you to queue bots in a " + CC.SECONDARY + "team" + CC.WHITE
									+ " match ",
							CC.WHITE + "with bot teammates and bot opponents.",
							" ",
							CC.WHITE + "Currently:",
							CC.GREEN + "Enabled",
							" ",
							CC.BOARD_SEPARATOR,
							CC.GREEN + "Left click to toggle bots.", CC.RED + "Right click to change team settings.")
							.build();
				} else
					item = ItemStacks.BOT_QUEUE_ENABLED;
			}

			setSlot(6,
					item,
					interaction -> {
						if (interaction.getClickType() == ClickType.RIGHT && queueSettings.getTeamSize() > 1
								&& !viewer.isInParty()) {
							viewer.openMenu(new BotTeamSettingsMenu());
						} else if (interaction.getClickType() == ClickType.LEFT)
							queueSettings.setBotQueue(!queueSettings.isBotQueue());

						reload();
					});

			setSlot(48, ItemStacks.RANDOM_QUEUE, () -> {
				Gametype gametype = viewer.getQueueSettings().isBotQueue() ? queuetype.randomGametypeWithBotsEnabled()
						: queuetype.randomGametype();

				queue(queuetype, gametype);

				if (viewer.getOpenMenu() instanceof SelectGametypeMenu)
					viewer.getPlayer().closeInventory();
			});

			boolean arenaSelection = viewer.getQueueSettings().isArenaSelection();

			setSlot(50, ItemStacks.ARENA.lore(CC.WHITE + "Select an " + CC.SECONDARY + "arena" + CC.WHITE
					+ " when you queue.",
					" ",
					CC.WHITE + "Currently:",
					arenaSelection ? CC.GREEN + "Enabled" : CC.RED + "Disabled",
					" ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle arena selection.").build(), () -> {
						viewer.getQueueSettings().setArenaSelection(!viewer.getQueueSettings().isArenaSelection());
						reload();
					});
		}

		for (Gametype g : catagory.getGametypes()) {

			if (g.isInCatagory())
				continue;

			ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem().clone())
					.name(CC.SECONDARY + CC.B + g.getDisplayName());

			QueueSettings queueSettings = viewer.getQueueSettings();
			UUID queueEntry = QueueSettings.toUUID(queuetype, g, queueSettings.getTeamSize(),
					queueSettings.getPlayerBots(),
					queueSettings.getOpponentBots(), queueSettings.getEnabledArenas());

			if (type == Type.QUEUE || type == Type.UNRANKED) {
				List<UUID> queueEntries = QueueSystem.getQueueEntries(viewer, queuetype, g);

				if (queueEntries != null && !queueEntries.isEmpty())
					itemBuild.lore(CC.RED + "Click to leave queue.");
				else
					itemBuild.lore(
							CC.SECONDARY + "In Queue: " + CC.WHITE + QueueSystem.getCompatibleQueueCount(queueEntry),
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
							viewer.sendToKitEditor(queuetype, g);
							return;
						}

						queue(queuetype, g);

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
