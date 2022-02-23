package gg.mineral.practice.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.event.PlayerStatusChangeEvent;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.Match;

public class PlayerStatusListener implements Listener {

    final MatchManager matchManager = PracticePlugin.INSTANCE.getMatchManager();

    @EventHandler
    public void onPlayerStatusChange(PlayerStatusChangeEvent e) {
        org.bukkit.entity.Player b = e.getPlayer().bukkit();
        Profile p = e.getPlayer();

        List<org.bukkit.entity.Player> list = b.getWorld().getPlayers();
        int i;

        if (e.previousStatus() != PlayerStatus.IN_QUEUE) {

            if (e.newStatus() == PlayerStatus.IN_LOBBY) {

                if (!p.getPlayersVisible()) {
                    for (i = 0; i < list.size(); i++) {
                        b.hidePlayer(list.get(i), false);
                    }

                    return;
                }

                for (i = 0; i < list.size(); i++) {
                    b.showPlayer(list.get(i));
                }
                return;
            }

            if (e.newStatus() == PlayerStatus.KIT_EDITOR || e.newStatus() == PlayerStatus.KIT_CREATOR) {
                for (i = 0; i < list.size(); i++) {
                    b.hidePlayer(list.get(i));
                }

                return;
            }

            if (e.newStatus() == PlayerStatus.SPECTATING) {
                List<Profile> list2 = p.getSpectatingMatch().getParticipants();

                for (i = 0; i < list2.size(); i++) {
                    p.bukkit().showPlayer(list2.get(i).bukkit());
                }

                for (Match match : matchManager.getMatchs()) {
                    match.updateVisiblity(p.getSpectatingMatch(), p);
                }

                return;
            }

        }
    }
}
