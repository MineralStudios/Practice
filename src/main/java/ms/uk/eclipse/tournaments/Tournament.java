package ms.uk.eclipse.tournaments;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import land.strafe.api.collection.GlueList;
import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.InfoMessage;
import ms.uk.eclipse.core.utils.message.JoinMessage;
import ms.uk.eclipse.core.utils.message.JoinedMessage;
import ms.uk.eclipse.core.utils.message.LeftMessage;
import ms.uk.eclipse.core.utils.message.Message;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.TournamentManager;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.match.MatchData;
import ms.uk.eclipse.match.TournamentMatch;
import ms.uk.eclipse.util.ProfileList;
import net.md_5.bungee.api.chat.ClickEvent;

public class Tournament {
    GlueList<Match> matches = new GlueList<>();
    ProfileList players = new ProfileList();
    ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<>();
    final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
    final TournamentManager tournamentManager = PracticePlugin.INSTANCE.getTournamentManager();
    boolean started = false;
    boolean ended = false;
    MatchData m;
    int round = 1;
    boolean event = false;
    Arena eventArena;
    String host;
    Location loc;

    public Tournament(Profile p) {
        this.m = p.getMatchData();
        this.host = p.getName();
        addPlayer(p);
        tournamentManager.registerTournament(this);
    }

    public Tournament(Profile p, Arena eventArena) {
        this.m = p.getMatchData();
        this.host = p.getName();
        event = true;
        this.eventArena = eventArena;
        m.setArena(eventArena);
        loc = eventArena.getWaitingLocation();
        addPlayer(p);
        tournamentManager.registerTournament(this);
    }

    public void addPlayer(Profile p) {

        if (started) {
            p.message(new ErrorMessage("The tournament has started"));
            return;
        }

        if (event) {
            p.teleport(loc);
        }

        p.setTournament(this);
        players.add(p);

        playerManager.broadcast(players, new JoinedMessage(p.getName(), "tournament"));
    }

    public void removePlayer(Profile p) {
        players.remove(p);

        Message m = new LeftMessage(p.getName(), "tournament");
        playerManager.broadcast(players, m);

        if (players.size() == 0) {
            tournamentManager.remove(this);
            ended = true;
            return;
        }

        if (started && players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromTournament();

            if (getSpectators().size() > 0) {
                for (Profile pl : getSpectators()) {
                    pl.stopSpectating();
                }
            }

            tournamentManager.remove(this);
            ended = true;

            playerManager.broadcast(playerManager.getProfiles(),
                    new InfoMessage(winner.getName() + " has won the tournament"));

            return;
        }
    }

    public void start() {

        if (started) {
            return;
        }

        JoinMessage message = new JoinMessage(host, "started a tournament",
                "with the " + m.getKitName() + " kit, it will start in 30 seconds",
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + host));

        playerManager.broadcast(playerManager.getProfiles(), message);

        Tournament t = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                started = true;

                if (players.size() == 1) {
                    Profile winner = players.get(0);
                    winner.removeFromTournament();
                    if (getSpectators().size() > 0) {
                        for (Profile p : getSpectators()) {
                            p.stopSpectating();
                        }
                    }
                    winner.message(new InfoMessage("There was not enough players to start the tournament"));
                    tournamentManager.remove(t);
                    ended = true;
                    return;
                }

                startRound();
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 600);
    }

    public void startRound() {

        if (players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromTournament();

            if (getSpectators().size() > 0) {
                for (Profile p : getSpectators()) {
                    p.stopSpectating();
                }
            }

            ended = true;
            tournamentManager.remove(this);
            return;
        }

        ProfileList tempList = new ProfileList(players);

        while (!tempList.isEmpty()) {
            Profile p1 = tempList.removeFirst();

            if (tempList.isEmpty()) {
                p1.message(new ChatMessage("There is no avalable opponent for this round, you have skipped this round",
                        CC.PRIMARY, false));
                return;
            }

            Profile p2 = tempList.removeFirst();

            TournamentMatch match = new TournamentMatch(p1, p2, m, this);
            match.start();
            matches.add(match);

            if (event) {
                break;
            }
        }
    }

    public void removeMatch(Match m) {
        matches.remove(m);

        if (ended) {
            return;
        }

        if (matches.isEmpty()) {
            Message message = new InfoMessage("Round " + round + " is over. The next round will start in 5 seconds");

            playerManager.broadcast(players, message);

            new BukkitRunnable() {
                @Override
                public void run() {
                    startRound();
                    round++;
                }
            }.runTaskLater(PracticePlugin.INSTANCE, 100);
        }
    }

    public String getHost() {
        return host;
    }

    public boolean isEnded() {
        return ended;
    }

    public boolean isEvent() {
        return event;
    }

    public Arena getEventArena() {
        return eventArena;
    }

    public Location getWaitingLocation() {
        return loc;
    }

    public void addSpectator(Profile player) {
        spectators.add(player);
    }

    public ConcurrentLinkedDeque<Profile> getSpectators() {
        return spectators;
    }
}
