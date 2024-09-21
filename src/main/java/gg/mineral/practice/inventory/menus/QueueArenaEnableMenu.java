package gg.mineral.practice.inventory.menus;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.match.BotMatch;
import gg.mineral.practice.match.BotTeamMatch;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.QueueMatchData;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class QueueArenaEnableMenu extends PracticeMenu {
    private final QueueEntry queueEntry;

    @Override
    public void update() {
        clear();
        Iterator<Arena> arenas = queueEntry.getQueuetype().filterArenasByGametype(queueEntry.getGametype()).iterator();

        while (arenas.hasNext()) {
            Arena a = arenas.next();
            boolean arenaEnabled = viewer.getMatchData().getEnabledArenas().getBoolean(a);

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
            QueueMatchData data = viewer.getMatchData()
                    .cloneBotAndArenaData(enabledArenas -> new QueueMatchData(queueEntry, enabledArenas));

            if (viewer.isInParty()) {
                List<Difficulty> opponentTeam = new GlueList<>();
                Difficulty difficulty = data.getBotDifficulty();
                opponentTeam.add(difficulty);
                opponentTeam.add(difficulty);

                TeamMatch m = new BotTeamMatch(viewer.getParty().getPartyMembers(), new GlueList<>(),
                        new GlueList<>(),
                        opponentTeam,
                        data);
                m.start();
                return;
            }

            data.getEnabledArenas().putAll(viewer.getMatchData().getEnabledArenas());

            if (viewer.getMatchData().isTeam2v2()) {

                List<Difficulty> playerTeam = new GlueList<>();
                playerTeam.add(viewer.getMatchData().getBotDifficulty());

                List<Difficulty> opponentTeam = new GlueList<>();
                opponentTeam.add(Difficulty.RANDOM);
                opponentTeam.add(Difficulty.RANDOM);

                TeamMatch m = new BotTeamMatch(Arrays.asList(viewer), new GlueList<>(), playerTeam,
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
            for (Arena a : queueEntry.getQueuetype().filterArenasByGametype(queueEntry.getGametype()))
                viewer.getMatchData().enableArena(a, true);

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
