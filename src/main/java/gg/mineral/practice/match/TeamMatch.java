package gg.mineral.practice.match;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scoreboard.Team;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.api.BotAPI;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.QueueMatchData;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard;
import gg.mineral.practice.scoreboard.impl.PartyMatchScoreboard;
import gg.mineral.practice.scoreboard.impl.TeamBoxingScoreboard;
import gg.mineral.practice.util.CoreConnector;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.Strings;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TeamMatch extends Match<QueueMatchData> {
    @Getter
    ProfileList team1RemainingPlayers, team2RemainingPlayers;
    List<InventoryStatsMenu> team1InventoryStatsMenus = new GlueList<>(), team2InventoryStatsMenus = new GlueList<>();

    @Getter
    int team1HitCount = 0, team2HitCount = 0, team1RequiredHitCount, team2RequiredHitCount;

    public TeamMatch(Collection<Profile> team1, Collection<Profile> team2, QueueMatchData matchData) {
        super(matchData);
        this.team1RemainingPlayers = new ProfileList(team1);
        this.team2RemainingPlayers = new ProfileList(team2);
    }

    @Override
    public void start() {

        if (noArenas())
            return;

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

        org.bukkit.scoreboard.Scoreboard team1sb = getDisplayNameBoard(team1RemainingPlayers, team2RemainingPlayers);
        org.bukkit.scoreboard.Scoreboard team2sb = getDisplayNameBoard(team2RemainingPlayers, team1RemainingPlayers);

        for (Profile teamMember : team1RemainingPlayers) {
            prepareForMatch(teamMember, team1sb);
            PlayerUtil.teleport(teamMember.getPlayer(), location1);
        }

        for (Profile teamMember : team2RemainingPlayers) {
            prepareForMatch(teamMember, team2sb);
            PlayerUtil.teleport(teamMember.getPlayer(), location2);
        }

        startCountdown();
    }

    public void prepareForMatch(Profile profile, org.bukkit.scoreboard.Scoreboard teamSb) {
        prepareForMatch(profile);
        profile.getPlayer().setScoreboard(teamSb);
    }

    @Override
    public void setScoreboard(Profile p) {
        if (getData().isBoxing()) {
            p.setScoreboard(TeamBoxingScoreboard.INSTANCE);
            return;
        }

        p.setScoreboard(PartyMatchScoreboard.INSTANCE);
    }

    @Override
    public void end(Profile victim) {
        if (isEnded() || victim.isDead())
            return;

        victim.setDead(true);

        victim.getMatchStatisticCollector().end(false);

        ProfileList attackerTeam, victimTeam;
        List<InventoryStatsMenu> attackerInventoryStatsMenus = new GlueList<>(),
                victimInventoryStatsMenus = new GlueList<>();
        int attackerTeamHits, victimTeamHits;

        if (team1RemainingPlayers.contains(victim)) {
            victimTeam = team1RemainingPlayers;
            attackerTeam = team2RemainingPlayers;
            victimInventoryStatsMenus = team1InventoryStatsMenus;
            attackerInventoryStatsMenus = team2InventoryStatsMenus;
            attackerTeamHits = team2HitCount;
            victimTeamHits = team1HitCount;
        } else {
            victimTeam = team2RemainingPlayers;
            attackerTeam = team1RemainingPlayers;
            victimInventoryStatsMenus = team2InventoryStatsMenus;
            attackerInventoryStatsMenus = team1InventoryStatsMenus;
            attackerTeamHits = team1HitCount;
            victimTeamHits = team2HitCount;
        }

        victimInventoryStatsMenus.add(setInventoryStats(victim, victim.getMatchStatisticCollector()));

        victim.setPearlCooldown(0);
        victim.heal();
        victim.removePotionEffects();
        victim.getInventory().clear();

        victim.setScoreboard(DefaultScoreboard.INSTANCE);

        victimTeam.remove(victim);

        for (Profile profile : participants) {
            boolean hasKiller = victim.getKiller() != null;
            ChatMessage message = hasKiller ? ChatMessages.KILLED_BY_PLAYER : ChatMessages.DIED;
            message = message.clone().replace("%victim%", victim.getName());
            profile.message(hasKiller ? message.replace("%attacker%", victim.getKiller().getName()) : message);
        }

        if (victimTeam.size() > 0) {

            participants.remove(victim);
            victim.removeFromMatch();

            if (BotAPI.INSTANCE.despawn(victim.getPlayer().getUniqueId()))
                return;

            victim.getSpectateHandler().spectate(victimTeam.getFirst());

            if (CoreConnector.connected()) {
                CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(victim.getPlayer(),
                        victim.getPlayer());
            }

            return;
        }

        ended = true;

        Iterator<Profile> attackerTeamIterator = attackerTeam.iterator();

        Profile attackerTeamLeader = attackerTeamIterator.next();

        attackerEndMatch(attackerTeamLeader, attackerInventoryStatsMenus);

        while (attackerTeamIterator.hasNext())
            attackerEndMatch(attackerTeamIterator.next(), attackerInventoryStatsMenus);

        ProfileManager.setTeamInventoryStats(attackerTeamLeader, attackerInventoryStatsMenus);
        ProfileManager.setTeamInventoryStats(victim, victimInventoryStatsMenus);
        MatchManager.remove(this);

        TextComponent winMessage = new TextComponent(
                CC.GREEN + "Winner: " + CC.GRAY + attackerTeamLeader.getName() + "\'s party");
        TextComponent loseMessage = new TextComponent(
                CC.RED + "Loser: " + CC.GRAY + victim.getName() + "\'s party");
        loseMessage
                .setHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(CC.RED + "Hits: " + victimTeamHits)
                                        .create()));
        winMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(CC.GREEN + "Hits: " + attackerTeamHits).create()));
        loseMessage
                .setClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewteaminventory " + victim.getName()));
        winMessage.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/viewteaminventory " + attackerTeamLeader.getName()));

        for (Profile profile : participants) {
            profile.getPlayer().sendMessage(CC.SEPARATOR);
            profile.getPlayer().sendMessage(Strings.MATCH_RESULTS);
            profile.getPlayer().spigot().sendMessage(winMessage);
            profile.getPlayer().spigot().sendMessage(loseMessage);
            profile.getPlayer().sendMessage(CC.SEPARATOR);

            if (CoreConnector.connected()) {
                CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(profile.getPlayer(),
                        profile.getPlayer());
            }

        }

        participants.remove(victim);

        victim.removePotionEffects();
        victim.teleportToLobby();

        if (victim.isInParty())
            victim.getInventory().setInventoryForParty();
        else
            victim.getInventory().setInventoryForLobby();

        victim.removeFromMatch();

        BotAPI.INSTANCE.despawn(victim.getPlayer().getUniqueId());

        for (Profile spectator : getSpectators()) {
            spectator.getPlayer().sendMessage(CC.SEPARATOR);
            spectator.getPlayer().sendMessage(Strings.MATCH_RESULTS);
            spectator.getPlayer().spigot().sendMessage(winMessage);
            spectator.getPlayer().spigot().sendMessage(loseMessage);
            spectator.getPlayer().sendMessage(CC.SEPARATOR);
            spectator.getSpectateHandler().stopSpectating();
        }

        clearWorld();

    }

    @Override
    public ProfileList getTeam(Profile p) {
        return team1RemainingPlayers.contains(p) ? team1RemainingPlayers : team2RemainingPlayers;
    }

    private void attackerEndMatch(Profile attacker, List<InventoryStatsMenu> attackerInventoryStatsMenus) {

        attacker.getMatchStatisticCollector().end(true);

        attackerInventoryStatsMenus
                .add(setInventoryStats(attacker, attacker.getMatchStatisticCollector()));

        attacker.setPearlCooldown(0);
        attacker.heal();
        attacker.removePotionEffects();
        attacker.getInventory().clear();

        attacker.setScoreboard(MatchEndScoreboard.INSTANCE);

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
            attacker.teleportToLobby();
            if (attacker.isInParty())
                attacker.getInventory().setInventoryForParty();
            else
                attacker.getInventory().setInventoryForLobby();

            attacker.removeFromMatch();
            attacker.setScoreboard(DefaultScoreboard.INSTANCE);
            BotAPI.INSTANCE.despawn(attacker.getPlayer().getUniqueId());
        }, getPostMatchTime());
    }

    @Override
    public boolean incrementTeamHitCount(Profile attacker, Profile victim) {
        attacker.getMatchStatisticCollector().increaseHitCount();
        victim.getMatchStatisticCollector().resetCombo();

        boolean isTeam1 = team1RemainingPlayers.contains(attacker);
        int hitCount = isTeam1 ? ++team1HitCount : ++team2HitCount;
        int requiredHitCount = isTeam1 ? team1RequiredHitCount : team2RequiredHitCount;
        ProfileList opponentTeam = isTeam1 ? team2RemainingPlayers : team1RemainingPlayers;

        if (hitCount >= requiredHitCount
                && getData().isBoxing()) {
            for (Profile opponent : opponentTeam)
                end(opponent);

            return true;
        }

        return false;
    }

    public org.bukkit.scoreboard.Scoreboard getDisplayNameBoard(ProfileList playerTeam, ProfileList opponentTeam) {

        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Team teammates = scoreboard.registerNewTeam("teammates");
        Team opponents = scoreboard.registerNewTeam("opponents");

        teammates.setPrefix(CC.GREEN);
        opponents.setPrefix(CC.RED);

        for (Profile profile : playerTeam)
            teammates.addEntry(profile.getName());

        for (Profile profile : opponentTeam)
            opponents.addEntry(profile.getName());

        return scoreboard;
    }
}
