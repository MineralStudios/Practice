package gg.mineral.practice.match;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
import gg.mineral.practice.scoreboard.impl.PartyMatchScoreboard;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class PartyMatch extends Match {
	ProfileList team1RemainingPlayers, team2RemainingPlayers;
	List<InventoryStatsMenu> team1InventoryStatsMenus = new GlueList<>(), team2InventoryStatsMenus = new GlueList<>();

	public PartyMatch(Party party1, Party party2, MatchData matchData) {
		super(matchData);
		this.profile1 = party1.getPartyLeader();
		this.profile2 = party2.getPartyLeader();
		this.team1RemainingPlayers = new ProfileList(party1.getPartyMembers());
		this.team2RemainingPlayers = new ProfileList(party2.getPartyMembers());
		participants.addAll(team1RemainingPlayers);
		participants.addAll(team2RemainingPlayers);
	}

	public PartyMatch(Party party, MatchData matchData) {
		super(matchData);
		participants = new ProfileList(party.getPartyMembers());
		int size = participants.size();
		this.team1RemainingPlayers = new ProfileList(participants.subList(0, (size + 1) / 2));
		this.team2RemainingPlayers = new ProfileList(participants.subList((size + 1) / 2, size));
		this.profile1 = team1RemainingPlayers.get(0);
		this.profile2 = team2RemainingPlayers.get(0);
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

		if (team1RemainingPlayers.contains(victim)) {
			victimTeam = team1RemainingPlayers;
			attackerTeam = team2RemainingPlayers;
			victimInventoryStatsMenus = team1InventoryStatsMenus;
			attackerInventoryStatsMenus = team2InventoryStatsMenus;
		} else {
			victimTeam = team2RemainingPlayers;
			attackerTeam = team1RemainingPlayers;
			victimInventoryStatsMenus = team2InventoryStatsMenus;
			attackerInventoryStatsMenus = team1InventoryStatsMenus;
		}

		int victimAmountOfPots = victim.getInventory().getNumber(Material.POTION, (short) 16421)
				+ victim.getInventory().getNumber(Material.MUSHROOM_SOUP,
						new ItemStack(Material.MUSHROOM_SOUP).getDurability());

		victimInventoryStatsMenus.add(setInventoryStats(victim, 0, victimAmountOfPots));
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
				attacker.heal();
				attacker.removePotionEffects();
				int attackerAmountOfPots = attacker.getInventory().getNumber(Material.POTION, (short) 16421)
						+ attacker.getInventory().getNumber(Material.MUSHROOM_SOUP,
								new ItemStack(Material.MUSHROOM_SOUP).getDurability());
				attackerInventoryStatsMenus.add(setInventoryStats(attacker, attackerHealth, attackerAmountOfPots));
				attacker.setPearlCooldown(0);
				new DefaultScoreboard(attacker).setBoard();
				attacker.removeFromMatch();
				Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
					attacker.teleportToLobby();
					if (attacker.isInParty()) {
						attacker.setInventoryForParty();
					} else {
						attacker.setInventoryForLobby();
					}
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
