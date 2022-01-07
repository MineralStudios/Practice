package ms.uk.eclipse.entity;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import land.strafe.api.collection.GlueList;
import land.strafe.api.config.FileConfiguration;
import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.inventory.PlayerInventory;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.core.tasks.RunnableTask;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.JoinMessage;
import ms.uk.eclipse.core.utils.message.Message;
import ms.uk.eclipse.core.utils.message.RequestMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.event.PlayerStatusChangeEvent;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.inventory.SubmitAction;
import ms.uk.eclipse.inventory.menus.MechanicsMenu;
import ms.uk.eclipse.inventory.menus.SelectGametypeMenu;
import ms.uk.eclipse.inventory.menus.SelectModeMenu;
import ms.uk.eclipse.inventory.menus.SelectQueuetypeMenu;
import ms.uk.eclipse.kit.Kit;
import ms.uk.eclipse.managers.EloManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueueEntryManager;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.match.DuelRequest;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.match.MatchData;
import ms.uk.eclipse.party.Party;
import ms.uk.eclipse.party.PartyRequest;
import ms.uk.eclipse.queue.QueueEntry;
import ms.uk.eclipse.queue.QueueSearchTask;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.scoreboard.Scoreboard;
import ms.uk.eclipse.tasks.MenuTask;
import ms.uk.eclipse.tournaments.Tournament;
import ms.uk.eclipse.util.PearlCooldown;
import ms.uk.eclipse.util.ProfileList;
import ms.uk.eclipse.util.messages.ErrorMessages;
import net.md_5.bungee.api.chat.ClickEvent;

public class Profile {
	final CraftPlayer player;
	final UUID uuid;
	final PlayerInventory inventory;
	final PracticePlugin instance = PracticePlugin.INSTANCE;
	final GametypeManager gametypeManager = instance.getGametypeManager();
	final QueuetypeManager queuetypeManager = instance.getQueuetypeManager();
	final PlayerManager playerManager = instance.getPlayerManager();
	final PartyManager partyManager = instance.getPartyManager();
	final EloManager eloManager = PracticePlugin.INSTANCE.getEloManager();
	final QueueEntryManager queueEntryManager = PracticePlugin.INSTANCE.getQueueEntryManager();
	Match match;
	Scoreboard b;
	Match spectatingMatch;
	MatchData matchData;
	Integer hits = 0;
	Boolean playersVisible = true;
	Menu openMenu;
	Profile following;
	GlueList<Profile> followers = new ProfileList();
	GlueList<DuelRequest> recievedDuelRequests = new GlueList<>();
	GlueList<PartyRequest> recievedPartyRequests = new GlueList<>();
	Boolean requests = true;
	Profile duelReciever;
	boolean partyOpenCooldown = false;
	PlayerStatus status = PlayerStatus.IN_LOBBY;
	boolean inMatchCountdown = false;
	Object2ObjectOpenHashMap<QueueEntry, ItemStack[]> customKits = new Object2ObjectOpenHashMap<>();
	Party party;
	QueueEntry kitEditorData;
	SubmitAction prevSubmitAction;
	Object2IntOpenHashMap<Gametype> eloMap = new Object2IntOpenHashMap<>();
	boolean inventoryClickCancelled = false;
	Tournament tournament;
	PearlCooldown pearlCooldown = new PearlCooldown(this);
	Tournament spectatingTournament;

	public Profile(org.bukkit.entity.Player player) {
		this.player = (CraftPlayer) player;
		this.matchData = new MatchData();
		this.uuid = player.getUniqueId();
		this.inventory = new PlayerInventory(player.getInventory());
		pearlCooldown.start();
	}

	public void setPreviousSubmitAction(SubmitAction submitAction) {
		this.prevSubmitAction = submitAction;
	}

	public void saveElo(Gametype g) {
		Integer elo = eloMap.get(g);

		if (elo == null) {
			return;
		}

		eloManager.updateElo(this, g.getName(), elo);
	}

	public CraftPlayer bukkit() {
		return player;
	}

	public Integer getHitCount() {
		return hits;
	}

