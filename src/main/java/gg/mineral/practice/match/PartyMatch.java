package gg.mineral.practice.match;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.scoreboard.Team;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard;
import gg.mineral.practice.scoreboard.impl.PartyBoxingScoreboard;
import gg.mineral.practice.scoreboard.impl.PartyMatchScoreboard;
import gg.mineral.practice.util.CoreConnector;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.Strings;
import gg.mineral.practice.util.world.WorldUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;

public class PartyMatch extends Match {
	@Getter
	ProfileList team1RemainingPlayers, team2RemainingPlayers;
	List<InventoryStatsMenu> team1InventoryStatsMenus = new GlueList<>(), team2InventoryStatsMenus = new GlueList<>();
	Queue<Item> itemRemovalQueue = new ConcurrentLinkedQueue<>();
	@Getter
	int team1HitCount = 0, team2HitCount = 0, team1RequiredHitCount, team2RequiredHitCount;

	public PartyMatch(Party party1, Party party2, MatchData matchData) {
		super(matchData);
		this.profile1 = party1.getPartyLeader();
		this.profile2 = party2.getPartyLeader();
		this.team1RemainingPlayers = new ProfileList(party1.getPartyMembers());
		this.team2RemainingPlayers = new ProfileList(party2.getPartyMembers());
		this.participants.addAll(team1RemainingPlayers);
		this.participants.addAll(team2RemainingPlayers);
		this.team1RequiredHitCount = team1RemainingPlayers.size() * 100;
		this.team2RequiredHitCount = team2RemainingPlayers.size() * 100;
	}

	public PartyMatch(Party party, MatchData matchData) {
		super(matchData);
		this.participants.addAll(party.getPartyMembers());
		int size = participants.size();
		this.team1RemainingPlayers = new ProfileList(participants.subList(0, (size + 1) / 2));
		this.team2RemainingPlayers = new ProfileList(participants.subList((size + 1) / 2, size));
		this.profile1 = team1RemainingPlayers.get(0);
		this.profile2 = team2RemainingPlayers.get(0);
		this.team1RequiredHitCount = team1RemainingPlayers.size() * 100;
		this.team2RequiredHitCount = team2RemainingPlayers.size() * 100;
	}

	@Override
	public void start() {

		if (noArenas()) {
			return;
		}

		MatchManager.registerMatch(this);
		Location location1 = data.getArena().getLocation1().clone();
		Location location2 = data.getArena().getLocation2().clone();
		setupLocations(location1, location2);

		org.bukkit.scoreboard.Scoreboard team1sb = getDisplayNameBoard(team1RemainingPlayers, team2RemainingPlayers);
		org.bukkit.scoreboard.Scoreboard team2sb = getDisplayNameBoard(team2RemainingPlayers, team1RemainingPlayers);

		int i;

		for (i = 0; i < team1RemainingPlayers.size(); i++) {
			Profile teamMember = team1RemainingPlayers.get(i);
			prepareForMatch(teamMember, team1sb);
			PlayerUtil.teleport(teamMember.getPlayer(), location1);
		}

		for (i = 0; i < team2RemainingPlayers.size(); i++) {
			Profile teamMember = team2RemainingPlayers.get(i);
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
		if (data.getBoxing()) {
			p.setScoreboard(PartyBoxingScoreboard.INSTANCE);
			return;
		}

		p.setScoreboard(PartyMatchScoreboard.INSTANCE);
	}

	@Override
	public void end(Profile victim) {
		if (isEnded()) {
			return;
		}

		victim.getMatchStatisticCollector().end();

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
		Collection<Item> items = data.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class);

		addToItemsToRemovalQueue(victim, items);

		if (victimTeam.size() > 0) {
			participants.remove(victim);
			victim.removeFromMatch();
			victim.getSpectateHandler().spectate(victimTeam.get(0));

			if (CoreConnector.connected()) {
				CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(victim.getPlayer(), victim.getPlayer());
			}

			return;
		}

		ended = true;

		Iterator<Profile> attackerTeamIterator = attackerTeam.iterator();

		Profile attackerTeamLeader = attackerTeamIterator.next();

		attackerEndMatch(attackerTeamLeader, attackerInventoryStatsMenus, items);

		while (attackerTeamIterator.hasNext()) {
			Profile attacker = attackerTeamIterator.next();
			attackerEndMatch(attacker, attackerInventoryStatsMenus, items);
		}

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
				CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(profile.getPlayer(), profile.getPlayer());
			}
		}

		participants.remove(victim);

		victim.removePotionEffects();
		victim.teleportToLobby();

		if (victim.isInParty()) {
			victim.getInventory().setInventoryForParty();
		} else {
			victim.getInventory().setInventoryForLobby();
		}

		victim.removeFromMatch();

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

	public void addToItemsToRemovalQueue(Profile profile, Collection<Item> items) {
		for (Item item : items) {
			EntityHuman lastHolder = ((EntityItem) ((CraftItem) item).getHandle()).lastHolder;

			if (lastHolder == null) {
				continue;
			}

			if (lastHolder.getBukkitEntity().getUniqueId() == profile.getUUID()) {
				itemRemovalQueue.add(item);
			}
		}
	}

	@Override
	public void clearWorld() {
		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			if (world != null) {
				WorldUtil.deleteWorld(world);
				return;
			}

			for (Item item : itemRemovalQueue) {
				item.remove();
			}

			for (Location location : buildLog) {
				location.getBlock().setType(Material.AIR);
			}
		}, getPostMatchTime() + 1);
	}

	@Override
	public List<Profile> getTeam(Profile p) {
		return team1RemainingPlayers.contains(p) ? team1RemainingPlayers : team2RemainingPlayers;
	}

	private void attackerEndMatch(Profile attacker, List<InventoryStatsMenu> attackerInventoryStatsMenus,
			Collection<Item> items) {

		attacker.getMatchStatisticCollector().end();

		attackerInventoryStatsMenus
				.add(setInventoryStats(attacker, attacker.getMatchStatisticCollector()));

		attacker.setPearlCooldown(0);
		attacker.heal();
		addToItemsToRemovalQueue(attacker, items);
		attacker.removePotionEffects();
		attacker.getInventory().clear();

		attacker.setScoreboard(MatchEndScoreboard.INSTANCE);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			attacker.teleportToLobby();
			if (attacker.isInParty()) {
				attacker.getInventory().setInventoryForParty();
			} else {
				attacker.getInventory().setInventoryForLobby();
			}
			attacker.removeFromMatch();
			attacker.setScoreboard(DefaultScoreboard.INSTANCE);
		}, getPostMatchTime());
	}

	@Override
	public boolean incrementTeamHitCount(Profile attacker, Profile victim) {
		attacker.getMatchStatisticCollector().increaseHitCount();
		victim.getMatchStatisticCollector().resetCombo();

		boolean isTeam1 = team1RemainingPlayers.contains(attacker);
		int hitCount = isTeam1 ? team1HitCount++ : team2HitCount++;
		int requiredHitCount = isTeam1 ? team1RequiredHitCount : team2RequiredHitCount;
		ProfileList opponentTeam = isTeam1 ? team2RemainingPlayers : team1RemainingPlayers;

		if (hitCount >= requiredHitCount
				&& getData().getBoxing()) {
			for (Profile opponent : opponentTeam) {
				end(opponent);
			}
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

		int i;

		for (i = 0; i < playerTeam.size(); i++) {
			teammates.addEntry(playerTeam.get(i).getName());
		}

		for (i = 0; i < opponentTeam.size(); i++) {
			opponents.addEntry(opponentTeam.get(i).getName());
		}

		return scoreboard;
	}
}
