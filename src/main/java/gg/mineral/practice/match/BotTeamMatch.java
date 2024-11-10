package gg.mineral.practice.match;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.ai.goal.DrinkPotionGoal;
import gg.mineral.bot.ai.goal.EatGappleGoal;
import gg.mineral.bot.ai.goal.MeleeCombatGoal;
import gg.mineral.bot.ai.goal.ReplaceArmorGoal;
import gg.mineral.bot.api.configuration.BotConfiguration;

import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.PlayerUtil;
import lombok.val;

public class BotTeamMatch extends TeamMatch {

    Collection<BotConfiguration> team1Bots, team2Bots;
    List<ClientInstance> team1BotInstances = new GlueList<>(), team2BotInstances = new GlueList<>();

    public BotTeamMatch(Collection<Profile> team1, Collection<Profile> team2, Collection<BotConfiguration> team1Bots,
            Collection<BotConfiguration> team2Bots, MatchData matchData) {
        super(team1, team2, matchData);
        this.team1Bots = team1Bots;
        this.team2Bots = team2Bots;
    }

    @Override
    public void onMatchStart() {
        super.onMatchStart();

        for (val instance : team1BotInstances) {
            instance.getConfiguration().setPearlCooldown(getData().getPearlCooldown());
            instance.startGoals(new ReplaceArmorGoal(instance), new DrinkPotionGoal(instance),
                    new EatGappleGoal(instance),
                    new MeleeCombatGoal(instance));
        }

        for (val instance : team2BotInstances) {
            instance.getConfiguration().setPearlCooldown(getData().getPearlCooldown());
            instance.startGoals(new ReplaceArmorGoal(instance), new DrinkPotionGoal(instance),
                    new EatGappleGoal(instance),
                    new MeleeCombatGoal(instance));
        }
    }

    @Override
    public void start() {
        if (noArenas())
            return;

        MatchManager.registerMatch(this);
        val arena = ArenaManager.getArenas().get(getData().getArenaId());
        val location1 = arena.getLocation1().clone();
        val location2 = arena.getLocation2().clone();
        setupLocations(location1, location2);

        team1RemainingPlayers.alive(teamMember -> PlayerUtil.teleport(teamMember.getPlayer(), location1));
        team2RemainingPlayers.alive(teamMember -> PlayerUtil.teleport(teamMember.getPlayer(), location2));

        startCountdown();

        int suffix = 0;

        for (val config : team1Bots) {
            config.setUsernameSuffix("" + (suffix++));
            val instance = Difficulty.spawn(config, location1);
            this.team1RemainingPlayers
                    .put(ProfileManager.getOrCreateProfile(Bukkit.getPlayer(config.getUuid())), true);
            team1BotInstances.add(instance);
        }

        for (val config : team2Bots) {
            config.setUsernameSuffix("" + (suffix++));
            val clientInstance = Difficulty.spawn(config, location2);
            this.team2RemainingPlayers
                    .put(ProfileManager.getOrCreateProfile(Bukkit.getPlayer(config.getUuid())), true);
            team2BotInstances.add(clientInstance);
        }

        team1RemainingPlayers.alive(teamMember -> this.participants.add(teamMember));
        team2RemainingPlayers.alive(teamMember -> this.participants.add(teamMember));

        this.profile1 = team1RemainingPlayers.firstKey();
        this.profile2 = team2RemainingPlayers.firstKey();
        this.team1RequiredHitCount = team1RemainingPlayers.size() * 100;
        this.team2RequiredHitCount = team2RemainingPlayers.size() * 100;

        this.nametagGroups = setDisplayNameBoard();

        team1RemainingPlayers.alive(teamMember -> {
            prepareForMatch(teamMember);
            for (val instance : team1BotInstances)
                instance.getConfiguration().getFriendlyUUIDs().add(teamMember.getUuid());
        });

        team2RemainingPlayers.alive(teamMember -> {
            prepareForMatch(teamMember);
            for (val instance : team2BotInstances)
                instance.getConfiguration().getFriendlyUUIDs().add(teamMember.getUuid());
        });
    }

}
