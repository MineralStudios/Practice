package gg.mineral.practice.match;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.ai.goal.MeleeCombatGoal;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.QueueMatchData;
import gg.mineral.practice.util.PlayerUtil;

import java.util.List;

public class BotTeamMatch extends TeamMatch {

    Collection<Difficulty> team1Bots, team2Bots;
    List<FakePlayer> team1FakePlayers = new GlueList<>(), team2FakePlayers = new GlueList<>();

    public BotTeamMatch(Collection<Profile> team1, Collection<Profile> team2, Collection<Difficulty> team1Bots,
            Collection<Difficulty> team2Bots, QueueMatchData matchData) {
        super(team1, team2, matchData);
        this.team1Bots = team1Bots;
        this.team2Bots = team2Bots;
    }

    @Override
    public void onMatchStart() {
        super.onMatchStart();

        for (FakePlayer fakePlayer : team1FakePlayers) {
            fakePlayer.getConfiguration().setPearlCooldown(getData().getPearlCooldown());
            fakePlayer.startGoals(new MeleeCombatGoal(fakePlayer));
        }

        for (FakePlayer fakePlayer : team2FakePlayers) {
            fakePlayer.getConfiguration().setPearlCooldown(getData().getPearlCooldown());
            fakePlayer.startGoals(new MeleeCombatGoal(fakePlayer));
        }
    }

    public void spawnBots() {
        Location location1 = getData().getArena().getLocation1().clone();
        Location location2 = getData().getArena().getLocation2().clone();
        int suffix = 0;

        for (Difficulty difficulty : team1Bots) {
            FakePlayer fakePlayer = difficulty.spawn(getData(), location1, "" + (suffix++));
            team1FakePlayers.add(fakePlayer);
        }

        for (Difficulty difficulty : team2Bots) {
            FakePlayer fakePlayer = difficulty.spawn(getData(), location2, "" + (suffix++));
            team2FakePlayers.add(fakePlayer);
        }
    }

    @Override
    public void start() {
        spawnBots();
        if (noArenas())
            return;

        for (FakePlayer fakePlayer : team1FakePlayers)
            this.team1RemainingPlayers
                    .add(ProfileManager.getOrCreateProfile(Bukkit.getPlayer(fakePlayer.getUuid())));

        for (FakePlayer fakePlayer : team2FakePlayers)
            this.team2RemainingPlayers
                    .add(ProfileManager.getOrCreateProfile(Bukkit.getPlayer(fakePlayer.getUuid())));

        MatchManager.registerMatch(this);
        Location location1 = getData().getArena().getLocation1().clone();
        Location location2 = getData().getArena().getLocation2().clone();
        setupLocations(location1, location2);

        this.participants.addAll(team1RemainingPlayers);
        this.participants.addAll(team2RemainingPlayers);

        this.profile1 = team1RemainingPlayers.getFirst();
        this.profile2 = team2RemainingPlayers.getFirst();
        this.team1RequiredHitCount = team1RemainingPlayers.size() * 100;
        this.team2RequiredHitCount = team2RemainingPlayers.size() * 100;

        org.bukkit.scoreboard.Scoreboard team1sb = getDisplayNameBoard(team1RemainingPlayers,
                team2RemainingPlayers);
        org.bukkit.scoreboard.Scoreboard team2sb = getDisplayNameBoard(team2RemainingPlayers,
                team1RemainingPlayers);

        for (Profile teamMember : team1RemainingPlayers) {
            prepareForMatch(teamMember, team1sb);
            PlayerUtil.teleport(teamMember.getPlayer(), location1);

            for (FakePlayer fakePlayer : team1FakePlayers)
                fakePlayer.getFriendlyEntityUUIDs().add(teamMember.getUuid());
        }

        for (Profile teamMember : team2RemainingPlayers) {
            prepareForMatch(teamMember, team2sb);
            PlayerUtil.teleport(teamMember.getPlayer(), location2);

            for (FakePlayer fakePlayer : team2FakePlayers)
                fakePlayer.getFriendlyEntityUUIDs().add(teamMember.getUuid());
        }

        startCountdown();
    }

}