	public Menu getOpenMenu() {
		return openMenu;
	}

	public PearlCooldown getPearlCooldown() {
		return pearlCooldown;
	}

	public void openMenu(Menu m) {
		m.open(this);
	}

	public ItemStack[] getCustomKit(QueueEntry queueEntry) {
		ItemStack[] kit = customKits.get(queueEntry);

		if (kit == null) {
			ConfigurationSection cs = playerManager.getConfig().getConfigurationSection(getName() + ".KitData."
					+ queueEntry.getGametype().getName() + "." + queueEntry.getQueuetype().getName());

			if (cs == null) {
				return null;
			}

			GlueList<ItemStack> items = new GlueList<>();

			for (String key : cs.getKeys(false)) {
				Object o = cs.get(key);

				if (o == null) {
					items.add(null);
					continue;
				}

				if (o instanceof ItemStack) {
					items.add((ItemStack) o);
					continue;
				}

				items.add(null);
			}

			kit = items.toArray(new ItemStack[0]);
			customKits.put(queueEntry, kit);
		}

		return kit;
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

	public GlueList<DuelRequest> getRecievedDuelRequests() {
		return recievedDuelRequests;
	}

	public GlueList<PartyRequest> getRecievedPartyRequests() {
		return recievedPartyRequests;
	}

	public void setRequests(Boolean duelRequests) {
		this.requests = duelRequests;
	}

	public Integer getElo(Gametype g) {
		Integer elo = eloMap.get(g);

		if (elo == null) {
			elo = eloManager.getEloEntry(g.getName(), player.getUniqueId());
			eloMap.put(g, elo);
		}

		return elo;
	}

	public Scoreboard getBoard() {
		return b;
	}

	public void removeScoreboard() {
		if (b == null) {
			return;
		}

		b = null;
	}

	public void setElo(Integer elo, Gametype g) {
		this.eloMap.put(g, elo);
		saveElo(g);
	}

	public Object2IntOpenHashMap<Gametype> getEloMap() {
		return eloMap;
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

	public void setOpenMenu(Menu menu) {
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
		if (m instanceof JoinMessage) {
			JoinMessage message = (JoinMessage) m;
			message.send(this.bukkit());
			return;
		}

		if (m instanceof RequestMessage) {
			RequestMessage message = (RequestMessage) m;
			message.send(this.bukkit());
			return;
		}

		this.player.sendMessage(m.toString());
	}

	public String getName() {
		return player.getName();
	}

	public void setInventoryToFollow() {
		setInventoryClickCancelled(true);
		inventory.clear();

		ItemStack stop = new ItemBuilder(new ItemStack(351, 1, (short) 1))
				.name(new StrikingMessage("Stop Following", CC.PRIMARY, true).toString()).build();
		inventory.setItem(0, stop, (Runnable) this::stopSpectatingAndFollowing);
		bukkit().updateInventory();
	}

	public void setInventoryForTournament() {
		setInventoryClickCancelled(true);
		inventory.clear();

		ItemStack wait = new ItemBuilder(new ItemStack(351, 1, (short) 14))
				.name(new StrikingMessage("Please wait before leaving", CC.PRIMARY, true).toString())
				.build();
		inventory.setItem(0, wait,
				(Runnable) () -> this.message(new ErrorMessage("You can not leave yet")));

		Profile pl = this;

		new BukkitRunnable() {
			@Override
			public void run() {
				ItemStack stop = new ItemBuilder(new ItemStack(351, 1, (short) 1))
						.name(new StrikingMessage("Leave Tournament", CC.PRIMARY, true).toString()).build();
				inventory.setItem(0, stop, (Runnable) pl::removeFromTournament);
			}
		}.runTaskLater(PracticePlugin.INSTANCE, 20);
		bukkit().updateInventory();
	}

	public void setInventoryForParty() {
		setInventoryClickCancelled(true);
		inventory.clear();

		ItemStack wait = new ItemBuilder(new ItemStack(351, 1, (short) 14))
				.name(new StrikingMessage("Please wait before leaving", CC.PRIMARY, true).toString())
				.build();
		inventory.setItem(0, wait, (Runnable) () -> this.message(new ErrorMessage("You can not leave yet")));

		new BukkitRunnable() {
			@Override
			public void run() {
				ItemStack disband = new ItemBuilder(new ItemStack(351, 1, (short) 1))
						.name(new StrikingMessage("Leave Party", CC.PRIMARY, true).toString()).build();
				inventory.setItem(0, disband, new CommandTask("p leave"));
			}
		}.runTaskLater(PracticePlugin.INSTANCE, 20);

		ItemStack list = new ItemBuilder(Material.PAPER)
				.name(new StrikingMessage("List Players", CC.PRIMARY, true).toString()).build();
		inventory.setItem(1, list, new CommandTask("p list"));
		ItemStack duel = new ItemBuilder(Material.WOOD_AXE)
				.name(new StrikingMessage("Duel", CC.PRIMARY, true).toString()).build();
		inventory.setItem(4, duel, new CommandTask("duel"));
		ItemStack split = new ItemBuilder(Material.GOLD_AXE)
				.name(new StrikingMessage("Party Split", CC.PRIMARY, true).toString()).build();
		inventory.setItem(5, split, new MenuTask(new SelectModeMenu(SubmitAction.P_SPLIT)));
		ItemStack open = new ItemBuilder(Material.SKULL_ITEM)
				.name(new StrikingMessage("Open Party", CC.PRIMARY, true).toString()).build();
		inventory.setItem(3, open, new CommandTask("p open"));
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
						.name(new StrikingMessage(q.getDisplayName(), CC.PRIMARY, true).toString()).build();
				inventory.setItem(q.getSlotNumber(), item,
						new MenuTask(new SelectGametypeMenu(q, true, false)));
			} catch (NullPointerException e) {
				continue;
			}
		}

		if (instance.getKitEditorManager().getEnabled()) {
			ItemStack editor = new ItemBuilder(instance.getKitEditorManager().getDisplayItem())
					.name(new StrikingMessage(instance.getKitEditorManager().getDisplayName(), CC.PRIMARY, true)
							.toString())
					.build();
			inventory.setItem(instance.getKitEditorManager().getSlot(), editor,
					new MenuTask(new SelectQueuetypeMenu()));
		}

		if (instance.getPartyManager().getEnabled()) {
			ItemStack parties = new ItemBuilder(instance.getPartyManager().getDisplayItem())
					.name(new StrikingMessage(instance.getPartyManager().getDisplayName(), CC.PRIMARY, true)
							.toString())
					.build();
			inventory.setItem(instance.getPartyManager().getSlot(), parties, new CommandTask("p create"));
		}

		if (instance.getSettingsManager().getEnabled()) {
			ItemStack settings = new ItemBuilder(instance.getSettingsManager().getDisplayItem())
					.name(new StrikingMessage(instance.getSettingsManager().getDisplayName(), CC.PRIMARY, true)
							.toString())
					.build();
			inventory.setItem(instance.getSettingsManager().getSlot(), settings, new CommandTask("settings"));
		}
		bukkit().updateInventory();
	}

