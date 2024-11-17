package gg.mineral.practice.inventory.menus;

import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Interaction;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.MatchData;

import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectArenaMenu extends PracticeMenu {
    private MechanicsMenu menu;
    private final SubmitAction action;
    private boolean simpleMode = true;

    public SelectArenaMenu(MechanicsMenu menu, SubmitAction action) {
        this(action);
        this.menu = menu;
        this.simpleMode = false;
    }

    @Override
    public void update() {
        val arenas = ArenaManager.getArenas();
        val gametype = viewer.getDuelSettings().getGametype();
        val arenaIds = simpleMode && gametype != null ? gametype.getArenas().iterator()
                : arenas.keySet().iterator();

        while (arenaIds.hasNext()) {
            byte arenaId = arenaIds.nextByte();

            val arena = arenas.get(arenaId);

            ItemStack item;
            try {
                item = new ItemBuilder(arena.getDisplayItem().clone())
                        .name(CC.SECONDARY + CC.B + arena.getDisplayName()).lore(CC.ACCENT + "Click to select.")
                        .build();
            } catch (Exception e) {
                continue;
            }

            Consumer<Interaction> arenaRunnable = interaction -> {
                viewer.getDuelSettings().setArenaId(arenaId);

                if (simpleMode) {
                    viewer.getPlayer().closeInventory();
                    viewer.getRequestHandler().sendDuelRequest(viewer.getRequestHandler().getDuelRequestReciever());
                    return;
                }

                viewer.openMenu(menu);
            };

            if (action == SubmitAction.P_SPLIT && simpleMode) {
                arenaRunnable = interaction -> {
                    viewer.getPlayer().closeInventory();

                    viewer.getDuelSettings().setArenaId(arenaId);

                    val p = viewer.getParty();

                    if (!viewer.getParty().getPartyLeader().equals(viewer)) {
                        viewer.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
                        return;
                    }

                    if (p.getPartyMembers().size() < 2) {
                        viewer.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH);
                        return;
                    }

                    val m = new TeamMatch(p, new MatchData(viewer.getDuelSettings()));
                    m.start();
                };
            } else if (action == SubmitAction.TOURNAMENT && simpleMode) {
                arenaRunnable = interaction -> {
                    viewer.getPlayer().closeInventory();

                    viewer.getDuelSettings().setArenaId(arenaId);

                    val tournament = new Tournament(viewer);
                    tournament.start();
                };
            }

            add(item, arenaRunnable);
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Select Arena";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
