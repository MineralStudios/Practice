package gg.mineral.practice.inventory.menus;

import java.util.Iterator;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.RequiredArgsConstructor;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.tournaments.Tournament;

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
        Iterator<Arena> arenas = simpleMode ? viewer.getMatchData().getGametype().getArenas().iterator()
                : ArenaManager.getArenas().iterator();

        while (arenas.hasNext()) {
            Arena a = arenas.next();

            ItemStack item;
            try {
                item = new ItemBuilder(a.getDisplayItem().clone())
                        .name(CC.SECONDARY + CC.B + a.getDisplayName()).lore(CC.ACCENT + "Click to select.").build();
            } catch (Exception e) {
                continue;
            }

            Runnable arenaRunnable = () -> {
                viewer.getMatchData().setArena(a);

                if (simpleMode) {
                    viewer.getPlayer().closeInventory();
                    viewer.getRequestHandler().sendDuelRequest(viewer.getRequestHandler().getDuelRequestReciever());
                    return;
                }

                viewer.openMenu(menu);
            };

            if (action == SubmitAction.P_SPLIT && simpleMode) {
                arenaRunnable = () -> {
                    viewer.getPlayer().closeInventory();

                    viewer.getMatchData().setArena(a);

                    Party p = viewer.getParty();

                    if (!viewer.getParty().getPartyLeader().equals(viewer)) {
                        viewer.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
                        return;
                    }

                    if (p.getPartyMembers().size() < 2) {
                        viewer.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH);
                        return;
                    }

                    PartyMatch m = new PartyMatch(p, viewer.getMatchData());
                    m.start();
                };
            } else if (action == SubmitAction.TOURNAMENT && simpleMode) {
                arenaRunnable = () -> {
                    viewer.getPlayer().closeInventory();

                    viewer.getMatchData().setArena(a);

                    Tournament tournament = new Tournament(viewer);
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
