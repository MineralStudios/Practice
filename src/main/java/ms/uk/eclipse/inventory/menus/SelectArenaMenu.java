package ms.uk.eclipse.inventory.menus;

import java.util.Iterator;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.inventory.SubmitAction;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.match.PartyMatch;
import ms.uk.eclipse.party.Party;
import ms.uk.eclipse.tournaments.Tournament;
import ms.uk.eclipse.util.messages.ErrorMessages;

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
