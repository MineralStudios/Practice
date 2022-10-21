package gg.mineral.practice.entity;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
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

import gg.mineral.api.inventory.MineralInventory;
import gg.mineral.api.inventory.MineralPlayerInventory;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.event.PlayerStatusChangeEvent;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.inventory.menus.MechanicsMenu;
import gg.mineral.practice.inventory.menus.SelectGametypeMenu;
import gg.mineral.practice.inventory.menus.SelectModeMenu;
import gg.mineral.practice.inventory.menus.SelectQueuetypeMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.managers.PlayerSettingsManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.match.DuelRequest;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.scoreboard.Scoreboard;

import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.AutoExpireList;
import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.PearlCooldown;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.Message;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Profile {
	final CraftPlayer player;
	Match match, spectatingMatch;
	Scoreboard b;
	MatchData matchData;
	Integer hits = 0, longestCombo = 0;
	Boolean playersVisible = true, partyOpenCooldown = false, inMatchCountdown = false,
			requests = true;
	Profile following, duelReciever;
	GlueList<Profile> followers = new ProfileList();
	AutoExpireList<DuelRequest> recievedDuelRequests = new AutoExpireList<>();
	AutoExpireList<Party> recievedPartyRequests = new AutoExpireList<>();
	PlayerStatus status = PlayerStatus.IN_LOBBY;
	Party party;
	QueueEntry kitEditorData;
	SubmitAction prevSubmitAction;
	Tournament tournament, spectatingTournament;
	PearlCooldown pearlCooldown = new PearlCooldown(this);
	MineralPlayerInventory playerInventory;

	public Profile(org.bukkit.entity.Player player) {
		this.player = (CraftPlayer) player;
		this.playerInventory = MineralPlayerInventory.create(player);
		this.matchData = new MatchData();
		pearlCooldown.start();
	}

	public void setPreviousSubmitAction(SubmitAction submitAction) {
		this.prevSubmitAction = submitAction;
	}

	public CraftPlayer bukkit() {
		return player;
	}

	public Integer getHitCount() {
		return hits;
	}

	public PearlCooldown getPearlCooldown() {
		return pearlCooldown;
	}

	public boolean getPlayersVisible() {
		return playersVisible;
	}

	public void setPlayersVisible(boolean playersVisible) {
		if (getPlayerStatus() != PlayerStatus.IN_LOBBY || getPlayerStatus() != PlayerStatus.IN_QUEUE) {
			return;
		}

		this.playersVisible = playersVisible;
	}

	public boolean getRequests() {
		return requests;
	}

	public AutoExpireList<DuelRequest> getRecievedDuelRequests() {
		return recievedDuelRequests;
	}

	public AutoExpireList<Party> getRecievedPartyRequests() {
		return recievedPartyRequests;
	}

	public void setRequests(Boolean duelRequests) {
		this.requests = duelRequests;
	}

	public Scoreboard getBoard() {
		return b;
	}

	public void removeScoreboard() {
		b = null;
	}

	public void increaseHitCount() {
		hits++;
		longestCombo++;
	}

	public void resetLongestCombo() {
		longestCombo = 0;
	}

	public Integer getLongestCombo() {
		return longestCombo;
	}

	public void clearHitCount() {
		hits = 0;
		longestCombo = 0;
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

	public Match getMatch() {
		return match;
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

		if (status != PlayerStatus.IN_LOBBY) {
			return;
		}

		setInventoryForLobby();
	}

	public Party getParty() {
		return party;
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
		m.send(this.bukkit());
	}

	public String getName() {
		return player.getName();
	}

	public void setInventoryToFollow() {
		getInventory().clear();
		getInventory().set(0, 0, ItemStacks.STOP_FOLLOWING, interaction -> {
			this.stopSpectatingAndFollowing();
			return true;
		});

		bukkit().updateInventory();
	}

	public void setInventoryForTournament() {

		getInventory().clear();
		getInventory().setItem(0, ItemStacks.WAIT_TO_LEAVE,
				(Runnable) () -> this.message(ErrorMessages.CAN_NOT_LEAVE_YET));

		Profile pl = this;

		new BukkitRunnable() {
			@Override
			public void run() {
				getInventory().set(0, 0, ItemStacks.LEAVE_TOURNAMENT, (Runnable) pl::removeFromTournament);
			}
		}.runTaskLater(PracticePlugin.INSTANCE, 20);
		bukkit().updateInventory();
	}

	public void setInventoryForParty() {

		getInventory().clear();
		getInventory().setItem(0, ItemStacks.WAIT_TO_LEAVE,
				(Runnable) () -> this.message(ErrorMessages.CAN_NOT_LEAVE_YET));

		new BukkitRunnable() {
			@Override
			public void run() {
				getInventory().setItem(0, ItemStacks.LEAVE_PARTY, new CommandTask("p leave"));
			}
		}.runTaskLater(PracticePlugin.INSTANCE, 20);

		getInventory().setItem(1, ItemStacks.LIST_PLAYERS, new CommandTask("p list"));
		getInventory().setItem(4, ItemStacks.DUEL, new CommandTask("duel"));
		getInventory().setItem(5, ItemStacks.PARTY_SPLIT, new MenuTask(new SelectModeMenu(SubmitAction.P_SPLIT)));
		getInventory().setItem(3, ItemStacks.OPEN_PARTY, new CommandTask("p open"));
		bukkit().updateInventory();
	}

	public void setInventoryForLobby() {

		getInventory().clear();

		List<Queuetype> list = QueuetypeManager.list();

		for (int i = 0; i < list.size(); i++) {
			Queuetype q = list.get(i);
			try {
				ItemStack item = new ItemBuilder(q.getDisplayItem())
						.name(CC.SECONDARY + CC.B + q.getDisplayName()).build();
				getInventory().setItem(q.getSlotNumber(), item,
						new MenuTask(new SelectGametypeMenu(q, true, false)));
			} catch (NullPointerException e) {
				continue;
			}
		}

		if (KitEditorManager.getEnabled()) {
			ItemStack editor = new ItemBuilder(KitEditorManager.getDisplayItem())
					.name(CC.SECONDARY + CC.B + KitEditorManager.getDisplayName())
					.build();
			getInventory().setItem(KitEditorManager.getSlot(), editor,
					new MenuTask(new SelectQueuetypeMenu()));
		}

		if (PartyManager.getEnabled()) {
			ItemStack parties = new ItemBuilder(PartyManager.getDisplayItem())
					.name(CC.SECONDARY + CC.B + PartyManager.getDisplayName())
					.build();
			getInventory().setItem(PartyManager.getSlot(), parties,
					new CommandTask("p create"));
		}

		if (PlayerSettingsManager.getEnabled()) {
			ItemStack settings = new ItemBuilder(PlayerSettingsManager.getDisplayItem())
					.name(CC.SECONDARY + CC.B + PlayerSettingsManager.getDisplayName())
					.build();
			getInventory().setItem(PlayerSettingsManager.getSlot(), settings,
					new CommandTask("settings"));
		}
		bukkit().updateInventory();
	}

	public void setInventoryForQueue() {
		getInventory().clear();

		getInventory().setItem(0, ItemStacks.LEAVE_QUEUE,
				new RunnableTask(this::removeFromQueue, this::setInventoryForLobby));
		bukkit().updateInventory();
	}

	public void setInventoryForSpectating() {
		getInventory().clear();
		getInventory().setItem(0, ItemStacks.STOP_SPECTATING, (Runnable) this::stopSpectatingAndFollowing);
		bukkit().updateInventory();
	}

	public void removeFromQueue() {
		QueueSearchTask.removePlayer(this);
		setPlayerStatus(PlayerStatus.IN_LOBBY);
	}

	public void addPlayerToQueue(QueueEntry qd) throws SQLException {
		this.player.getOpenInventory().close();

		if (status != PlayerStatus.IN_LOBBY) {
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

		if (status != PlayerStatus.FOLLOWING) {
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

		if (status != PlayerStatus.SPECTATING && status != PlayerStatus.FOLLOWING) {
			message(ErrorMessages.NOT_SPEC_OR_FOLLOWING);
			return;
		}

		if (spectatingMatch != null) {
			spectatingMatch.getSpectators().remove(this);
			spectatingMatch = null;
		}

		if (status == PlayerStatus.FOLLOWING) {
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

	public Match getSpectatingMatch() {
		return spectatingMatch;
	}

	public void spectate(Profile p) {

		if (p.equals(this)) {
			message(ErrorMessages.NOT_SPEC_SELF);
			return;
		}

		if (p.getPlayerStatus() == PlayerStatus.IN_TOURAMENT) {
			spectateTournament(p.getTournament());
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
		match.addSpectator(this);
		spectatingMatch = match;

		this.player.setGameMode(GameMode.SPECTATOR);

		teleport(p);
		ChatMessages.SPECTATING.clone().replace("%player%", p.getName()).send(bukkit());

		ChatMessages.STOP_SPECTATING.send(bukkit());

		ChatMessage broadcastedMessage = ChatMessages.SPECTATING_YOUR_MATCH.clone().replace("%player%", getName());
		PlayerManager.broadcast(match.getParticipants(), broadcastedMessage);

		this.setInventoryForSpectating();

		if (getPlayerStatus() == PlayerStatus.FOLLOWING) {
			return;
		}

		setPlayerStatus(PlayerStatus.SPECTATING);
	}

	public void spectateTournament(Tournament t) {

		if (t.isEnded() || !t.isEvent()) {
			return;
		}

		if (getPlayerStatus() != PlayerStatus.IN_LOBBY && getPlayerStatus() != PlayerStatus.FOLLOWING) {
			message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		spectatingTournament = t;
		t.addSpectator(this);

		this.player.setGameMode(GameMode.SPECTATOR);

		teleport(t.getEventArena().getWaitingLocation());
		ChatMessages.SPECTATING_TOURNAMENT.send(bukkit());
		ChatMessages.STOP_SPECTATING.send(bukkit());

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
		teleport(PlayerManager.getSpawnLocation());

		if (status != PlayerStatus.FOLLOWING) {
			setPlayerStatus(PlayerStatus.IN_LOBBY);
		}

		try {
			bukkit().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		} catch (Exception e) {

		}
	}

	public void sendDuelRequest(Profile player) {
		bukkit().closeInventory();

		if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			message(ErrorMessages.PLAYER_NOT_IN_LOBBY);
			return;
		}

		if (!player.getRequests()) {
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
		ChatMessages.DUEL_REQUEST_SENT.clone().replace("%player%", player.getName()).send(bukkit());

		HoverEvent DATA = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(matchData.toString()).create());

		ChatMessages.DUEL_REQUEST_RECIEVED.clone().replace("%player%", sender)
				.setTextEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + getName()),
						DATA)
				.send(player.bukkit());
	}

	public void leaveKitEditor() {

		teleportToLobby();
		this.setInventoryForLobby();
	}

	public void leaveKitCreator() {
		this.player.setGameMode(GameMode.SURVIVAL);
		leaveKitEditor();
		openMenu(new MechanicsMenu(prevSubmitAction));
	}

	public void saveKit() {
		if (kitEditorData == null) {
			return;
		}

		kitEditorData.saveKit(this);
	}

	public void sendPlayerToKitEditor(QueueEntry qe) {
		bukkit().closeInventory();

		Location location = KitEditorManager.getLocation();

		if (location == null) {
			message(ErrorMessages.KIT_EDITOR_LOCATION_NOT_SET);
			return;
		}

		teleport(location);

		getInventory().clear();
		setPlayerStatus(PlayerStatus.KIT_EDITOR);
		kitEditorData = qe;
		getInventory().setContents(qe.getGametype().getKit().getContents());
	}

	public void saveCreatedKit() {
		ItemStack[] cont = getInventory().getContents();
		ItemStack[] armcont = getInventory().getArmorContents();
		Kit k = new Kit(cont, armcont);
		matchData.setKit(k, "Custom");
		bukkit().closeInventory();
		ChatMessages.KIT_SAVED.send(bukkit());
	}

	public void sendPlayerToKitCreator() {
		bukkit().closeInventory();

		Location location = KitEditorManager.getLocation();

		if (location == null) {
			message(ErrorMessages.KIT_EDITOR_LOCATION_NOT_SET);
			return;
		}

		teleport(location);
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
		return bukkit().getInventory().getItemInHand();
	}

	public MineralPlayerInventory getInventory() {
		return playerInventory;
	}

	public UUID getUUID() {
		return player.getUniqueId();
	}

	public PlayerStatus getPlayerStatus() {
		return status;
	}

	public void setPlayerStatus(PlayerStatus s) {
		PlayerStatusChangeEvent psce = new PlayerStatusChangeEvent(this, s);
		Bukkit.getServer().getPluginManager().callEvent(psce);

		if (psce.cancelled()) {
			return;
		}

		status = s;
	}

	public QueueEntry getKitEditorData() {
		return kitEditorData;
	}

	public MatchData getMatchData() {
		return matchData;
	}

	public Profile getDuelReciever() {
		return duelReciever;
	}

	public void setDuelReciever(Profile p) {
		duelReciever = p;
	}

	public GlueList<Profile> getFollowers() {
		return followers;
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

	public boolean isInMatchCountdown() {
		return inMatchCountdown;
	}

	public void setPearlCooldown(int i) {
		pearlCooldown.setTimeRemaining(i);
	}

	public void setScoreboard(Scoreboard scoreboard) {
		b = scoreboard;
	}

	ArmorStand matchCountdownEntity;

	public void setInMatchCountdown(boolean c) {
		inMatchCountdown = c;

		if (c) {
			matchCountdownEntity = player.getWorld().spawn(bukkit().getLocation(), ArmorStand.class);
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
		teleport(playerarg.bukkit().getLocation());
	}

	public void startPartyOpenCooldown() {
		if (partyOpenCooldown) {
			return;
		}

		partyOpenCooldown = true;

		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PracticePlugin.INSTANCE,
				() -> {
					partyOpenCooldown = false;
				}, 400);
	}

	public boolean getPartyOpenCooldown() {
		return partyOpenCooldown;
	}

	public void setTournament(Tournament tournament) {
		setPlayerStatus(PlayerStatus.IN_TOURAMENT);
		this.setInventoryForTournament();
		this.tournament = tournament;
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

	public Tournament getTournament() {
		return tournament;
	}
}