	public void setInventoryForQueue() {
		setInventoryClickCancelled(true);
		inventory.clear();

		ItemStack leaveItem = new ItemBuilder(new ItemStack(351, 1, (short) 1))
				.name(new StrikingMessage("Leave Queue", CC.PRIMARY, true).toString()).build();
		getInventory().setItem(0, leaveItem,
				new RunnableTask(this::removeFromQueue, this::setInventoryForLobby));
		bukkit().updateInventory();
	}

	public void setInventoryForSpectating() {
		setInventoryClickCancelled(true);
		inventory.clear();

		ItemStack leaveItem = new ItemBuilder(new ItemStack(351, 1, (short) 1))
				.name(new StrikingMessage("Stop Spectating", CC.PRIMARY, true).toString()).build();
		getInventory().setItem(0, leaveItem, (Runnable) this::stopSpectatingAndFollowing);
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
			message(new ErrorMessage("You are not spectating or following"));
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
			message(new ErrorMessage("You can not spectate yourself"));
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
		message(new ChatMessage("You are now spectating " + p.getName(), CC.PRIMARY, false).highlightText(CC.ACCENT,
				p.getName()));

		message(new ChatMessage("Type /stopspectating to stop spectating", CC.PRIMARY, false).highlightText(CC.ACCENT,
				"/stopspectating"));

		ChatMessage m = new ChatMessage(getName() + " is now spectating your match", CC.PRIMARY, false)
				.highlightText(CC.ACCENT, getName());

		playerManager.broadcast(match.getParticipants(), m);

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
		message(new ChatMessage("You are now spectating the tournament", CC.PRIMARY, false));

		message(new ChatMessage("Type /stopspectating to stop spectating", CC.PRIMARY, false).highlightText(CC.ACCENT,
				"/stopspectating"));

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
			message(new ErrorMessage("That player is not in the lobby"));
			return;
		}

