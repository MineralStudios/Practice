package gg.mineral.practice.match;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
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
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class PartyMatch extends Match {
	@Getter
	ProfileList team1RemainingPlayers, team2RemainingPlayers;
	List<InventoryStatsMenu> team1InventoryStatsMenus = new GlueList<>(), team2InventoryStatsMenus = new GlueList<>();
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
		nameTag.clearTagOnMatchStart(profile1.getPlayer(), profile2.getPlayer());
		Location location1 = data.getArena().getLocation1().clone();
		Location location2 = data.getArena().getLocation2().clone();
		setupLocations(location1, location2);

		org.bukkit.scoreboard.Scoreboard team1sb = getDisplayNameBoard(team1RemainingPlayers, team2RemainingPlayers);
		org.bukkit.scoreboard.Scoreboard team2sb = getDisplayNameBoard(team2RemainingPlayers, team1RemainingPlayers);

		int i;

		for (i = 0; i < team1RemainingPlayers.size(); i++) {
			Profile player1m = team1RemainingPlayers.get(i);
			prepareForMatch(player1m, team1sb);
			PlayerUtil.teleport(player1m.getPlayer(), location1);
		}

		for (i = 0; i < team2RemainingPlayers.size(); i++) {
			Profile player2m = team2RemainingPlayers.get(i);
			prepareForMatch(player2m, team2sb);
			PlayerUtil.teleport(player2m.getPlayer(), location2);
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
			new PartyBoxingScoreboard(p).setBoard();
			return;
		}

		new PartyMatchScoreboard(p).setBoard();
	}

	@Override
	public void end(Profile victim) {
		if (ended) {
			return;
		}

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

		int victimAmountOfPots = victim.getInventory().getNumber(Material.POTION, (short) 16421)
				+ victim.getInventory().getNumber(Material.MUSHROOM_SOUP,
						new ItemStack(Material.MUSHROOM_SOUP).getDurability());

		Collection<PotionEffect> victimPotionEffects = victim.getPlayer().getActivePotionEffects();
		victimInventoryStatsMenus.add(setInventoryStats(victim, 0, victimAmountOfPots, victimPotionEffects));
		victim.setPearlCooldown(0);
		victim.removeFromMatch();
		victim.heal();

		if (victimTeam.size() <= 1) {

			ended = true;

			Iterator<Profile> attackerTeamIterator = attackerTeam.iterator();

			Profile attackerTeamLeader = attackerTeamIterator.next();

			while (attackerTeamIterator.hasNext()) {
				Profile attacker = attackerTeamIterator.next();
				int attackerHealth = (int) attacker.getPlayer().getHealth();
				Collection<PotionEffect> attackerPotionEffects = attacker.getPlayer().getActivePotionEffects();
				attacker.heal();
				attacker.removePotionEffects();
				int attackerAmountOfPots = attacker.getInventory().getNumber(Material.POTION, (short) 16421)
						+ attacker.getInventory().getNumber(Material.MUSHROOM_SOUP,
								new ItemStack(Material.MUSHROOM_SOUP).getDurability());
				attackerInventoryStatsMenus
						.add(setInventoryStats(attacker, attackerHealth, attackerAmountOfPots, attackerPotionEffects));
				attacker.setPearlCooldown(0);
				new MatchEndScoreboard(attacker).setBoard();
				attacker.removeFromMatch();
				Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
					attacker.teleportToLobby();
					if (attacker.isInParty()) {
						attacker.setInventoryForParty();
					} else {
						attacker.setInventoryForLobby();
					}
					new DefaultScoreboard(attacker).setBoard();
				}, 40);

			}

			ProfileManager.setTeamInventoryStats(attackerTeamLeader, attackerInventoryStatsMenus);
			ProfileManager.setTeamInventoryStats(victim, victimInventoryStatsMenus);
			MatchManager.remove(this);
			String viewinv = CC.YELLOW + "Match Results";
			TextComponent winmessage = new TextComponent(
					CC.GREEN + "Winner: " + CC.GRAY + attackerTeamLeader.getName() + "\'s party");
			TextComponent losemessage = new TextComponent(
					CC.RED + "Loser: " + CC.GRAY + victim.getName() + "\'s party");
			losemessage
					.setHoverEvent(
							new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(CC.RED + "Hits: " + victimTeamHits)
											.create()));
			winmessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(CC.GREEN + "Hits: " + attackerTeamHits).create()));
			losemessage
					.setClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewteaminventory " + victim.getName()));
			winmessage.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							"/viewteaminventory " + attackerTeamLeader.getName()));

			for (Profile profile : participants) {
				profile.getPlayer().sendMessage(CC.SEPARATOR);
				profile.getPlayer().sendMessage(viewinv);
				profile.getPlayer().spigot().sendMessage(winmessage);
				profile.getPlayer().spigot().sendMessage(losemessage);
				profile.getPlayer().sendMessage(CC.SEPARATOR);
				nameTag.giveTagAfterMatch(profile.getPlayer(), profile.getPlayer());
			}

			new DefaultScoreboard(victim).setBoard();

			Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
				if (victim.getPlayer().isDead()) {
					victim.getPlayer().getHandle().playerConnection
							.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
				}

				victim.removePotionEffects();
				victim.teleportToLobby();

				if (victim.isInParty()) {
					victim.setInventoryForParty();
				} else {
					victim.setInventoryForLobby();
				}

				for (Profile p : getSpectators()) {
					p.getPlayer().sendMessage(CC.SEPARATOR);
					p.getPlayer().sendMessage(viewinv);
					p.getPlayer().spigot().sendMessage(winmessage);
					p.getPlayer().spigot().sendMessage(losemessage);
					p.getPlayer().sendMessage(CC.SEPARATOR);
					p.stopSpectating();
				}

				clearWorld();

			}, 1);

			return;
		}

		victimTeam.remove(victim);
		participants.remove(victim);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			if (victim.getPlayer().isDead()) {
				victim.getPlayer().getHandle().playerConnection
						.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
			}

			victim.removePotionEffects();
			victim.spectate(victimTeam.get(0));
			nameTag.giveTagAfterMatch(victim.getPlayer(), victim.getPlayer());
			new DefaultScoreboard(victim).setBoard();

		}, 1);
	}

	@Override
	public List<Profile> getTeam(Profile p) {
		return team1RemainingPlayers.contains(p) ? team1RemainingPlayers : team2RemainingPlayers;
	}

	@Override
	public boolean incrementTeamHitCount(Profile attacker, Profile victim) {
		attacker.increaseHitCount();
		victim.resetCombo();

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
