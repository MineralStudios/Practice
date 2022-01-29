package ms.uk.eclipse.inventory.menus;

import java.util.Iterator;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.inventory.SubmitAction;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.match.PartyMatch;
import ms.uk.eclipse.party.Party;
import ms.uk.eclipse.tournaments.Tournament;

public class SelectArenaMenu extends Menu {
    MechanicsMenu menu;
    boolean simpleMode = false;
    SubmitAction action;
    final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();

    public SelectArenaMenu(MechanicsMenu menu, SubmitAction action) {
        super(new StrikingMessage("Select Arena", CC.PRIMARY, true));
        setClickCancelled(true);
        this.menu = menu;
        this.action = action;
    }

    public SelectArenaMenu(SubmitAction action) {
        super(new StrikingMessage("Select Arena", CC.PRIMARY, true));
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
                        .name(new ChatMessage(a.getDisplayName(), CC.WHITE, true).toString()).build();
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
                        viewer.message(new ErrorMessage("You must be the party leader"));
                        return;
                    }

                    if (p.getPartyMembers().size() < 2) {
                        viewer.message(new ErrorMessage("You need at least 2 people in a party"));
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
