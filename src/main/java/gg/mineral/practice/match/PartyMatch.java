package gg.mineral.practice.match;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.PartyMatchScoreboard;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class PartyMatch extends Match {
	public ProfileList team1RemainingPlayers;
	public ProfileList team2RemainingPlayers;

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
			player1m.teleport(location1);
		}

		for (i = 0; i < team2RemainingPlayers.size(); i++) {
			Profile player2m = team2RemainingPlayers.get(i);
			prepareForMatch(player2m, team2sb);
			player2m.teleport(location2);
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

		ProfileList attackerParty;
		ProfileList remaining;

		if (team1RemainingPlayers.contains(victim)) {
			remaining = team1RemainingPlayers;
			attackerParty = team2RemainingPlayers;
		} else {
			remaining = team2RemainingPlayers;
			attackerParty = team1RemainingPlayers;
		}

		int victimAmountOfPots = victim.getNumber(Material.POTION, (short) 16421)
				+ victim.getNumber(Material.MUSHROOM_SOUP, new ItemStack(Material.MUSHROOM_SOUP).getDurability());

		setInventoryStats(victim, 0, victimAmountOfPots);
		victim.setPearlCooldown(0);
		victim.removeFromMatch();
		victim.heal();

		if (remaining.size() <= 1) {

			ended = true;

			for (int i = 0; i < attackerParty.size(); i++) {
				Profile a = attackerParty.get(i);
				int attackerHealth = (int) a.getPlayer().getHealth();
				a.heal();
				a.removePotionEffects();
				int attackerAmountOfPots = a.getNumber(Material.POTION, (short) 16421)
						+ a.getNumber(Material.MUSHROOM_SOUP, new ItemStack(Material.MUSHROOM_SOUP).getDurability());
				setInventoryStats(a, attackerHealth, attackerAmountOfPots);
				a.getPlayer().sendMessage(CC.GOLD + "You won");
				a.setPearlCooldown(0);
				new DefaultScoreboard(a).setBoard();
				a.removeFromMatch();
				Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
					public void run() {
						a.teleportToLobby();
						if (a.isInParty()) {
							a.setInventoryForParty();
						} else {
							a.setInventoryForLobby();
						}
					}
				}, 40);

			}

			MatchManager.remove(this);
			victim.getPlayer().sendMessage(CC.RED + "You lost");
			nameTag.giveTagAfterMatch(profile1.getPlayer(), profile2.getPlayer());
			new DefaultScoreboard(victim).setBoard();

			Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
				public void run() {
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
				}
			}, 1);

			for (Profile p : getSpectators()) {
				p.stopSpectating();
			}

			for (Item item : data.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class)) {
				EntityHuman lastHolder = ((EntityItem) ((CraftItem) item).getHandle()).lastHolder;

				for (Profile participant : participants) {
					if (lastHolder.getBukkitEntity().getUniqueId() == participant.getUUID()) {
						item.remove();
					}
				}
			}

			return;
		}

		remaining.remove(victim);
		participants.remove(victim);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {

			public void run() {
				if (victim.getPlayer().isDead()) {
					victim.getPlayer().getHandle().playerConnection
							.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
				}

				victim.removePotionEffects();
				victim.spectate(remaining.get(0));
				new DefaultScoreboard(victim).setBoard();
			}

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
