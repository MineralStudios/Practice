package gg.mineral.practice.queue;

import java.util.List;
import java.util.Map.Entry;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.match.BotTeamMatch;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.QueueMatchData;
import gg.mineral.practice.party.Party;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class QueueSearchTask2v2 {
    static Object2ObjectOpenHashMap<QueueEntry, List<Team>> formedTeams = new Object2ObjectOpenHashMap<>();

    public static void addPlayer(Profile profile, QueueEntry queueEntry) {
        QueueSearchTask.removePlayer(profile);

        Team found = null;

        List<Team> teams = formedTeams.getOrDefault(queueEntry, new GlueList<>());

        if (profile.getMatchData().isBotTeammate()) {
            for (Team team : teams) {
                if (team.getProfiles().contains(profile) && team.isBots()) {
                    found = team;
                    break;
                }
            }
            if (found == null) {
                found = new Team(2, true);
                found.getProfiles().add(profile);
                teams.add(found);
                formedTeams.put(queueEntry, teams);
            }
        } else {
            for (Team team : teams) {
                if (!team.isFull()) {
                    for (Profile teamProfile : team.getProfiles()) {
                        if (teamProfile.getMatchData().isBotQueue() == profile.getMatchData().isBotQueue()) {
                            found = team;
                            found.getProfiles().add(profile);

                            if (profile.getMatchData().isBotQueue()) {
                                List<Difficulty> team2Difficulties = new GlueList<>();

                                for (int i = 0; i < 2; i++)
                                    team2Difficulties.add(Difficulty.RANDOM);

                                TeamMatch m = new BotTeamMatch(found.getProfiles(), new GlueList<>(),
                                        new GlueList<>(),
                                        team2Difficulties,
                                        profile.getMatchData()
                                                .cloneBotAndArenaData(() -> new QueueMatchData(queueEntry)));
                                teams.remove(found);
                                formedTeams.put(queueEntry, teams);
                                m.start();
                                return;
                            }

                            break;
                        }
                    }
                }
            }
        }

        if (found == null) {
            found = new Team(2, false);
            found.getProfiles().add(profile);
            teams.add(found);
            formedTeams.put(queueEntry, teams);
        }

        if (found.isFull()) {
            Team opponent = null;
            for (Team team : teams) {
                if (!team.getProfiles().contains(profile)) {
                    opponent = team;
                    break;
                }
            }

            if (opponent != null && opponent.isFull()) {
                List<Difficulty> team1Difficulties = new GlueList<>(), team2Difficulties = new GlueList<>();

                if (found.isBots())
                    for (int i = 0; i < found.getBotCount(); i++)
                        team1Difficulties.add(Difficulty.RANDOM);

                if (opponent.isBots())
                    for (int i = 0; i < opponent.getBotCount(); i++)
                        team2Difficulties.add(Difficulty.RANDOM);

                TeamMatch m = new BotTeamMatch(found.getProfiles(), opponent.getProfiles(), team1Difficulties,
                        team2Difficulties,
                        profile.getMatchData().cloneBotAndArenaData(() -> new QueueMatchData(queueEntry)));
                formedTeams.remove(queueEntry);
                m.start();
                return;
            }
        }
    }

    public static void addParty(Party party, QueueEntry queueEntry) {
        List<Team> teams = formedTeams.getOrDefault(queueEntry, new GlueList<>());

        Team found = new Team(2, false);

        for (Profile profile : party.getPartyMembers())
            found.getProfiles().add(profile);

        teams.add(found);
        formedTeams.put(queueEntry, teams);

        Team opponent = null;
        for (Team team : teams) {
            for (Profile profile : party.getPartyMembers()) {
                if (!team.getProfiles().contains(profile)) {
                    opponent = team;
                    break;
                }
            }
        }

        if (opponent != null && opponent.isFull()) {
            List<Difficulty> team1Difficulties = new GlueList<>(), team2Difficulties = new GlueList<>();

            if (opponent.isBots())
                for (int i = 0; i < opponent.getBotCount(); i++)
                    team2Difficulties.add(Difficulty.RANDOM);

            TeamMatch m = new BotTeamMatch(found.getProfiles(), opponent.getProfiles(), team1Difficulties,
                    team2Difficulties,
                    new QueueMatchData(queueEntry));
            formedTeams.remove(queueEntry);
            m.start();
            return;
        }

    }

    public static void removePlayer(Profile profile) {
        for (List<Team> teams : formedTeams.values()) {
            teams.forEach(team -> team.getProfiles().remove(profile));
        }
    }

    public static boolean removePlayer(Profile profile, QueueEntry queueEntry) {
        List<Team> teams = formedTeams.get(queueEntry);
        if (teams == null) {
            return true;
        }

        teams.forEach(team -> team.getProfiles().remove(profile));

        formedTeams.put(queueEntry, teams);

        return teams.isEmpty();
    }

    public static List<QueueEntry> getQueueEntries(Profile profile) {
        List<QueueEntry> profileQueueEntries = new GlueList<>();
        for (Entry<QueueEntry, List<Team>> entry : formedTeams.entrySet()) {
            List<Team> teams = entry.getValue();
            for (Team team : teams) {
                if (team.getProfiles().contains(profile)) {
                    profileQueueEntries.add(entry.getKey());
                }
            }
        }
        return profileQueueEntries;
    }

    public static int getNumberInQueue(Queuetype queuetype, Gametype gametype) {
        int i = 0;

        for (List<Team> teams : formedTeams.values()) {
            for (Team team : teams) {
                for (Profile profile : team.getProfiles()) {
                    for (QueueEntry queueEntry : getQueueEntries(profile)) {
                        if (queueEntry.getGametype().equals(gametype)
                                && queueEntry.getQueuetype().equals(queuetype)) {
                            i++;
                        }
                    }
                }
            }
        }

        return i;
    }

    @RequiredArgsConstructor
    static class Team {
        final int size;
        @Getter
        List<Profile> profiles = new GlueList<>();
        @Getter
        final boolean bots;

        public int getBotCount() {
            return size - profiles.size();
        }

        public boolean isFull() {
            return bots || profiles.size() >= size;
        }
    }
}
