package gg.mineral.practice.inventory.menus;

import java.util.Iterator;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.tournaments.Tournament;

public class SelectArenaMenu extends PracticeMenu {
    MechanicsMenu menu;
    boolean simpleMode = false;
    SubmitAction action;
    final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();
    final static String TITLE = CC.BLUE + "Select Arena";

    public SelectArenaMenu(MechanicsMenu menu, SubmitAction action) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
        this.action = action;
    }

    public SelectArenaMenu(SubmitAction action) {
        super(TITLE);
        setClickCancelled(true);
        simpleMode = true;
        this.action = action;
    }

    @Override
    public boolean update() {
        Iterator<Arena> arenas = simpleMode ? viewer.getMatchData().getGametype().getEnabledArenas().iterator()
                : arenaManager.getArenas().iterator();

        while (arenas.hasNext()) {
            Arena a = arenas.next();

            ItemStack item;
            try {
                item = new ItemBuilder(a.getDisplayItem())
                        .name(a.getDisplayName()).build();
            } catch (Exception e) {
                continue;
            }

            Runnable arenaRunnable = () -> {
                viewer.getMatchData().setArena(a);

                if (simpleMode) {
                    viewer.sendDuelRequest(viewer.getDuelReciever());
                    return;
                }

                viewer.openMenu(menu);
            };

            if (action == SubmitAction.P_SPLIT && simpleMode) {
                arenaRunnable = () -> {
                    viewer.bukkit().closeInventory();

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
                    viewer.bukkit().closeInventory();

                    viewer.getMatchData().setArena(a);

                    Tournament t = new Tournament(viewer);
                    t.start();
                };
            }

            add(item, arenaRunnable);
        }
        return true;
    }
}
