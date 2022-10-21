package gg.mineral.practice.match;

import java.sql.SQLException;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.util.Countdown;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.WorldUtil;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class PartyMatch extends Match {
	public ProfileList team1RemainingPlayers;
	public ProfileList team2RemainingPlayers;

	public PartyMatch(Party party1, Party party2, MatchData m) {
		super(m);
		this.player1 = party1.getPartyLeader();
		this.player2 = party2.getPartyLeader();
		this.team1RemainingPlayers = new ProfileList(party1.getPartyMembers());
		this.team2RemainingPlayers = new ProfileList(party2.getPartyMembers());
		participants.addAll(team1RemainingPlayers);
		participants.addAll(team2RemainingPlayers);
	}

	public PartyMatch(Party party, MatchData m) {
		super(m);
		participants = new ProfileList(party.getPartyMembers());
		int size = participants.size();
		this.team1RemainingPlayers = new ProfileList(participants.subList(0, (size + 1) / 2));
		this.team2RemainingPlayers = new ProfileList(participants.subList((size + 1) / 2, size));
		this.player1 = team1RemainingPlayers.get(0);
		this.player2 = team2RemainingPlayers.get(0);
	}

	@Override
	public void start() throws SQLException {

		if (m.getArena() == null) {
			PlayerManager.broadcast(participants, ErrorMessages.ARENA_NOT_FOUND);
			end(player1);
			return;
		}

		MatchManager.register(this);
		Location location1 = m.getArena().getLocation1();
		Location location2 = m.getArena().getLocation2();
		location1.setDirection(m.getArena().getLocation1EyeVector());
		location2.setDirection(m.getArena().getLocation2EyeVector());

		setWorldParameters(((CraftWorld) location1.getWorld()).getHandle());

		org.bukkit.scoreboard.Scoreboard team1sb = getDisplayNameBoard(team1RemainingPlayers, team2RemainingPlayers);
		org.bukkit.scoreboard.Scoreboard team2sb = getDisplayNameBoard(team2RemainingPlayers, team1RemainingPlayers);

		int i;

		for (i = 0; i < team1RemainingPlayers.size(); i++) {

			Profile player1m = team1RemainingPlayers.get(i);

			if (!player1m.bukkit().isOnline()) {
				team1RemainingPlayers.remove(player1m);
				return;
			}

			prepareForMatch(player1m);
			player1m.teleport(location1);
			player1m.bukkit().setScoreboard(team1sb);
		}

		for (i = 0; i < team2RemainingPlayers.size(); i++) {

			Profile player2m = team2RemainingPlayers.get(i);

			if (!player2m.bukkit().isOnline()) {
				team2RemainingPlayers.remove(player2m);
				return;
			}

			prepareForMatch(player2m);
			player2m.teleport(location2);
			player2m.bukkit().setScoreboard(team2sb);
		}

		Countdown countdown = new Countdown(5, this);
		countdown.start();
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
				int attackerHealth = (int) a.bukkit().getHealth();
				a.heal();
				a.removePotionEffects();
				int attackerAmountOfPots = a.getNumber(Material.POTION, (short) 16421)
						+ a.getNumber(Material.MUSHROOM_SOUP, new ItemStack(Material.MUSHROOM_SOUP).getDurability());
				setInventoryStats(a, attackerHealth, attackerAmountOfPots);
				a.bukkit().sendMessage(CC.GOLD + "You won");
				a.setPearlCooldown(0);
				new Scoreboard(a).setBoard();
				a.removeFromMatch();
				Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
					a.teleportToLobby();
					if (a.isInParty()) {
						a.setInventoryForParty();
					} else {
						a.setInventoryForLobby();
					}
				}, 40);

			}

			MatchManager.remove(this);
			victim.bukkit().sendMessage(CC.RED + "You lost");
			new Scoreboard(victim).setBoard();

			Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
				if (victim.bukkit().isDead()) {
					victim.bukkit().getHandle().playerConnection
							.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
				}

				victim.removePotionEffects();
				victim.teleportToLobby();

				if (victim.isInParty()) {
					victim.setInventoryForParty();
				} else {
					victim.setInventoryForLobby();
				}
			}, 1);

			if (getSpectators().size() > 0) {
				Iterator<Profile> it = getSpectators().iterator();
				while (it.hasNext()) {
					Profile p = it.next();
					p.stopSpectating();
				}
			}

			if (world != null) {
				WorldUtil.deleteWorld(world);
				return;
			}

			for (Item item : m.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class)) {
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

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			if (victim.bukkit().isDead()) {
				victim.bukkit().getHandle().playerConnection
						.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
			}

			victim.removePotionEffects();
			victim.spectate(participants.get(0));
			new Scoreboard(victim).setBoard();
		}, 1);
	}

	@Override
	public ProfileList getTeam(Profile p) {
		if (team1RemainingPlayers.contains(p)) {
			return team1RemainingPlayers;
		}

		return team2RemainingPlayers;
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
