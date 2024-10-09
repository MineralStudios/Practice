package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Interaction;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.match.BotMatch;
import gg.mineral.practice.match.BotTeamMatch;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.QueueSystem;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.bytes.ByteIterator;

import java.util.function.Consumer;
import java.util.List;
import java.util.UUID;

@ClickCancelled(true)
public class QueueArenaEnableMenu extends PracticeMenu {
    private final Queuetype queuetype;
    private final Gametype gametype;
    private final Consumer<Interaction> queueInteraction;

    public QueueArenaEnableMenu(Queuetype queuetype, Gametype gametype, Consumer<Interaction> queueInteraction) {
        this.queuetype = queuetype;
        this.gametype = gametype;
        this.queueInteraction = interaction -> {
            QueueSettings queueSettings = viewer.getQueueSettings();
            MatchData data = new MatchData(queuetype, gametype, queueSettings);

            int teamSize = queueSettings.getTeamSize();

            List<Profile> playerList = new GlueList<>();

            Profile viewer = interaction.getProfile();

            if (viewer.isInParty()) {
                playerList.addAll(viewer.getParty().getPartyMembers());
            } else {
                playerList.add(viewer);
            }

            if (queueSettings.isBotQueue()) {
                List<BotConfiguration> playerTeam = new GlueList<>();
                for (byte i = 0; i < queueSettings.getPlayerBots(); i++)
                    playerTeam.add(
                            Difficulty.values()[queueSettings.getTeamDifficulties().get(i)]
                                    .getConfiguration(queueSettings));

                List<BotConfiguration> opponentTeam = new GlueList<>();
                for (byte i = 0; i < queueSettings.getOpponentBots(); i++)
                    opponentTeam.add(
                            Difficulty.values()[queueSettings.getTeamDifficulties().get(i)]
                                    .getConfiguration(queueSettings));
                viewer.getPlayer().closeInventory();
                if (teamSize > 1 && playerList.size() + playerTeam.size() == teamSize
                        && opponentTeam.size() == teamSize) {
                    TeamMatch m = new BotTeamMatch(playerList, new GlueList<>(), playerTeam,
                            opponentTeam,
                            data);
                    m.start();
                    return;
                } else if (teamSize == 1 && queueSettings.isBotQueue()) {
                    BotMatch m = new BotMatch(playerList.get(0), opponentTeam.get(0),
                            data);
                    m.start();
                }
            }

            List<UUID> queueEntries = QueueSystem.getQueueEntries(viewer, queuetype, gametype);

            if (queueEntries != null && !queueEntries.isEmpty())
                viewer.removeFromQueue(queuetype, gametype);
            else
                viewer.addPlayerToQueue(queuetype, gametype);
        };
    }

    @Override
    public void update() {
        clear();
        ByteIterator arenas = queuetype.filterArenasByGametype(gametype).iterator();

        QueueSettings queueSettings = viewer.getQueueSettings();

        while (arenas.hasNext()) {
            byte arenaId = arenas.nextByte();
            boolean arenaEnabled = queueSettings.getEnabledArenas().get(arenaId);

            ItemStack item = null;

            Arena arena = ArenaManager.getArenas().get(arenaId);

            if (arena == null)
                continue;

            try {

                if (arenaEnabled) {
                    item = new ItemBuilder(arena.getDisplayItem())
                            .name(CC.SECONDARY + CC.B + arena.getDisplayName())
                            .lore(CC.GREEN + "Click to disable arena.")
                            .build();
                } else {
                    item = ItemStacks.ARENA_DISABLED.name(arena.getDisplayName()).build();
                }
            } catch (Exception e) {
                continue;
            }

            add(item, () -> {
                queueSettings.enableArena(arena, !arenaEnabled);
                reload();
            });
        }

        setSlot(29, ItemStacks.DESELECT_ALL, interaction -> {
            queueSettings.getEnabledArenas().clear();
            reload();
        });

        setSlot(31, ItemStacks.APPLY, queueInteraction);

        setSlot(33, ItemStacks.SELECT_ALL, interaction -> {
            for (byte arenaId : queuetype.filterArenasByGametype(gametype))
                viewer.getQueueSettings().enableArena(arenaId, true);

            reload();
        });
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Toggle Arena";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
