package gg.mineral.practice.inventory.menus;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Interaction;
import gg.mineral.practice.inventory.Menu;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.BotMatch;
import gg.mineral.practice.match.BotTeamMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.QueueSettings.QueueEntry;
import gg.mineral.practice.queue.QueueSystem;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectGametypeMenu extends PracticeMenu {

	protected final static Int2ObjectOpenHashMap<String> teamSizeColors = new Int2ObjectOpenHashMap<String>() {
		{
			put(1, CC.RED + "1v1");
			put(2, CC.GREEN + "2v2");
			put(3, CC.AQUA + "3v3");
			put(4, CC.YELLOW + "4v4");
			put(5, CC.PINK + "5v5");
			put(6, CC.GOLD + "6v6");
			put(7, CC.BLUE + "7v7");
			put(8, CC.PURPLE + "8v8");
		}
	};

	public enum Type {
		QUEUE, KIT_EDITOR, UNRANKED
	}

	protected final Queuetype queuetype;
	protected final Type type;

	public void queue(Gametype gametype, Interaction interact) {

		@Nullable
		QueueEntry queueEntry = QueueSystem.getQueueEntry(viewer, queuetype, gametype);

		if (queueEntry != null) {
			viewer.removeFromQueue(queuetype, gametype);
			return;
		}

		Consumer<Interaction> queueInteraction = interaction -> {
			Profile viewer = interaction.getProfile();
			QueueSettings queueSettings = viewer.getQueueSettings();
			MatchData data = new MatchData(queuetype, gametype, queueSettings);

			int teamSize = queueSettings.getTeamSize();

			List<Profile> playerList = new GlueList<>();

			if (viewer.isInParty())
				playerList.addAll(viewer.getParty().getPartyMembers());
			else
				playerList.add(viewer);

			if (queueSettings.isBotQueue()) {
				List<BotConfiguration> playerTeam = new GlueList<>();
				for (byte i = 0; i < (queueSettings.isTeammateBot() ? Math.max(0, teamSize - 1) : 0); i++)
					playerTeam.add(
							queueSettings.getBotDifficulty()
									.getConfiguration(queueSettings));

				List<BotConfiguration> opponentTeam = new GlueList<>();
				for (byte i = 0; i < (queueSettings.isOpponentBot() ? teamSize : 0); i++)
					opponentTeam.add(
							queueSettings.getBotDifficulty()
									.getConfiguration(queueSettings));
				viewer.getPlayer().closeInventory();
				if (teamSize > 1 && playerList.size() + playerTeam.size() == teamSize
						&& opponentTeam.size() == teamSize) {
					new BotTeamMatch(playerList, new GlueList<>(), playerTeam,
							opponentTeam,
							data).start();
					return;
				} else if (teamSize == 1 && queueSettings.isBotQueue() && !playerList.isEmpty()
						&& !opponentTeam.isEmpty()) {
					new BotMatch(playerList.get(0), opponentTeam.get(0),
							data).start();
					return;
				}
			}

			viewer.addPlayerToQueue(queuetype, gametype);

			if (!(viewer.getOpenMenu() instanceof SelectGametypeMenu))
				viewer.openMenu(new SelectGametypeMenu(queuetype, type));
		};

		if (viewer.getQueueSettings().isArenaSelection())
			viewer.openMenu(new QueueArenaEnableMenu(queuetype, gametype, queueInteraction));
		else
			queueInteraction.accept(interact);
	}

	protected static final Consumer<Interaction> TEAM_SIZE_INTERACTION = interaction -> {
		Profile viewer = interaction.getProfile();
		QueueSettings queueSettings = viewer.getQueueSettings();
		queueSettings.setTeamSize((byte) (queueSettings.getTeamSize() % 8 + 1));

		Menu menu = viewer.getOpenMenu();

		if (menu != null)
			menu.reload();
	};

	protected static final Consumer<Interaction> DIFFICULTY_INTERACTION = interaction -> {

		Profile p = interaction.getProfile();

		QueueSettings queueSettings = p.getQueueSettings();

		Menu menu = p.getOpenMenu();

		if (interaction.getClickType() == ClickType.LEFT) {

			Difficulty newDifficulty = Difficulty.values()[(queueSettings.getDifficulty() + 1)
					% Difficulty.values().length];

			if (newDifficulty == Difficulty.CUSTOM)
				newDifficulty = Difficulty.values()[(queueSettings.getDifficulty() + 2) % Difficulty.values().length];

			queueSettings
					.setDifficulty((byte) newDifficulty.ordinal());
		} else if (interaction.getClickType() == ClickType.RIGHT) {

			if (p.getPlayer().hasPermission("practice.custombot")
					&& menu instanceof SelectGametypeMenu selectGametypeMenu)
				p.openMenu(new CustomBotDifficultyMenu(selectGametypeMenu));
			else
				ErrorMessages.RANK_REQUIRED.send(p.getPlayer());
			return;
		}

		if (menu != null)
			menu.reload();
	};

	protected static final Consumer<Interaction> BOT_QUEUE_INTERACTION = interaction -> {
		Profile viewer = interaction.getProfile();
		QueueSettings queueSettings = viewer.getQueueSettings();
		boolean botOpponents = queueSettings.isOpponentBot(), botTeammate = queueSettings.isTeammateBot();
		if (interaction.getClickType() == ClickType.LEFT)
			queueSettings.setBotQueue(!queueSettings.isBotQueue());
		else if (viewer.getQueueSettings().isBotQueue() && interaction.getClickType() == ClickType.RIGHT
				&& queueSettings.getTeamSize() > 1
				&& !viewer.isInParty()) {
			if (botOpponents && botTeammate)
				queueSettings.setTeammateBot(false);
			else if (botOpponents) {
				queueSettings.setTeammateBot(true);
				queueSettings.setOpponentBot(false);
			} else if (botTeammate) {
				queueSettings.setTeammateBot(true);
				queueSettings.setOpponentBot(true);
			}
		}

		Menu menu = viewer.getOpenMenu();

		if (menu != null)
			menu.reload();
	};

	protected static final Consumer<Interaction> ARENA_INTERACTION = interaction -> {
		Profile viewer = interaction.getProfile();
		QueueSettings queueSettings = viewer.getQueueSettings();
		queueSettings.setArenaSelection(!queueSettings.isArenaSelection());

		Menu menu = viewer.getOpenMenu();

		if (menu != null)
			menu.reload();
	};

	@Override
	public void update() {
		QueueSettings queueSettings = viewer.getQueueSettings();
		if (!queueSettings.isOpponentBot() && !queueSettings.isTeammateBot()) {
			queueSettings.setOpponentBot(true);
			queueSettings.setTeammateBot(true);
		}
		boolean botOpponents = queueSettings.isOpponentBot(), botTeammate = queueSettings.isTeammateBot();

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

			byte difficulty = queueSettings.getDifficulty();
			setSlot(4,
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

		for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Gametype> entry : queuetype.getGametypes()
				.object2IntEntrySet()) {

			Gametype gametype = entry.getKey();

			if (gametype.isInCatagory())
				continue;

			ItemBuilder itemBuild = new ItemBuilder(gametype.getDisplayItem().clone())
					.name(CC.SECONDARY + CC.B + gametype.getDisplayName());

			byte teamSize = queueSettings.getTeamSize();
			QueueEntry queueEntry = QueueSettings.toEntry(queuetype, gametype, teamSize,
					botTeammate,
					botOpponents, queueSettings.getEnabledArenas());

			if (type == Type.QUEUE || type == Type.UNRANKED) {

				if (QueueSystem.getQueueEntry(viewer, queuetype, gametype) != null)
					itemBuild.lore(CC.RED + "Click to leave queue.");
				else
					itemBuild.lore(
							CC.SECONDARY + "In Queue: " + CC.WHITE
									+ QueueSystem.getCompatibleQueueCount(queueEntry.queuetype(),
											queueEntry.gametype()),
							CC.SECONDARY + "In Game: " + CC.WHITE + MatchManager.getInGameCount(queuetype, gametype),
							CC.BOARD_SEPARATOR,
							CC.ACCENT + "Click to queue.");
			} else
				itemBuild.lore();

			ItemStack item = itemBuild.build();

			setSlot(type == Type.UNRANKED ? entry.getIntValue() + 18 : entry.getIntValue(), item, interaction -> {

				if (type == Type.KIT_EDITOR) {
					viewer.getPlayer().closeInventory();
					viewer.sendToKitEditor(queuetype, gametype);
					return;
				}

				queue(gametype, interaction);

				reload();
			});
		}

		for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Catagory> entry : queuetype.getCatagories()
				.object2IntEntrySet()) {
			Catagory c = entry.getKey();
			ItemBuilder itemBuild = new ItemBuilder(c.getDisplayItem())
					.name(CC.SECONDARY + CC.B + c.getDisplayName());

			List<String> sb = new GlueList<String>();
			sb.add(CC.SECONDARY + "Includes:");

			for (Gametype g : c.getGametypes())
				sb.add(CC.WHITE + g.getDisplayName());

			sb.add(" ");
			sb.add(CC.BOARD_SEPARATOR);
			sb.add(CC.ACCENT + "Click to view catagory.");

			itemBuild.lore(sb.toArray(new String[0]));
			ItemStack item = itemBuild.build();
			setSlot(type == Type.UNRANKED ? entry.getIntValue() + 18 : entry.getIntValue(), item,
					interaction -> interaction
							.getProfile().openMenu(new SelectCategorizedGametypeMenu(queuetype, c, type)));
		}
	}

	@Override
	public String getTitle() {
		return CC.BLUE + queuetype.getDisplayName();
	}

	@Override
	public boolean shouldUpdate() {
		return true;
	}
}
