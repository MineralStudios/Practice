package gg.mineral.practice.inventory.menus;

import java.util.function.Consumer;

import org.bukkit.event.inventory.ClickType;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Interaction;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.QueueSettings.BotTeamSetting;
import gg.mineral.practice.queue.QueueSystem;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.queue.QueuetypeMenuEntry;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import lombok.NoArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
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

    protected Queuetype queuetype;
    protected Type type;
    protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> menuEntries;

    public SelectGametypeMenu(Queuetype queuetype, Type type) {
        this.queuetype = queuetype;
        this.type = type;
        this.menuEntries = setMenuEntries();
    }

    protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> setMenuEntries() {
        return queuetype.getMenuEntries();
    }

    public void queue(Gametype gametype, Interaction interact) {

        val viewer = interact.getProfile();
        val queueEntry = QueueSystem.getQueueEntry(viewer, queuetype, gametype);

        if (queueEntry != null) {
            viewer.removeFromQueue(queuetype, gametype);
            return;
        }

        Consumer<Interaction> queueInteraction = interaction -> {
            viewer.addPlayerToQueue(queuetype, gametype);

            if (!(viewer.getOpenMenu() instanceof SelectGametypeMenu)
                    && viewer.getPlayerStatus() == PlayerStatus.QUEUEING)
                viewer.openMenu(new SelectGametypeMenu(queuetype, type));
        };

        val queueSettings = viewer.getQueueSettings();
        if (queueSettings.isBotQueue() && (!queuetype.isBotsEnabled() || !gametype.isBotsEnabled())) {
            viewer.message(ErrorMessages.COMING_SOON);
            return;
        }

        if (viewer.getQueueSettings().isArenaSelection())
            viewer.openMenu(new QueueArenaEnableMenu(queuetype, gametype, queueInteraction));
        else
            queueInteraction.accept(interact);
    }

    protected static final Consumer<Interaction> TEAM_SIZE_INTERACTION = interaction -> {
        val viewer = interaction.getProfile();
        val queueSettings = viewer.getQueueSettings();
        queueSettings.setTeamSize((byte) (queueSettings.getTeamSize() % 8 + 1));

        val menu = viewer.getOpenMenu();

        if (menu != null)
            menu.reload();
    };

    protected static final Consumer<Interaction> DIFFICULTY_INTERACTION = interaction -> {

        val p = interaction.getProfile();

        val queueSettings = p.getQueueSettings();

        val menu = p.getOpenMenu();

        if (interaction.getClickType() == ClickType.LEFT) {
            queueSettings.setOpponentDifficulty((byte) ((queueSettings.getOpponentDifficulty() + 1)
                    % Difficulty.values().length));

            val difficulty = queueSettings.getOpponentBotDifficulty();

            if (difficulty == Difficulty.CUSTOM)
                queueSettings.setOpponentDifficulty((byte) ((queueSettings.getOpponentDifficulty() + 1)
                        % Difficulty.values().length));
        } else if (interaction.getClickType() == ClickType.RIGHT) {

            if (queueSettings.getTeamSize() <= 1) {
                if (p.getPlayer().hasPermission("practice.custombot")
                        && menu instanceof SelectGametypeMenu selectGametypeMenu)
                    p.openMenu(new CustomBotDifficultyMenu(selectGametypeMenu));
                else
                    ErrorMessages.RANK_REQUIRED.send(p.getPlayer());
                return;
            }

            if (queueSettings.getBotTeamSetting() == BotTeamSetting.BOTH)
                return;

            queueSettings.setTeammateDifficulty((byte) ((queueSettings.getTeammateDifficulty() + 1)
                    % Difficulty.values().length));

            val difficulty = queueSettings.getTeammateBotDifficulty();

            if (difficulty == Difficulty.CUSTOM)
                queueSettings.setTeammateDifficulty((byte) ((queueSettings.getTeammateDifficulty() + 1)
                        % Difficulty.values().length));

        }

        if (menu != null)
            menu.reload();
    };

    protected static final Consumer<Interaction> BOT_QUEUE_INTERACTION = interaction -> {
        val viewer = interaction.getProfile();
        val queueSettings = viewer.getQueueSettings();
        if (interaction.getClickType() == ClickType.LEFT)
            queueSettings.setBotQueue(!queueSettings.isBotQueue());
        else if (viewer.getQueueSettings().isBotQueue() && interaction.getClickType() == ClickType.RIGHT
                && queueSettings.getTeamSize() > 1 && !viewer.isInParty())
            queueSettings.setBotTeamSetting(BotTeamSetting.values()[(queueSettings.getBotTeamSetting().ordinal()
                    + 1) % BotTeamSetting.values().length]);

        val menu = viewer.getOpenMenu();
        val botQueue = queueSettings.isBotQueue();

        if (menu != null)
            if (menu instanceof SelectCategorizedGametypeMenu selectCategorizedGametypeMenu && botQueue
                    && !selectCategorizedGametypeMenu.catagory.isBotsEnabled())
                viewer.openMenu(new SelectGametypeMenu(selectCategorizedGametypeMenu.queuetype,
                        selectCategorizedGametypeMenu.type));
            else
                menu.reload();
    };

    protected static final Consumer<Interaction> ARENA_INTERACTION = interaction -> {
        val viewer = interaction.getProfile();
        val queueSettings = viewer.getQueueSettings();
        queueSettings.setArenaSelection(!queueSettings.isArenaSelection());

        val menu = viewer.getOpenMenu();

        if (menu != null)
            menu.reload();
    };

    protected void addSurroundingButtons(QueueSettings queueSettings, boolean botQueue) {
        if (type != Type.UNRANKED)
            return;

        if (!viewer.isInParty()) {
            val teamSize = queueSettings.getTeamSize();
            setSlot(botQueue ? 2 : 3,
                    ItemStacks.TEAMFIGHT.lore(
                            CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "team" + CC.WHITE + " match.",
                            " ", CC.WHITE + "Currently:", teamSizeColors.get(teamSize), " ", CC.BOARD_SEPARATOR,
                            CC.ACCENT + "Click to change team size.").build(),
                    TEAM_SIZE_INTERACTION);
        } else
            queueSettings.setTeamSize((byte) (viewer.getParty().getPartyMembers().size()));

        val opponentDifficulty = queueSettings.getOpponentDifficulty();
        val teamDifficulty = queueSettings.getTeammateDifficulty();

        var item = ItemStacks.BOT_QUEUE_DISABLED;
        if (botQueue) {
            if (queueSettings.getTeamSize() == 1)
                setSlot(4,
                        ItemStacks.BOT_SETTINGS.lore(
                                CC.WHITE + "Allows you to configure the " + CC.SECONDARY + "difficulty" + CC.WHITE
                                        + ".",
                                " ", CC.WHITE + "Selected Difficulty: ",
                                Difficulty.values()[opponentDifficulty].getDisplay(), " ",
                                CC.BOARD_SEPARATOR, " ", CC.GREEN + "Left Click to change difficulty.",
                                CC.RED + "Right Click to configure custom difficulty.").build(),
                        DIFFICULTY_INTERACTION);
            else if (queueSettings.getBotTeamSetting() == BotTeamSetting.BOTH)
                setSlot(4,
                        ItemStacks.BOT_SETTINGS.lore(
                                CC.WHITE + "Allows you to configure the " + CC.SECONDARY + "difficulty" + CC.WHITE
                                        + ".",
                                " ",
                                CC.WHITE + "Opponent Difficulty: ",
                                Difficulty.values()[opponentDifficulty].getDisplay(), " ",
                                CC.WHITE + "Team Difficulty: ",
                                Difficulty.values()[teamDifficulty].getDisplay(), " ",
                                CC.BOARD_SEPARATOR, " ", CC.GREEN + "Left Click to change opponent difficulty.",
                                CC.RED + "Right Click to change team difficulty.").build(),
                        DIFFICULTY_INTERACTION);
            else
                setSlot(4,
                        ItemStacks.BOT_SETTINGS.lore(
                                CC.WHITE + "Allows you to configure the " + CC.SECONDARY + "difficulty" + CC.WHITE
                                        + ".",
                                " ",
                                CC.WHITE + "Opponent Difficulty: ",
                                Difficulty.values()[opponentDifficulty].getDisplay(), " ",
                                CC.BOARD_SEPARATOR, " ", CC.GREEN + "Left Click to change opponent difficulty.")
                                .build(),
                        DIFFICULTY_INTERACTION);

            if (queueSettings.getTeamSize() > 1 && !viewer.isInParty()) {
                switch (queueSettings.getBotTeamSetting()) {
                    case BOTH:
                        item = ItemStacks.BOT_QUEUE_ENABLED_TEAM.lore(
                                CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "team bot" + CC.WHITE
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
                        break;
                    case OPPONENT:
                        item = ItemStacks.BOT_QUEUE_ENABLED_TEAM.lore(
                                CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "team bot" + CC.WHITE
                                        + " match ",
                                CC.WHITE + "with a bots as your opponents.",
                                " ",
                                CC.WHITE + "Currently:",
                                CC.GREEN + "Enabled",
                                " ", CC.WHITE + "Team Settings:", CC.AQUA + "Bot Opponents", " ",
                                CC.BOARD_SEPARATOR,
                                CC.GREEN + "Left click to toggle bots.",
                                CC.RED + "Right click to change team settings.")
                                .build();
                        break;
                }
            } else
                item = ItemStacks.BOT_QUEUE_ENABLED;
        }

        setSlot(botQueue ? 6 : 5, item, BOT_QUEUE_INTERACTION);

        setSlot(48, ItemStacks.RANDOM_QUEUE, interaction -> {
            val gametype = viewer.getQueueSettings().isBotQueue() ? queuetype.randomGametypeWithBotsEnabled()
                    : queuetype.randomGametype();

            queue(gametype, interaction);

            if (viewer.getOpenMenu() instanceof SelectGametypeMenu)
                viewer.getPlayer().closeInventory();
        });

        val arenaSelection = viewer.getQueueSettings().isArenaSelection();

        setSlot(50,
                ItemStacks.ARENA.lore(
                        CC.WHITE + "Select an " + CC.SECONDARY + "arena" + CC.WHITE + " when you queue.", " ",
                        CC.WHITE + "Currently:", arenaSelection ? CC.GREEN + "Enabled" : CC.RED + "Disabled", " ",
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle arena selection.").build(),
                ARENA_INTERACTION);
    }

    protected boolean shouldSkip(QueuetypeMenuEntry menuEntry) {
        return menuEntry instanceof Gametype gametype && gametype.isInCatagory();
    }

    @Override
    public void update() {
        clear();
        val queueSettings = viewer.getQueueSettings();
        val botQueue = viewer.getQueueSettings().isBotQueue();

        addSurroundingButtons(queueSettings, botQueue);

        int offset = 0;

        for (val entry : menuEntries
                .object2IntEntrySet()) {

            val menuEntry = entry.getKey();

            if (shouldSkip(menuEntry))
                continue;

            if (botQueue && !menuEntry.isBotsEnabled()) {
                offset++;
                continue;
            }

            val itemBuild = new ItemBuilder(menuEntry.getDisplayItem().clone())
                    .name(CC.SECONDARY + CC.B + menuEntry.getDisplayName());

            if (menuEntry instanceof Gametype gametype) {
                val teamSize = queueSettings.getTeamSize();
                val queueEntry = QueueSettings.toEntry(queuetype, gametype, teamSize, queueSettings.isBotQueue(),
                        queueSettings.getOpponentDifficulty(),
                        queueSettings.getBotTeamSetting(),
                        queueSettings.getEnabledArenas());

                if (type == Type.QUEUE || type == Type.UNRANKED) {

                    if (QueueSystem.getQueueEntry(viewer, queuetype, gametype) != null)
                        itemBuild.lore(CC.RED + "Click to leave queue.");
                    else
                        itemBuild.lore(
                                CC.SECONDARY + "In Queue: " + CC.WHITE
                                        + QueueSystem.getCompatibleQueueCount(queueEntry.queuetype(),
                                                queueEntry.gametype()),
                                CC.SECONDARY + "In Game: " + CC.WHITE
                                        + MatchManager.getInGameCount(queuetype, gametype),
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to queue.");
                } else
                    itemBuild.lore();

                val item = itemBuild.build();

                setSlot(type == Type.UNRANKED ? entry.getIntValue() + 18 - offset : entry.getIntValue() - offset, item,
                        interaction -> {

                            if (type == Type.KIT_EDITOR) {
                                viewer.getPlayer().closeInventory();
                                viewer.sendToKitEditor(queuetype, gametype);
                                return;
                            }

                            queue(gametype, interaction);

                            val menu = viewer.getOpenMenu();
                            if (menu != null && menu.equals(this) && viewer.getPlayerStatus() == PlayerStatus.QUEUEING)
                                reload();
                        });
                continue;
            }

            if (menuEntry instanceof Catagory catagory) {
                val sb = new GlueList<String>();
                sb.add(CC.SECONDARY + "Includes:");

                for (val g : catagory.getGametypes()) {
                    boolean isQueued = QueueSystem.getQueueEntry(viewer, queuetype, g) != null;
                    sb.add(CC.WHITE + g.getDisplayName() + (isQueued ? " - " + CC.GREEN + "Queued" : ""));
                }

                sb.add(" ");
                sb.add(CC.BOARD_SEPARATOR);
                sb.add(CC.ACCENT + "Click to view catagory.");

                itemBuild.lore(sb.toArray(new String[0]));
                val item = itemBuild.build();
                setSlot(type == Type.UNRANKED ? entry.getIntValue() + 18 - offset : entry.getIntValue() - offset, item,
                        interaction -> interaction.getProfile()
                                .openMenu(new SelectCategorizedGametypeMenu(queuetype, catagory, type)));
            }
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