		if (!player.getRequests()) {
			message(new ErrorMessage("That player has duel requests disabled"));
			return;
		}

		for (DuelRequest d : player.getRecievedDuelRequests()) {
			if (d.getSender().equals(this)) {
				message(new ErrorMessage("You already sent that player a duel request"));
				return;
			}
		}

		DuelRequest request = new DuelRequest(this, matchData);
		player.getRecievedDuelRequests().add(request);
		removeFromQueue();
		message(new ChatMessage("You have sent a duel request", CC.PRIMARY, false).highlightText(CC.ACCENT,
				"duel request"));

		String sender = getName();

		if (isInParty()) {
			if (!getParty().getPartyLeader().equals(this)) {
				message(new ErrorMessage("You are not a party leader"));
				return;
			}

			sender = getName() + "'s party (" + getParty().getPartyMembers().size() + ") ";
		}

		RequestMessage message = new RequestMessage(sender, "duel", "with a " + matchData.getKitName() + " kit",
				new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + getName()));

		player.message(message);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
			public void run() {
				player.getRecievedDuelRequests().remove(request);
			}
		}, 600);
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
		ItemStack[] cont = getInventory().getContents();

		if (kitEditorData == null) {
			return;
		}

		customKits.put(kitEditorData, cont);
		FileConfiguration config = playerManager.getConfig();
		String path = getName() + ".KitData." + kitEditorData.getGametype().getName() + "."
				+ kitEditorData.getQueuetype().getName() + ".";

		for (int f = 0; f < cont.length; f++) {
			ItemStack item = cont[f];

			if (item == null) {
				config.set(path + f, "empty");
				continue;
			}

			config.set(path + f, item);
		}

		config.save();

		bukkit().closeInventory();
		message(new ChatMessage("Your kit has been saved", CC.PRIMARY, false));
	}

	public void sendPlayerToKitEditor(QueueEntry qe) {
		bukkit().closeInventory();

		try {
			teleport(PracticePlugin.INSTANCE.getKitEditorManager().getLocation());
			setInventoryClickCancelled(false);
		} catch (Exception e) {
			message(new ErrorMessage(
					"A kit editor location has not been set, use /kiteditor setlocation to set the location"));
			return;
		}

		PlayerInventory playerinv = getInventory();
		playerinv.clear();
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
		message(new ChatMessage("Your kit has been saved", CC.PRIMARY, false));
	}

	public void sendPlayerToKitCreator() {
		bukkit().closeInventory();

		try {
			teleport(PracticePlugin.INSTANCE.getKitEditorManager().getLocation());
			setInventoryClickCancelled(false);
		} catch (Exception e) {
			message(new ErrorMessage(
					"A kit editor location has not been set, use /kiteditor setlocation to set the location"));
			return;
		}

		PlayerInventory playerinv = getInventory();
		playerinv.clear();
		setPlayerStatus(PlayerStatus.KIT_CREATOR);
		this.player.setGameMode(GameMode.CREATIVE);
	}

	public boolean equals(Profile p) {
		return p.getUUID().equals(uuid);
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
		return uuid;
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

	public void setPartyOpenCooldown(boolean b) {
		partyOpenCooldown = b;
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
