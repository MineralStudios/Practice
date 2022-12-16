package gg.mineral.practice.entity;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.bukkit.event.PlayerStatusChangeEvent;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.inventory.PlayerInventory;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.inventory.menus.MechanicsMenu;
import gg.mineral.practice.inventory.menus.SelectGametypeMenu;
import gg.mineral.practice.inventory.menus.SelectModeMenu;
import gg.mineral.practice.inventory.menus.SelectQueuetypeMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.PlayerSettingsManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.request.DuelRequest;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.collection.AutoExpireList;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.math.PearlCooldown;
import gg.mineral.practice.util.messages.CC;
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
	Integer hitCount = 0, currentCombo = 0, longestCombo = 0;
	@Getter
	boolean playersVisible = true, partyOpenCooldown = false, inMatchCountdown = false;
	@Getter
	@Setter
	PracticeMenu openMenu;
	Profile following;
	@Getter
	GlueList<Profile> followers = new ProfileList();
	@Getter
	AutoExpireList<DuelRequest> recievedDuelRequests = new AutoExpireList<>();
	@Getter
	AutoExpireList<Party> recievedPartyRequests = new AutoExpireList<>();
	@Getter
	@Setter
	boolean requests = true, inventoryClickCancelled = false;
	@Getter
	@Setter
	Profile duelReciever;
	@Getter
	PlayerStatus playerStatus = PlayerStatus.IN_LOBBY;
	@Getter
	Party party;
	@Getter
	QueueEntry kitEditorData;
	@Setter
	SubmitAction previousSubmitAction;
	@Getter
	Tournament tournament;
	@Getter
	Event event;
	@Getter
	PearlCooldown pearlCooldown = new PearlCooldown(this);
	Event spectatingTournament;

	public Profile(org.bukkit.entity.Player player) {
		this.player = (CraftPlayer) player;
		this.matchData = new MatchData();
		this.inventory = new PlayerInventory(player.getInventory());
		pearlCooldown.start();
	}

	public void openMenu(PracticeMenu m) {
		m.open(this);
	}

	public void setPlayersVisible(boolean playersVisible) {
		if (getPlayerStatus() != PlayerStatus.IN_LOBBY && getPlayerStatus() != PlayerStatus.IN_QUEUE) {
			return;
		}

		this.playersVisible = playersVisible;
	}

	public void removeScoreboard() {
		scoreboard = null;
	}

	public void increaseHitCount() {
		hitCount++;
		currentCombo++;
		longestCombo = Math.max(currentCombo, longestCombo);
	}

	public void resetCombo() {
		currentCombo = 0;
	}

	public void clearHitCount() {
		hitCount = 0;
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
		setPlayerStatus(PlayerStatus.IN_LOBBY);
		match = null;
	}

	public void addToParty(Party party) {
		party.add(this);
		this.party = party;
		setInventoryForParty();
	}

	public void removeFromParty() {

		if (party != null) {
			party.remove(this);
		}

		this.party = null;

		if (playerStatus != PlayerStatus.IN_LOBBY) {
			return;
		}

		setInventoryForLobby();
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
				player.removePotionEffect(e.getType());
			} catch (Exception ex) {
				continue;
			}
		}
	}

	public int getNumber(Material m, short durability) {
		int i = 0;

		for (ItemStack itemStack : getInventory().getContents()) {

			if (itemStack == null) {
				continue;
			}

			if (itemStack.getType() != m) {
				continue;
			}

			if (itemStack.getDurability() != durability) {
				continue;
			}

			i++;
		}

		return i;
	}

	public int getNumber(Material m) {
		int i = 0;

		for (ItemStack itemStack : getInventory().getContents()) {

			if (itemStack == null) {
				continue;
			}

			if (itemStack.getType() != m) {
				continue;
			}

			i++;
		}

		return i;
	}

	public void message(Message m) {
		m.send(this.getPlayer());
	}

	public String getName() {
		return player.getName();
	}

	public void setInventoryToFollow() {
		setInventoryClickCancelled(true);
		inventory.clear();
		inventory.setItem(0, ItemStacks.STOP_FOLLOWING, (Runnable) this::stopSpectatingAndFollowing);
		getPlayer().updateInventory();
	}

	public void setInventoryForTournament() {
		setInventoryClickCancelled(true);
		inventory.clear();
		inventory.setItem(0, ItemStacks.WAIT_TO_LEAVE,
				(Runnable) () -> this.message(ErrorMessages.CAN_NOT_LEAVE_YET));

		Profile pl = this;

		new BukkitRunnable() {
			@Override
			public void run() {
				inventory.setItem(0, ItemStacks.LEAVE_TOURNAMENT, (Runnable) pl::removeFromTournament);
			}
		}.runTaskLater(PracticePlugin.INSTANCE, 20);
		getPlayer().updateInventory();
	}

	public void setInventoryForEvent() {
		setInventoryClickCancelled(true);
		inventory.clear();
		inventory.setItem(0, ItemStacks.WAIT_TO_LEAVE,
				(Runnable) () -> this.message(ErrorMessages.CAN_NOT_LEAVE_YET));

		Profile pl = this;

		new BukkitRunnable() {
			@Override
			public void run() {
				inventory.setItem(0, ItemStacks.LEAVE_EVENT, (Runnable) pl::removeFromEvent);
			}
		}.runTaskLater(PracticePlugin.INSTANCE, 20);
		getPlayer().updateInventory();
	}

	public void setInventoryForParty() {
		setInventoryClickCancelled(true);
		inventory.clear();
		inventory.setItem(0, ItemStacks.WAIT_TO_LEAVE, (Runnable) () -> this.message(ErrorMessages.CAN_NOT_LEAVE_YET));

		new BukkitRunnable() {
			@Override
			public void run() {
				inventory.setItem(0, ItemStacks.LEAVE_PARTY, p -> p.getPlayer().performCommand("p leave"));
			}
		}.runTaskLater(PracticePlugin.INSTANCE, 20);

		inventory.setItem(1, ItemStacks.LIST_PLAYERS, p -> p.getPlayer().performCommand("p list"));
		inventory.setItem(4, ItemStacks.DUEL, p -> p.getPlayer().performCommand("duel"));
		inventory.setItem(5, ItemStacks.PARTY_SPLIT, p -> {
			p.openMenu(new SelectModeMenu(SubmitAction.P_SPLIT));
			return true;
		});
		inventory.setItem(3, ItemStacks.OPEN_PARTY, p -> p.getPlayer().performCommand("p open"));
		getPlayer().updateInventory();
	}

	public void setInventoryForLobby() {
		setInventoryClickCancelled(true);
		inventory.clear();

		GlueList<Queuetype> list = QueuetypeManager.getQueuetypes();

		for (int i = 0; i < list.size(); i++) {
			Queuetype q = list.get(i);
			try {
				ItemStack item = new ItemBuilder(q.getDisplayItem())
						.name(CC.SECONDARY + CC.B + q.getDisplayName()).build();
				inventory.setItem(q.getSlotNumber(), item,
						p -> {
							p.openMenu(new SelectGametypeMenu(q, true, false));
							return true;
						});
			} catch (NullPointerException e) {
				continue;
			}
		}

		if (KitEditorManager.getEnabled()) {
			ItemStack editor = new ItemBuilder(KitEditorManager.getDisplayItem())
					.name(CC.SECONDARY + CC.B + KitEditorManager.getDisplayName())
					.build();
			inventory.setItem(KitEditorManager.getSlot(), editor,
					p -> {
						p.openMenu(new SelectQueuetypeMenu());
						return true;
					});
		}

		if (PartyManager.getEnabled()) {
			ItemStack parties = new ItemBuilder(PartyManager.getDisplayItem())
					.name(CC.SECONDARY + CC.B + PartyManager.getDisplayName())
					.build();
			inventory.setItem(PartyManager.getSlot(), parties,
					p -> p.getPlayer().performCommand("p create"));
		}

		if (PlayerSettingsManager.getEnabled()) {
			ItemStack settings = new ItemBuilder(PlayerSettingsManager.getDisplayItem())
					.name(CC.SECONDARY + CC.B + PlayerSettingsManager.getDisplayName())
					.build();
			inventory.setItem(PlayerSettingsManager.getSlot(), settings,
					p -> p.getPlayer().performCommand("settings"));
		}
		getPlayer().updateInventory();
	}

	public void setInventoryForQueue() {
		setInventoryClickCancelled(true);
		inventory.clear();

		getInventory().setItem(0, ItemStacks.LEAVE_QUEUE,
				() -> {
					this.removeFromQueue();
					this.setInventoryForLobby();
				});
		getPlayer().updateInventory();
	}

	public void setInventoryForSpectating() {
		setInventoryClickCancelled(true);
		inventory.clear();
		getInventory().setItem(0, ItemStacks.STOP_SPECTATING, (Runnable) this::stopSpectatingAndFollowing);
		getPlayer().updateInventory();
	}

	public void removeFromQueue() {
		QueueSearchTask.removePlayer(this);
		setPlayerStatus(PlayerStatus.IN_LOBBY);
	}

	public void addPlayerToQueue(QueueEntry qd) {
		this.player.getOpenInventory().close();

		if (playerStatus != PlayerStatus.IN_LOBBY) {
			return;
		}

		setPlayerStatus(PlayerStatus.IN_QUEUE);
		setInventoryForQueue();
		QueueSearchTask.addPlayer(this, qd);
	}

	public void stopSpectating() {
		if (spectatingMatch != null) {
			spectatingMatch.getSpectators().remove(this);
			spectatingMatch = null;
		}

		if (spectatingTournament != null) {
			spectatingTournament.getSpectators().remove(this);
			spectatingTournament = null;
		}

		teleportToLobby();

		if (playerStatus != PlayerStatus.FOLLOWING) {
			this.player.setGameMode(GameMode.SURVIVAL);
			if (this.isInParty()) {
				this.setInventoryForParty();
			} else {
				this.setInventoryForLobby();
			}
			return;
		}

		this.setInventoryToFollow();
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
			this.setPlayerStatus(PlayerStatus.IN_LOBBY);
		}

		this.player.setGameMode(GameMode.SURVIVAL);

		teleportToLobby();
		if (this.isInParty()) {
			this.setInventoryForParty();
		} else {
			this.setInventoryForLobby();
		}
	}

	public void spectate(Profile p) {

		if (p.equals(this)) {
			message(ErrorMessages.NOT_SPEC_SELF);
			return;
		}

		if (p.getPlayerStatus() == PlayerStatus.IN_EVENT) {
			spectateEvent(p.getEvent());
			return;
		}

		if (p.getPlayerStatus() != PlayerStatus.FIGHTING) {
			return;
		}

		if (getPlayerStatus() != PlayerStatus.IN_LOBBY && getPlayerStatus() != PlayerStatus.FOLLOWING) {
			message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		Match match = p.getMatch();
		match.getSpectators().add(this);
		spectatingMatch = match;

		this.player.setGameMode(GameMode.SPECTATOR);

		teleport(p);
		ChatMessages.SPECTATING.clone().replace("%player%", p.getName()).send(getPlayer());

		ChatMessages.STOP_SPECTATING.send(getPlayer());

		ChatMessage broadcastedMessage = ChatMessages.SPECTATING_YOUR_MATCH.clone().replace("%player%", getName());
		ProfileManager.broadcast(match.getParticipants(), broadcastedMessage);

		this.setInventoryForSpectating();

		if (getPlayerStatus() == PlayerStatus.FOLLOWING) {
			return;
		}

		setPlayerStatus(PlayerStatus.SPECTATING);
	}

	public void spectateEvent(Event event) {

		if (event.isEnded()) {
			return;
		}

		if (getPlayerStatus() != PlayerStatus.IN_LOBBY && getPlayerStatus() != PlayerStatus.FOLLOWING) {
			message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		spectatingTournament = event;
		event.getSpectators().add(this);

		this.player.setGameMode(GameMode.SPECTATOR);

		teleport(event.getEventArena().getWaitingLocation());
		ChatMessages.SPECTATING_EVENT.send(getPlayer());
		ChatMessages.STOP_SPECTATING.send(getPlayer());

		this.setInventoryForSpectating();

		if (getPlayerStatus() == PlayerStatus.FOLLOWING) {
			return;
		}

		setPlayerStatus(PlayerStatus.SPECTATING);
	}

	public void teleport(Location loc) {
		this.player.getHandle().playerConnection.checkMovement = false;
		this.player.teleport(loc);
	}

	public void teleportToLobby() {
		teleport(ProfileManager.getSpawnLocation());

		if (playerStatus != PlayerStatus.FOLLOWING) {
			setPlayerStatus(PlayerStatus.IN_LOBBY);
		}
	}

	public void sendDuelRequest(Profile player) {
		getPlayer().closeInventory();

		if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			message(ErrorMessages.PLAYER_NOT_IN_LOBBY);
			return;
		}

		if (!player.isRequests()) {
			message(ErrorMessages.DUEL_REQUESTS_DISABLED);
			return;
		}

		for (DuelRequest d : player.getRecievedDuelRequests()) {
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
		player.getRecievedDuelRequests().add(request);
		removeFromQueue();
		ChatMessages.DUEL_REQUEST_SENT.clone().replace("%player%", player.getName()).send(getPlayer());

		HoverEvent DATA = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(matchData.toString()).create());

		ChatMessages.DUEL_REQUEST_RECIEVED.clone().replace("%player%", sender)
				.setTextEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + getName()),
						DATA)
				.send(player.getPlayer());
	}

	public void leaveKitEditor() {
		setInventoryClickCancelled(true);
		teleportToLobby();
		this.setInventoryForLobby();
	}

	public void leaveKitCreator() {
		this.player.setGameMode(GameMode.SURVIVAL);
		leaveKitEditor();
		openMenu(new MechanicsMenu(previousSubmitAction));
	}

	public void saveKit() {
		if (kitEditorData == null) {
			return;
		}

		kitEditorData.saveKit(this);
	}

	public void sendPlayerToKitEditor(QueueEntry qe) {
		getPlayer().closeInventory();

		Location location = KitEditorManager.getLocation();

		if (location == null) {
			message(ErrorMessages.KIT_EDITOR_LOCATION_NOT_SET);
			return;
		}

		teleport(location);
		setInventoryClickCancelled(false);
		getInventory().clear();
		setPlayerStatus(PlayerStatus.KIT_EDITOR);
		kitEditorData = qe;
		getInventory().setContents(qe.getGametype().getKit().getContents());
	}

	public void saveCreatedKit() {
		matchData.setKit(new Kit(getInventory().getContents(), getInventory().getArmorContents()));
		getPlayer().closeInventory();
		ChatMessages.KIT_SAVED.send(getPlayer());
	}

	public void sendPlayerToKitCreator() {
		getPlayer().closeInventory();

		Location location = KitEditorManager.getLocation();

		if (location == null) {
			message(ErrorMessages.KIT_EDITOR_LOCATION_NOT_SET);
			return;
		}

		teleport(location);
		setInventoryClickCancelled(false);
		getInventory().clear();
		setPlayerStatus(PlayerStatus.KIT_CREATOR);
		this.player.setGameMode(GameMode.CREATIVE);
	}

	public boolean equals(Profile p) {
		return p.getUUID().equals(player.getUniqueId());
	}

	public Profile getOpponent() {
		return getMatch().getOpponent(this);
	}

	public ItemStack getItemInHand() {
		return getInventory().getItemInHand();
	}

	public UUID getUUID() {
		return player.getUniqueId();
	}

	public void setPlayerStatus(PlayerStatus s) {
		PlayerStatusChangeEvent psce = new PlayerStatusChangeEvent(this, s);
		Bukkit.getServer().getPluginManager().callEvent(psce);

		if (psce.cancelled()) {
			return;
		}

		playerStatus = s;
	}

	public void follow(Profile p) {

		if (getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		setPlayerStatus(PlayerStatus.FOLLOWING);
		following = p;
		p.getFollowers().add(this);
		this.setInventoryToFollow();
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

	public void teleport(Profile playerarg) {
		teleport(playerarg.getPlayer().getLocation());
	}

	public void startPartyOpenCooldown() {
		if (partyOpenCooldown) {
			return;
		}

		partyOpenCooldown = true;

		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PracticePlugin.INSTANCE,
				new Runnable() {
					public void run() {
						partyOpenCooldown = false;
					}
				}, 400);
	}

	public void setTournament(Tournament tournament) {
		setPlayerStatus(PlayerStatus.IN_TOURAMENT);
		this.setInventoryForTournament();
		this.tournament = tournament;
	}

	public void setEvent(Event event) {
		setPlayerStatus(PlayerStatus.IN_EVENT);
		this.setInventoryForTournament();
		this.event = event;
	}

	public void removeFromTournament() {

		teleportToLobby();
		this.setInventoryForLobby();

		if (tournament == null) {
			return;
		}

		tournament.removePlayer(this);

		tournament = null;
	}

	public void removeFromEvent() {

		teleportToLobby();
		this.setInventoryForLobby();

		if (tournament == null) {
			return;
		}

		event.removePlayer(this);

		event = null;
	}
}
