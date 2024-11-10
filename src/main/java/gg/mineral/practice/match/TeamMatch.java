package gg.mineral.practice.match;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.nametag.NametagGroup;
import gg.mineral.bot.api.BotAPI;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard;
import gg.mineral.practice.scoreboard.impl.PartyMatchScoreboard;
import gg.mineral.practice.scoreboard.impl.TeamBoxingScoreboard;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.Strings;
import io.isles.nametagapi.NametagAPI;
import lombok.Getter;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TeamMatch extends Match {
    @Getter
    protected ProfileList team1RemainingPlayers, team2RemainingPlayers;
    protected List<InventoryStatsMenu> team1InventoryStatsMenus = new GlueList<>(),
            team2InventoryStatsMenus = new GlueList<>();
    @Getter
    protected NametagGroup[] nametagGroups;

    @Getter
    int team1HitCount = 0, team2HitCount = 0, team1RequiredHitCount, team2RequiredHitCount;

    public TeamMatch(Collection<Profile> team1, Collection<Profile> team2, MatchData matchData) {
        super(matchData);
        this.team1RemainingPlayers = new ProfileList(team1);
        this.team2RemainingPlayers = new ProfileList(team2);
    }

    public TeamMatch(Party party1, Party party2, MatchData matchData) {
        super(matchData);
        this.team1RemainingPlayers = new ProfileList(party1.getPartyMembers());
        this.team2RemainingPlayers = new ProfileList(party2.getPartyMembers());
    }

    public TeamMatch(Party party, MatchData matchData) {
        this(party.getPartyMembers(), matchData);
    }

    public TeamMatch(Collection<Profile> profiles, MatchData matchData) {
        super(matchData);
        this.participants.addAll(profiles);
        int size = participants.size();
        this.team1RemainingPlayers = new ProfileList(participants.subList(0, (size + 1) / 2));
        this.team2RemainingPlayers = new ProfileList(participants.subList((size + 1) / 2, size));
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

        this.participants.addAll(team1RemainingPlayers);
        this.participants.addAll(team2RemainingPlayers);

        this.profile1 = team1RemainingPlayers.getFirst();
        this.profile2 = team2RemainingPlayers.getFirst();
        this.team1RequiredHitCount = team1RemainingPlayers.size() * 100;
        this.team2RequiredHitCount = team2RemainingPlayers.size() * 100;

        this.nametagGroups = setDisplayNameBoard(team1RemainingPlayers, team2RemainingPlayers);

        for (val teamMember : team1RemainingPlayers) {
            prepareForMatch(teamMember);
            PlayerUtil.teleport(teamMember.getPlayer(), location1);
        }

        for (val teamMember : team2RemainingPlayers) {
            prepareForMatch(teamMember);
            PlayerUtil.teleport(teamMember.getPlayer(), location2);
        }

        startCountdown();
    }

    @Override
    public void setScoreboard(Profile p) {
        p.setScoreboard(getData().isBoxing() ? TeamBoxingScoreboard.INSTANCE : PartyMatchScoreboard.INSTANCE);
    }

    @Override
    public void end(Profile victim) {
        if (isEnded() || victim.isDead())
            return;

        victim.setDead(true);

        stat(victim, collector -> collector.end(false));

        boolean isTeam1 = team1RemainingPlayers.contains(victim);
        val attackerTeam = isTeam1 ? team2RemainingPlayers : team1RemainingPlayers;
        val victimTeam = isTeam1 ? team1RemainingPlayers : team2RemainingPlayers;
        val attackerInventoryStatsMenus = isTeam1 ? team2InventoryStatsMenus
                : team1InventoryStatsMenus;
        val victimInventoryStatsMenus = isTeam1 ? team1InventoryStatsMenus
                : team2InventoryStatsMenus;
        int attackerTeamHits = isTeam1 ? team2HitCount : team1HitCount;
        int victimTeamHits = isTeam1 ? team1HitCount : team2HitCount;

        stat(victim, collector -> victimInventoryStatsMenus.add(setInventoryStats(collector)));

        pearlCooldown.getCooldowns().removeInt(victim.getUuid());
        victim.heal();
        victim.removePotionEffects();
        victim.getInventory().clear();

        victim.setScoreboard(DefaultScoreboard.INSTANCE);

        victimTeam.remove(victim);

        for (val profile : participants) {
            boolean hasKiller = victim.getKiller() != null;
            var message = hasKiller ? ChatMessages.KILLED_BY_PLAYER : ChatMessages.DIED;
            message = message.clone().replace("%victim%", victim.getName());
            profile.message(hasKiller ? message.replace("%attacker%", victim.getKiller().getName()) : message);
        }

        if (victimTeam.size() > 0) {

            participants.remove(victim);
            victim.removeFromMatch();

            if (BotAPI.INSTANCE.despawn(victim.getPlayer().getUniqueId()))
                return;

            victim.getSpectateHandler().spectate(victimTeam.getFirst());

            return;
        }

        ended = true;

        for (val nametagGroup : nametagGroups) {
            nametagGroup.delete();
            for (val player : nametagGroup.getPlayers())
                refreshBukkitScoreboard(player);
        }

        val attackerTeamIterator = attackerTeam.iterator();

        val attackerTeamLeader = attackerTeamIterator.next();

        attackerEndMatch(attackerTeamLeader, attackerInventoryStatsMenus);

        while (attackerTeamIterator.hasNext())
            attackerEndMatch(attackerTeamIterator.next(), attackerInventoryStatsMenus);

        for (val invStats : attackerInventoryStatsMenus) {
            val profile = invStats.getMatchStatisticCollector().getProfile();
            ProfileManager.setInventoryStats(profile, attackerInventoryStatsMenus);
        }

        for (val invStats : victimInventoryStatsMenus) {
            val profile = invStats.getMatchStatisticCollector().getProfile();
            ProfileManager.setInventoryStats(profile, victimInventoryStatsMenus);
        }

        MatchManager.remove(this);

        val winMessage = new TextComponent(
                CC.GREEN + "Winner: " + CC.GRAY + attackerTeamLeader.getName() + "\'s team");
        val loseMessage = new TextComponent(
                CC.RED + "Loser: " + CC.GRAY + victim.getName() + "\'s team");
        loseMessage
                .setHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(CC.RED + "Hits: " + victimTeamHits)
                                        .create()));
        winMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(CC.GREEN + "Hits: " + attackerTeamHits).create()));
        loseMessage
                .setClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + victim.getName()));
        winMessage.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/viewinventory " + attackerTeamLeader.getName()));

        for (val profile : participants) {
            profile.getPlayer().sendMessage(CC.SEPARATOR);
            profile.getPlayer().sendMessage(Strings.MATCH_RESULTS);
            profile.getPlayer().spigot().sendMessage(winMessage);
            profile.getPlayer().spigot().sendMessage(loseMessage);
            profile.getPlayer().sendMessage(CC.SEPARATOR);
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

        for (val spectator : getSpectators()) {
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

    @Override
    public Profile getOpponent(Profile p) {
        return team1RemainingPlayers.contains(p) ? team2RemainingPlayers.getFirst()
                : team1RemainingPlayers.getFirst();
    }

    private void attackerEndMatch(Profile attacker, List<InventoryStatsMenu> attackerInventoryStatsMenus) {

        stat(attacker, collector -> collector.end(true));

        stat(attacker, collector -> attackerInventoryStatsMenus
                .add(setInventoryStats(collector)));

        pearlCooldown.getCooldowns().removeInt(attacker.getUuid());
        attacker.heal();
        attacker.removePotionEffects();
        attacker.getInventory().clear();

        attacker.setScoreboard(MatchEndScoreboard.INSTANCE);

        giveQueueAgainItem(attacker);

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
            if (attacker.getPlayerStatus() == PlayerStatus.FIGHTING && !attacker.getMatch().isEnded())
                return;
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
        stat(attacker, collector -> collector.increaseHitCount());
        stat(victim, collector -> collector.resetCombo());

        boolean isTeam1 = team1RemainingPlayers.contains(attacker);
        int hitCount = isTeam1 ? ++team1HitCount : ++team2HitCount;
        int requiredHitCount = isTeam1 ? team1RequiredHitCount : team2RequiredHitCount;
        val opponentTeam = isTeam1 ? team2RemainingPlayers : team1RemainingPlayers;

        if (hitCount >= requiredHitCount
                && getData().isBoxing()) {
            for (val opponent : opponentTeam)
                end(opponent);

            return true;
        }

        return false;
    }

    public void refreshBukkitScoreboard(Player player) {
        val scoreboard = player.getScoreboard();
        val manager = Bukkit.getScoreboardManager();
        val blankScoreboard = manager.getNewScoreboard();
        player.setScoreboard(blankScoreboard);
        Bukkit.getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> player.setScoreboard(scoreboard), 1L);
    }

    public NametagGroup[] setDisplayNameBoard(ProfileList playerTeam, ProfileList opponentTeam) {

        val playerGroup = new NametagGroup();

        for (val profile : playerTeam)
            playerGroup.add(profile.getPlayer());

        val opponentGroup = new NametagGroup();

        for (val profile : opponentTeam)
            opponentGroup.add(profile.getPlayer());

        for (val profile : playerTeam)
            NametagAPI.setPrefix(playerGroup, profile.getName(), CC.GREEN);

        for (val profile : opponentTeam)
            NametagAPI.setPrefix(playerGroup, profile.getName(), CC.RED);

        for (val profile : opponentTeam)
            NametagAPI.setPrefix(opponentGroup, profile.getName(), CC.GREEN);

        for (val profile : playerTeam)
            NametagAPI.setPrefix(opponentGroup, profile.getName(), CC.RED);

        return new NametagGroup[] { playerGroup, opponentGroup };
    }
}
