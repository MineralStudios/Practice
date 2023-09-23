package gg.mineral.practice.inventory.menus;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.match.BotMatch;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class QueueArenaEnableMenu extends PracticeMenu {
    final static String TITLE = CC.BLUE + "Toggle Arena";
    QueueEntry queueEntry;

    public QueueArenaEnableMenu(QueueEntry queueEntry) {
        super(TITLE);
        setClickCancelled(true);
        this.queueEntry = queueEntry;
    }

    @Override
    public boolean update() {
        clear();
        Iterator<Arena> arenas = queueEntry.getQueuetype().getCommonArenas(queueEntry.getGametype()).iterator();

        while (arenas.hasNext()) {
            Arena a = arenas.next();
            boolean arenaEnabled = viewer.getMatchData().getEnabledArenas().contains(a);

            ItemStack item = null;

            try {

                if (arenaEnabled) {
                    item = new ItemBuilder(a.getDisplayItem())
                            .name(CC.SECONDARY + CC.B + a.getDisplayName()).lore(CC.GREEN + "Click to disable arena.")
                            .build();
                } else {
                    item = ItemStacks.ARENA_DISABLED.name(a.getDisplayName()).build();
                }
            } catch (Exception e) {
                continue;
            }

            add(item, () -> {
                viewer.getMatchData().enableArena(a, !arenaEnabled);
                reload();
            });
        }

        setSlot(29, ItemStacks.DESELECT_ALL, interaction -> {
            viewer.getMatchData().getEnabledArenas().clear();
            reload();
        });

        setSlot(31, ItemStacks.APPLY, interaction -> {
            if (viewer.isInParty()) {
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

            MatchData data = new MatchData(queueEntry, viewer.getMatchData().getEnabledArenas());

            if (viewer.getMatchData().getTeam2v2()) {

                List<Difficulty> playerTeam = new GlueList<>();
                playerTeam.add(viewer.getMatchData().getBotDifficulty());

                List<Difficulty> opponentTeam = new GlueList<>();
                opponentTeam.add(Difficulty.RANDOM);
                opponentTeam.add(Difficulty.RANDOM);

                TeamMatch m = new TeamMatch(Arrays.asList(viewer), new GlueList<>(), playerTeam,
                        opponentTeam,
                        data);
                m.start();
                return;
            }

            viewer.getPlayer().closeInventory();
            BotMatch m = new BotMatch(viewer, viewer.getMatchData().getBotDifficulty(),
                    data);
            m.start();
            return;
        });

        setSlot(33, ItemStacks.SELECT_ALL, interaction -> {
            viewer.getMatchData().getEnabledArenas()
                    .addAll(queueEntry.getQueuetype().getCommonArenas(queueEntry.getGametype()));
            reload();
        });

        return true;
    }
}
