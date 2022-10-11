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
import gg.mineral.core.inventory.PlayerInventory;
import gg.mineral.core.tasks.CommandTask;
import gg.mineral.core.tasks.RunnableTask;
import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.core.utils.message.ChatMessage;
import gg.mineral.core.utils.message.Message;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.event.PlayerStatusChangeEvent;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.inventory.menus.MechanicsMenu;
import gg.mineral.practice.inventory.menus.SelectGametypeMenu;
import gg.mineral.practice.inventory.menus.SelectModeMenu;
import gg.mineral.practice.inventory.menus.SelectQueuetypeMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.match.DuelRequest;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.MatchData;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.tasks.MenuTask;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.AutoExpireList;
import gg.mineral.practice.util.PearlCooldown;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.ChatMessages;
import gg.mineral.practice.util.messages.ErrorMessages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Profile {
	final CraftPlayer player;
	final PlayerInventory inventory;
	final PracticePlugin instance = PracticePlugin.INSTANCE;
	final QueuetypeManager queuetypeManager = instance.getQueuetypeManager();
	final PlayerManager playerManager = instance.getPlayerManager();
	Match match;
	Scoreboard b;
	Match spectatingMatch;
	MatchData matchData;
	Integer hits = 0;
	Boolean playersVisible = true;
	PracticeMenu openMenu;
	Profile following;
	GlueList<Profile> followers = new ProfileList();
	AutoExpireList<DuelRequest> recievedDuelRequests = new AutoExpireList<>();
	AutoExpireList<Party> recievedPartyRequests = new AutoExpireList<>();
	Boolean requests = true;
	Profile duelReciever;
	boolean partyOpenCooldown = false;
	PlayerStatus status = PlayerStatus.IN_LOBBY;
	boolean inMatchCountdown = false;
	Party party;
	QueueEntry kitEditorData;
	SubmitAction prevSubmitAction;
	boolean inventoryClickCancelled = false;
	Tournament tournament;
	PearlCooldown pearlCooldown = new PearlCooldown(this);
	Tournament spectatingTournament;

	public Profile(org.bukkit.entity.Player player) {
		this.player = (CraftPlayer) player;
		this.matchData = new MatchData();
		this.inventory = new PlayerInventory(player.getInventory());
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

	public PracticeMenu getOpenMenu() {
		return openMenu;
	}

	public PearlCooldown getPearlCooldown() {
		return pearlCooldown;
	}

	public void openMenu(PracticeMenu m) {
		m.open(this);
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
	}

	public void clearHitCount() {
		hits = 0;
	}

	public void clearInventory() {
		getInventory().clear();
	}

	public void heal() {
		this.player.setFoodLevel(20);
		this.player.setHealth(20);
		this.player.setSaturation(20);
		this.player.setFireTicks(0);
	}

	public void setMatch(Match match) {
		playerManager.setInMatch(this);
		this.match = match;
		setPlayerStatus(PlayerStatus.FIGHTING);
	}

	public void setOpenMenu(PracticeMenu menu) {
		this.openMenu = menu;
	}

	public Match getMatch() {
		return match;
	}

	public boolean isInParty() {
		return party != null;
	}

	public void removeFromMatch() {
		playerManager.removeFromMatch(this);
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
		setInventoryClickCancelled(true);
		inventory.clear();
		inventory.setItem(0, ItemStacks.STOP_FOLLOWING, (Runnable) this::stopSpectatingAndFollowing);
		bukkit().updateInventory();
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
		bukkit().updateInventory();
	}

	public void setInventoryForParty() {
		setInventoryClickCancelled(true);
		inventory.clear();
		inventory.setItem(0, ItemStacks.WAIT_TO_LEAVE, (Runnable) () -> this.message(ErrorMessages.CAN_NOT_LEAVE_YET));

		new BukkitRunnable() {
			@Override
			public void run() {
				inventory.setItem(0, ItemStacks.LEAVE_PARTY, new CommandTask("p leave"));
			}
		}.runTaskLater(PracticePlugin.INSTANCE, 20);

		inventory.setItem(1, ItemStacks.LIST_PLAYERS, new CommandTask("p list"));
		inventory.setItem(4, ItemStacks.DUEL, new CommandTask("duel"));
		inventory.setItem(5, ItemStacks.PARTY_SPLIT, new MenuTask(new SelectModeMenu(SubmitAction.P_SPLIT)));
		inventory.setItem(3, ItemStacks.OPEN_PARTY, new CommandTask("p open"));
		bukkit().updateInventory();
	}

	public void setInventoryForLobby() {
		setInventoryClickCancelled(true);
		inventory.clear();

		GlueList<Queuetype> list = queuetypeManager.getQueuetypes();

		for (int i = 0; i < list.size(); i++) {
			Queuetype q = list.get(i);
			try {
				ItemStack item = new ItemBuilder(q.getDisplayItem())
						.name(CC.SECONDARY + CC.B + q.getDisplayName()).build();
				inventory.setItem(q.getSlotNumber(), item,
						new MenuTask(new SelectGametypeMenu(q, true, false)));
			} catch (NullPointerException e) {
				continue;
			}
		}

		if (instance.getKitEditorManager().getEnabled()) {
			ItemStack editor = new ItemBuilder(instance.getKitEditorManager().getDisplayItem())
					.name(CC.SECONDARY + CC.B + instance.getKitEditorManager().getDisplayName())
					.build();
			inventory.setItem(instance.getKitEditorManager().getSlot(), editor,
					new MenuTask(new SelectQueuetypeMenu()));
		}

		if (instance.getPartyManager().getEnabled()) {
			ItemStack parties = new ItemBuilder(instance.getPartyManager().getDisplayItem())
					.name(CC.SECONDARY + CC.B + instance.getPartyManager().getDisplayName())
					.build();
			inventory.setItem(instance.getPartyManager().getSlot(), parties, new CommandTask("p create"));
		}

		if (instance.getSettingsManager().getEnabled()) {
			ItemStack settings = new ItemBuilder(instance.getSettingsManager().getDisplayItem())
					.name(CC.SECONDARY + CC.B + instance.getSettingsManager().getDisplayName())
					.build();
			inventory.setItem(instance.getSettingsManager().getSlot(), settings, new CommandTask("settings"));
		}
		bukkit().updateInventory();
	}

	public void setInventoryForQueue() {
		setInventoryClickCancelled(true);
		inventory.clear();

		getInventory().setItem(0, ItemStacks.LEAVE_QUEUE,
				new RunnableTask(this::removeFromQueue, this::setInventoryForLobby));
		bukkit().updateInventory();
	}

	public void setInventoryForSpectating() {
		setInventoryClickCancelled(true);
		inventory.clear();
		getInventory().setItem(0, ItemStacks.STOP_SPECTATING, (Runnable) this::stopSpectatingAndFollowing);
		bukkit().updateInventory();
	}

	public void setInventoryClickCancelled(boolean c) {
		inventoryClickCancelled = c;
	}

	public boolean getInventoryClickCancelled() {
		return inventoryClickCancelled;
	}

	public void removeFromQueue() {
		QueueSearchTask.removePlayer(this);
		setPlayerStatus(PlayerStatus.IN_LOBBY);
	}

	public void addPlayerToQueue(QueueEntry qd) {
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
		playerManager.broadcast(match.getParticipants(), broadcastedMessage);

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
		teleport(playerManager.getSpawnLocation());

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
		setInventoryClickCancelled(true);
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

		Location location = PracticePlugin.INSTANCE.getKitEditorManager().getLocation();

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
		ItemStack[] cont = getInventory().getContents();
		ItemStack[] armcont = getInventory().getArmorContents();
		Kit k = new Kit(cont, armcont);
		matchData.setKit(k, "Custom");
		bukkit().closeInventory();
		ChatMessages.KIT_SAVED.send(bukkit());
	}

	public void sendPlayerToKitCreator() {
		bukkit().closeInventory();

		Location location = PracticePlugin.INSTANCE.getKitEditorManager().getLocation();

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

	public PlayerInventory getInventory() {
		return inventory;
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
