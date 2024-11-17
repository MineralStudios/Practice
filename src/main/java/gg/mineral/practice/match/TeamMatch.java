package gg.mineral.practice.match;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

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
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import lombok.Getter;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TeamMatch extends Match {

    public class Team extends Object2BooleanLinkedOpenHashMap<Profile> {
        public ProfileList alive() {
            val list = new ProfileList();
            for (val e : object2BooleanEntrySet())
                if (e.getBooleanValue())
                    list.add(e.getKey());
            return list;
        }

        public void alive(Consumer<Profile> consumer) {
            for (val e : object2BooleanEntrySet())
                if (e.getBooleanValue())
                    consumer.accept(e.getKey());
        }

        public Set<Profile> all() {
            return keySet();
        }

        public void add(Profile profile) {
            put(profile, true);
        }

        public void reportDeath(Profile profile) {
            put(profile, false);
        }
    }

    @Getter
    protected final Team team1Players = new Team(), team2Players = new Team();
    protected List<InventoryStatsMenu> team1InventoryStatsMenus = new GlueList<>(),
            team2InventoryStatsMenus = new GlueList<>();
    @Getter
    protected NametagGroup[] nametagGroups;

    @Getter
    int team1HitCount = 0, team2HitCount = 0, team1RequiredHitCount, team2RequiredHitCount;

    public TeamMatch(Queue<Profile> team1, Queue<Profile> team2, MatchData matchData) {
        super(matchData);

        for (val profile : team1)
            this.team1Players.add(profile);

        for (val profile : team2)
            this.team2Players.add(profile);
    }

    public TeamMatch(Party party1, Party party2, MatchData matchData) {
        super(matchData);

        for (val profile : party1.getPartyMembers())
            this.team1Players.add(profile);

        for (val profile : party2.getPartyMembers())
            this.team2Players.add(profile);
    }

    public TeamMatch(Party party, MatchData matchData) {
        this(party.getPartyMembers(), matchData);
    }

    public TeamMatch(Collection<Profile> profiles, MatchData matchData) {
        super(matchData);
        this.participants.addAll(profiles);
        int size = participants.size();
        val team1 = participants.subList(0, (size + 1) / 2);
        val team2 = participants.subList((size + 1) / 2, size);

        for (val profile : team1)
            this.team1Players.add(profile);

        for (val profile : team2)
            this.team2Players.add(profile);
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

        team1Players.alive(teamMember -> this.participants.add(teamMember));
        team2Players.alive(teamMember -> this.participants.add(teamMember));

        this.profile1 = team1Players.firstKey();
        this.profile2 = team2Players.firstKey();
        this.team1RequiredHitCount = team1Players.size() * 100;
        this.team2RequiredHitCount = team2Players.size() * 100;

        this.nametagGroups = setDisplayNameBoard();

        team1Players.alive(teamMember -> {
            prepareForMatch(teamMember);
            PlayerUtil.teleport(teamMember.getPlayer(), location1);
        });

        team2Players.alive(teamMember -> {
            prepareForMatch(teamMember);
            PlayerUtil.teleport(teamMember.getPlayer(), location2);
        });

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

        boolean isTeam1 = team1Players.all().contains(victim);
        val attackerTeam = isTeam1 ? team2Players : team1Players;
        val victimTeam = isTeam1 ? team1Players : team2Players;
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

        victimTeam.reportDeath(victim);

        for (val profile : participants) {
            boolean hasKiller = victim.getKiller() != null;
            var message = hasKiller ? ChatMessages.KILLED_BY_PLAYER : ChatMessages.DIED;
            message = message.clone().replace("%victim%", victim.getName());
            profile.message(hasKiller ? message.replace("%attacker%", victim.getKiller().getName()) : message);
        }

        val victimsAlive = victimTeam.alive();

        if (victimsAlive.size() > 0) {

            participants.remove(victim);
            victim.removeFromMatch();

            if (BotAPI.INSTANCE.despawn(victim.getPlayer().getUniqueId()))
                return;

            victim.getSpectateHandler().spectate(victimsAlive.getFirst());

            return;
        }

        ended = true;

        for (val nametagGroup : nametagGroups) {
            nametagGroup.delete();
            for (val player : nametagGroup.getPlayers())
                refreshBukkitScoreboard(player);
        }

        val attackerTeamIterator = attackerTeam.alive().iterator();

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
                CC.RED + "Loser: " + CC.GRAY + victimTeam.firstKey().getName() + "\'s team");
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
            val player = spectator.getPlayer();
            player.sendMessage(CC.SEPARATOR);
            player.sendMessage(Strings.MATCH_RESULTS);
            player.spigot().sendMessage(winMessage);
            player.spigot().sendMessage(loseMessage);
            player.sendMessage(CC.SEPARATOR);
            spectator.getSpectateHandler().stopSpectating();
        }

        clearWorld();

    }

    @Override
    public ProfileList getTeam(Profile p, boolean alive) {
        val profileSet = team1Players.all().contains(p) ? team1Players
                : team2Players;
        return profileSet.alive();
    }

    @Override
    public Profile getOpponent(Profile p) {
        return team1Players.all().contains(p) ? team2Players.firstKey()
                : team1Players.firstKey();
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

        boolean isTeam1 = team1Players.all().contains(attacker);
        int hitCount = isTeam1 ? ++team1HitCount : ++team2HitCount;
        int requiredHitCount = isTeam1 ? team1RequiredHitCount : team2RequiredHitCount;
        val opponentTeam = isTeam1 ? team2Players : team1Players;

        if (hitCount >= requiredHitCount
                && getData().isBoxing()) {
            opponentTeam.alive(opponent -> end(opponent));
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

    public NametagGroup[] setDisplayNameBoard() {

        val playerGroup = new NametagGroup();
        val opponentGroup = new NametagGroup();

        team1Players.alive(profile -> playerGroup.add(profile.getPlayer()));
        team2Players.alive(profile -> opponentGroup.add(profile.getPlayer()));

        team1Players.alive(profile -> NametagAPI.setPrefix(playerGroup, profile.getName(), CC.GREEN));
        team2Players.alive(profile -> NametagAPI.setPrefix(playerGroup, profile.getName(), CC.RED));

        team2Players.alive(profile -> NametagAPI.setPrefix(opponentGroup, profile.getName(), CC.GREEN));
        team1Players.alive(profile -> NametagAPI.setPrefix(opponentGroup, profile.getName(), CC.RED));

        return new NametagGroup[] { playerGroup, opponentGroup };
    }
}
