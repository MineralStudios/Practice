package gg.mineral.practice.entity;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.inventory.PlayerInventory;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.inventory.menus.MechanicsMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.kit.KitCreator;
import gg.mineral.practice.kit.KitEditor;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.match.data.MatchStatisticCollector;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.request.DuelRequest;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.FollowingScoreboard;
import gg.mineral.practice.scoreboard.impl.SpectatorScoreboard;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.AutoExpireList;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.math.PearlCooldown;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.Message;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Profile {
	@Getter
	final CraftPlayer player;
	@Getter
	final PlayerInventory inventory;
	@Getter
	Match match, spectatingMatch;
	@Setter
	@Getter
	DefaultScoreboard scoreboard;
	@Getter
	MatchData matchData;
	@Getter
	MatchStatisticCollector matchStatisticCollector = new MatchStatisticCollector(this);
	@Getter
	boolean playersVisible = true, partyOpenCooldown = false, inMatchCountdown = false;
	@Getter
	@Setter
	PracticeMenu openMenu;
	@Getter
	Profile following;
	@Getter
	GlueList<Profile> followers = new ProfileList();
	@Getter
	AutoExpireList<DuelRequest> recievedDuelRequests = new AutoExpireList<>();
	@Getter
	AutoExpireList<Party> recievedPartyRequests = new AutoExpireList<>();
	@Getter
	@Setter
	boolean duelRequests = true, partyRequests = true;
	@Getter
	@Setter
	Profile duelReciever;
	@Getter
	PlayerStatus playerStatus = PlayerStatus.IDLE;
	@Getter
	Party party;
	@Getter
	KitEditor kitEditor;
	@Getter
	KitCreator kitCreator;
	@Getter
	Tournament tournament;
	@Getter
	Event event;
	@Getter
	PearlCooldown pearlCooldown = new PearlCooldown(this);
	Event spectatingEvent;

	public Profile(org.bukkit.entity.Player player) {
		this.player = (CraftPlayer) player;
		this.matchData = new MatchData();
		this.inventory = new PlayerInventory(this);
		pearlCooldown.start();
	}

	public void openMenu(PracticeMenu m) {
		m.open(this);
	}

	public void setPlayersVisible(boolean playersVisible) {
		if (getPlayerStatus() != PlayerStatus.IDLE && getPlayerStatus() != PlayerStatus.QUEUEING) {
			return;
		}

		this.playersVisible = playersVisible;
	}

	public void removeScoreboard() {
		scoreboard = null;
	}

	public void heal() {
		this.player.setFoodLevel(20);
		this.player.setHealth(20);
		this.player.setSaturation(20);
		this.player.setFireTicks(0);
	}

	public void setMatch(Match match) {
		this.match = match;
		setPlayerStatus(PlayerStatus.FIGHTING);
	}

	public boolean isInParty() {
		return party != null;
	}

	public void removeFromMatch() {
		setPlayerStatus(PlayerStatus.IDLE);
		match = null;
	}

	public void addToParty(Party party) {
		party.add(this);
		this.party = party;
		getInventory().setInventoryForParty();
	}

	public void removeFromParty() {

		if (party != null) {
			party.remove(this);
		}

		this.party = null;

		if (playerStatus != PlayerStatus.IDLE) {
			return;
		}

		getInventory().setInventoryForLobby();
	}

	public void giveKit(Kit kit) {
		getInventory().setContents(kit.getContents());
		getInventory().setArmorContents(kit.getArmourContents());
	}

	public void removePotionEffects() {
		Iterator<PotionEffect> it = player.getActivePotionEffects().iterator();

		while (it.hasNext()) {
			try {
				PotionEffect e = it.next();

				if (e == null) {
					continue;
				}

				player.removePotionEffect(e.getType());
			} catch (Exception ex) {
				continue;
			}
		}
	}

	public void message(Message m) {
		m.send(this.getPlayer());
	}

	public String getName() {
		return player.getName();
	}

	public void removeFromQueue() {
		QueueSearchTask.removePlayer(this);
		message(ChatMessages.LEFT_QUEUE);
		setPlayerStatus(PlayerStatus.IDLE);
	}

	public void addPlayerToQueue(QueueEntry queueEntry) {
		this.player.getOpenInventory().close();

		if (playerStatus != PlayerStatus.IDLE) {
			return;
		}

		setPlayerStatus(PlayerStatus.QUEUEING);
		getInventory().setInventoryForQueue();
		QueueSearchTask.addPlayer(this, queueEntry);
		message(ChatMessages.JOINED_QUEUE.clone().replace("%queue%", queueEntry.getQueuetype().getDisplayName())
				.replace("%gametype%", queueEntry.getGametype().getDisplayName()));
	}

	public void stopSpectating() {
		if (spectatingMatch != null) {
			spectatingMatch.getSpectators().remove(this);
			spectatingMatch = null;
		}

		if (spectatingEvent != null) {
			spectatingEvent.getSpectators().remove(this);
			spectatingEvent = null;
		}

		teleportToLobby();

		if (playerStatus != PlayerStatus.FOLLOWING) {
			this.player.setGameMode(GameMode.SURVIVAL);
			if (this.isInParty()) {
				getInventory().setInventoryForParty();
			} else {
				getInventory().setInventoryForLobby();
			}
			new DefaultScoreboard(this).setBoard();
			return;
		}

		getInventory().setInventoryToFollow();
	}

	public void stopSpectatingAndFollowing() {

		if (playerStatus != PlayerStatus.SPECTATING && playerStatus != PlayerStatus.FOLLOWING) {
			message(ErrorMessages.NOT_SPEC_OR_FOLLOWING);
			return;
		}

		if (spectatingMatch != null) {
			spectatingMatch.getSpectators().remove(this);
			spectatingMatch = null;
		}

		if (playerStatus == PlayerStatus.FOLLOWING) {
			following.followers.remove(this);
			following = null;
			this.setPlayerStatus(PlayerStatus.IDLE);
		}

		this.player.setGameMode(GameMode.SURVIVAL);

		teleportToLobby();
		if (this.isInParty()) {
			getInventory().setInventoryForParty();
		} else {
			getInventory().setInventoryForLobby();
		}
		new DefaultScoreboard(this).setBoard();
	}

	public void spectate(Profile p) {

		if (p.equals(this)) {
			message(ErrorMessages.NOT_SPEC_SELF);
			return;
		}

		if (p.isInEvent()) {
			spectateEvent(p.getEvent());
			return;
		}

		if (p.getPlayerStatus() != PlayerStatus.FIGHTING) {
			message(ErrorMessages.PLAYER_NOT_IN_MATCH);
			return;
		}

		if (getPlayerStatus() != PlayerStatus.IDLE && getPlayerStatus() != PlayerStatus.FOLLOWING) {
			message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		Match match = p.getMatch();
		match.getSpectators().add(this);
		spectatingMatch = match;

		this.player.setGameMode(GameMode.SPECTATOR);

		PlayerUtil.teleport(this.getPlayer(), p.getPlayer());
		ChatMessages.SPECTATING.clone().replace("%player%", p.getName()).send(getPlayer());

		ChatMessages.STOP_SPECTATING.send(getPlayer());

		ChatMessage broadcastedMessage = ChatMessages.SPECTATING_YOUR_MATCH.clone().replace("%player%", getName());
		ProfileManager.broadcast(match.getParticipants(), broadcastedMessage);

		getInventory().setInventoryForSpectating();

		if (getPlayerStatus() == PlayerStatus.FOLLOWING) {
			return;
		}

		new SpectatorScoreboard(this).setBoard();
		setPlayerStatus(PlayerStatus.SPECTATING);
	}

	public void spectateEvent(Event event) {

		if (event.isEnded()) {
			return;
		}

		if (getPlayerStatus() != PlayerStatus.IDLE && getPlayerStatus() != PlayerStatus.FOLLOWING) {
			message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		spectatingEvent = event;
		event.getSpectators().add(this);

		this.player.setGameMode(GameMode.SPECTATOR);

		PlayerUtil.teleport(this.getPlayer(), event.getEventArena().getWaitingLocation());
		ChatMessages.SPECTATING_EVENT.send(getPlayer());
		ChatMessages.STOP_SPECTATING.send(getPlayer());

		getInventory().setInventoryForSpectating();

		if (getPlayerStatus() == PlayerStatus.FOLLOWING) {
			return;
		}

		setPlayerStatus(PlayerStatus.SPECTATING);
	}

	public void teleportToLobby() {
		if (playerStatus == PlayerStatus.FIGHTING && !getMatch().isEnded()) {
			return;
		}

		PlayerUtil.teleport(this.getPlayer(), ProfileManager.getSpawnLocation());

		if (playerStatus != PlayerStatus.FOLLOWING && playerStatus != PlayerStatus.QUEUEING) {
			setPlayerStatus(PlayerStatus.IDLE);
		}
	}

	public void sendDuelRequest(Profile profile) {
		getPlayer().closeInventory();

		if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
			message(ErrorMessages.PLAYER_NOT_IN_LOBBY);
			return;
		}

		if (!profile.isDuelRequests()) {
			message(ErrorMessages.DUEL_REQUESTS_DISABLED);
			return;
		}

		for (DuelRequest d : profile.getRecievedDuelRequests()) {
			if (d.getSender().equals(this)) {
				message(ErrorMessages.DUEL_REQUEST_ALREADY_SENT);
				return;
			}
		}

		String sender = getName();

		if (isInParty()) {
			if (!getParty().getPartyLeader().equals(this)) {
				message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
				return;
			}

			sender += "'s party (" + getParty().getPartyMembers().size() + ") ";
		}

		DuelRequest request = new DuelRequest(this, matchData);
		profile.getRecievedDuelRequests().add(request);
		removeFromQueue();
		ChatMessages.DUEL_REQUEST_SENT.clone().replace("%player%", profile.getName()).send(getPlayer());

		HoverEvent DATA = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(matchData.toString()).create());

		ChatMessages.DUEL_REQUEST_RECIEVED.clone().replace("%player%", sender)
				.setTextEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + getName()),
						DATA)
				.send(profile.getPlayer());
	}

	public void leaveKitEditor() {
		new DefaultScoreboard(this).setBoard();
		getInventory().setInventoryClickCancelled(true);
		this.kitEditor = null;
		teleportToLobby();
		getInventory().setInventoryForLobby();
	}

	public void leaveKitCreator() {
		new DefaultScoreboard(this).setBoard();
		this.player.setGameMode(GameMode.SURVIVAL);
		leaveKitEditor();
		openMenu(new MechanicsMenu(kitCreator.getSubmitAction()));
		this.kitCreator = null;
	}

	public void sendToKitEditor(QueueEntry queueEntry) {

		if (KitEditorManager.getLocation() == null) {
			message(ErrorMessages.KIT_EDITOR_LOCATION_NOT_SET);
			return;
		}

		this.kitEditor = new KitEditor(queueEntry, this);
		kitEditor.start();
	}

	public void sendToKitCreator(SubmitAction submitAction) {

		if (KitEditorManager.getLocation() == null) {
			message(ErrorMessages.KIT_EDITOR_LOCATION_NOT_SET);
			return;
		}

		this.kitCreator = new KitCreator(this, submitAction);
		kitCreator.start();
	}

	public boolean isInKitEditor() {
		return kitEditor != null;
	}

	public boolean isInKitCreator() {
		return kitCreator != null;
	}

	public boolean equals(Profile p) {
		return p.getUUID().equals(player.getUniqueId());
	}

	public UUID getUUID() {
		return player.getUniqueId();
	}

	public void setPlayerStatus(PlayerStatus newPlayerStatus) {

		List<org.bukkit.entity.Player> playersInWorld = getPlayer().getWorld().getPlayers();

		if (playerStatus != PlayerStatus.QUEUEING) {

			switch (newPlayerStatus) {
				case IDLE:
					if (!this.isPlayersVisible()) {
						for (Player player : playersInWorld) {
							getPlayer().hidePlayer(player, false);
						}

						break;
					}

					for (Player player : playersInWorld) {
						getPlayer().showPlayer(player);
					}

					break;
				case SPECTATING:
					List<Profile> participants = this.getSpectatingMatch().getParticipants();

					for (Profile profile : participants) {
						this.getPlayer().showPlayer(profile.getPlayer());
					}

					for (Match match : MatchManager.getMatches()) {
						match.updateVisiblity(this.getSpectatingMatch(), this);
					}

					break;
				default:

			}
		}

		playerStatus = newPlayerStatus;
	}

	public void follow(Profile p) {

		if (getPlayerStatus() != PlayerStatus.IDLE) {
			message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		setPlayerStatus(PlayerStatus.FOLLOWING);
		following = p;
		p.getFollowers().add(this);
		getInventory().setInventoryToFollow();
		new FollowingScoreboard(this).setBoard();

		if (p.getPlayerStatus() == PlayerStatus.FIGHTING) {
			spectate(following);
		}
	}

	public void resetMatchData() {
		matchData = new MatchData();
	}

	public void setPearlCooldown(int i) {
		pearlCooldown.setTimeRemaining(i);
	}

	ArmorStand matchCountdownEntity;

	public void setInMatchCountdown(boolean c) {
		inMatchCountdown = c;

		if (c) {
			matchCountdownEntity = player.getWorld().spawn(getPlayer().getLocation(), ArmorStand.class);
			matchCountdownEntity.setVisible(false);
			matchCountdownEntity.setPassenger(player);
			return;
		}

		if (matchCountdownEntity != null) {
			matchCountdownEntity.remove();
			matchCountdownEntity = null;
		}

		this.player.setSaturation(20);
		this.player.setFoodLevel(20);
	}

	public void startPartyOpenCooldown() {
		if (partyOpenCooldown) {
			return;
		}

		partyOpenCooldown = true;

		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PracticePlugin.INSTANCE,
				() -> partyOpenCooldown = false, 400);
	}

	public void setTournament(Tournament tournament) {
		getInventory().setInventoryForTournament();
		this.tournament = tournament;
	}

	public void setEvent(Event event) {
		getInventory().setInventoryForEvent();
		this.event = event;
	}

	public boolean isInTournament() {
		return this.tournament != null;
	}

	public boolean isInEvent() {
		return this.tournament != null;
	}

	public void removeFromTournament() {

		teleportToLobby();
		getInventory().setInventoryForLobby();

		if (tournament == null) {
			return;
		}

		tournament.removePlayer(this);

		tournament = null;
	}

	public void removeFromEvent() {

		teleportToLobby();
		getInventory().setInventoryForLobby();

		if (tournament == null) {
			return;
		}

		event.removePlayer(this);

		event = null;
	}
}
