package gg.mineral.practice.entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.duel.DuelSettings;
import gg.mineral.practice.entity.handler.RequestHandler;
import gg.mineral.practice.entity.handler.SpectateHandler;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.Menu;
import gg.mineral.practice.inventory.PlayerInventory;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.inventory.menus.MechanicsMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.kit.KitCreator;
import gg.mineral.practice.kit.KitEditor;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.QueueSystem;
import gg.mineral.practice.queue.QueuedEntity;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.Registry;
import gg.mineral.practice.util.messages.Message;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.world.BlockData;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.WorldServer;

@Getter
public class Profile extends ProfileData implements QueuedEntity {
	@NonNull
	private final CraftPlayer player;
	private final PlayerInventory inventory;
	private Match match;
	@Setter
	@Nullable
	private Scoreboard scoreboard;
	private ScoreboardHandler scoreboardHandler;
	private int scoreboardTaskId, fakeBlockTaskId;
	private QueueSettings queueSettings;
	private DuelSettings duelSettings;
	private SpectateHandler spectateHandler = new SpectateHandler(this);
	private RequestHandler requestHandler = new RequestHandler(this);
	private boolean playersVisible = true, partyOpenCooldown = false, scoreboardEnabled = true;
	@Setter
	boolean nightMode = false, dead = false;
	@Setter
	private Menu openMenu;
	private PlayerStatus playerStatus = PlayerStatus.IDLE;
	private Party party;
	private KitEditor kitEditor;
	private KitCreator kitCreator;
	private Tournament tournament;
	private Event event;
	@Setter
	private Profile killer;
	@Setter
	private boolean kitLoaded = false, inMatchCountdown = false;
	@Setter
	private int ridingEntityID = -1;
	private Registry<BlockData, String> fakeBlocks = new Registry<>(BlockData::toString);
	private final Short2ObjectOpenHashMap<Int2ObjectOpenHashMap<ItemStack[]>> customKits = new Short2ObjectOpenHashMap<>();

	@Getter
	private final Set<UUID> visiblePlayers = new ObjectOpenHashSet<>();

	@Getter
	private final Set<UUID> visiblePlayersOnTab = new ObjectOpenHashSet<>();

	@Getter
	private final Set<UUID> setVisiblePlayers = new ObjectOpenHashSet<>();

	@Getter
	private final Set<UUID> setVisiblePlayersOnTab = new ObjectOpenHashSet<>();

	public Profile(org.bukkit.entity.Player player) {
		super(player.getUniqueId(), player.getName());
		this.player = (CraftPlayer) player;
		this.queueSettings = new QueueSettings();
		this.duelSettings = new DuelSettings();
		this.inventory = new PlayerInventory(this);
		this.scoreboardHandler = new ScoreboardHandler(player);

		scoreboardTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (getScoreboard() != null && scoreboardHandler != null)
				getScoreboard().updateBoard(scoreboardHandler, this);
		}, 0, 10);

		fakeBlockTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE,
				() -> fakeBlocks.getRegisteredObjects().forEach(blockData -> blockData.update(this.getPlayer())), 0, 3);
	}

	private ItemStack[] getCustomKit(Gametype gametype, ConfigurationSection cs) {

		val kit = gametype.getKit().getContents().clone();

		for (val key : cs.getKeys(false)) {
			val o = cs.get(key);

			val index = Integer.valueOf(key);

			if (o == null) {
				kit[index] = null;
				continue;
			}

			if (o instanceof ItemStack) {
				kit[index] = (ItemStack) o;
				continue;
			}

			kit[index] = null;
		}

		return kit;
	}

	public Int2ObjectOpenHashMap<ItemStack[]> getCustomKits(Queuetype queuetype, Gametype gametype) {
		val hash = (short) (queuetype.getId() << 8 | gametype.getId());
		return getCustomKits(queuetype, gametype, hash);
	}

	public Int2ObjectOpenHashMap<ItemStack[]> getCustomKits(Queuetype queuetype, Gametype gametype, short hash) {
		var kitLoadouts = customKits.get(hash);

		if (kitLoadouts != null)
			return kitLoadouts;

		val cs = ProfileManager.getPlayerConfig()
				.getConfigurationSection(getName() + ".KitData."
						+ gametype.getName() + "." + queuetype.getName());

		if (cs == null)
			return null;

		kitLoadouts = new Int2ObjectOpenHashMap<>();

		for (val key : cs.getKeys(false)) {
			val cs1 = ProfileManager.getPlayerConfig()
					.getConfigurationSection(getName() + ".KitData."
							+ gametype.getName() + "." + queuetype.getName() + "." + key);

			if (cs1 == null)
				continue;

			kitLoadouts.put((int) Integer.valueOf(key), getCustomKit(gametype, cs1));
		}

		getCustomKits().put(hash, kitLoadouts);

		return kitLoadouts;
	}

	public void openMenu(Menu m) {
		m.open(this);
	}

	public void setPlayersVisible(boolean playersVisible) {
		if (getPlayerStatus() != PlayerStatus.IDLE && getPlayerStatus() != PlayerStatus.QUEUEING)
			return;

		this.playersVisible = playersVisible;
	}

	public void removeScoreboard() {
		Bukkit.getScheduler().cancelTask(scoreboardTaskId);
		scoreboard = null;
		scoreboardHandler.delete();
	}

	public void disableScoreboard() {
		scoreboard = null;
		scoreboardHandler.delete();
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

		if (isInParty())
			party.remove(this);

		this.party = null;

		if (playerStatus != PlayerStatus.IDLE)
			return;

		getInventory().setInventoryForLobby();
	}

	public void giveKit(Kit kit) {
		getInventory().setContents(kit.getContents());
		getInventory().setArmorContents(kit.getArmourContents());
		kitLoaded = true;
	}

	public void removePotionEffects() {
		val it = player.getActivePotionEffects().iterator();

		while (it.hasNext()) {
			try {
				val e = it.next();

				if (e == null)
					continue;

				player.removePotionEffect(e.getType());
			} catch (Exception ex) {
				continue;
			}
		}
	}

	public boolean testVisibility(UUID uuid) {

		val match = this.getMatch();
		if (playerStatus == PlayerStatus.FIGHTING
				&& match != null && match.getParticipants().get(uuid) != null)
			return true;

		val spectatable = spectateHandler.getSpectatable();

		if (spectatable != null)
			if ((playerStatus == PlayerStatus.FOLLOWING || playerStatus == PlayerStatus.SPECTATING)
					&& spectatable.getParticipants().get(uuid) != null)
				return true;

		val p = ProfileManager.getProfile(uuid);

		if (this.isPlayersVisible() && p != null)
			if ((playerStatus == PlayerStatus.QUEUEING || playerStatus == PlayerStatus.IDLE)
					&& (p.getPlayerStatus() == PlayerStatus.QUEUEING || p.getPlayerStatus() == PlayerStatus.IDLE)
					&& p.getPlayer().hasPermission("practice.visible"))
				return true;

		return uuid == this.getUuid();
	}

	public boolean testTabVisibility(UUID uuid) {

		val match = this.getMatch();
		if (playerStatus == PlayerStatus.FIGHTING
				&& match != null && match.getParticipants().get(uuid) != null)
			return true;

		val spectatable = spectateHandler.getSpectatable();

		val isBot = BotAPI.INSTANCE.isFakePlayer(uuid);

		if (spectatable != null)
			if ((playerStatus == PlayerStatus.FOLLOWING || playerStatus == PlayerStatus.SPECTATING)
					&& spectatable.getParticipants().get(uuid) != null)
				return true;

		if (!isBot)
			return true;

		return uuid == this.getUuid();
	}

	public void message(Message m) {
		m.send(this.getPlayer());
	}

	public void removeFromQueue() {
		QueueSystem.removePlayerFromQueue(this);
		message(ChatMessages.LEFT_QUEUE);
		setPlayerStatus(PlayerStatus.IDLE);
	}

	public void removeFromQueue(Queuetype queuetype, Gametype gametype) {

		if (!QueueSystem.removePlayerFromQueue(this, queuetype, gametype)) {
			removeFromQueue();
			player.closeInventory();

			if (isInParty())
				getInventory().setInventoryForParty();
			else
				getInventory().setInventoryForLobby();
		}
	}

	public boolean addPlayerToQueue(Queuetype queuetype,
			Gametype gametype) {
		assert queueSettings != null;
		assert queuetype != null;
		assert gametype != null;

		if (queueSettings.isBotQueue() && (!queuetype.isBotsEnabled() || !gametype.isBotsEnabled())) {
			message(ErrorMessages.COMING_SOON);
			return false;
		}

		if (playerStatus == PlayerStatus.IDLE || playerStatus == PlayerStatus.QUEUEING
				|| (playerStatus == PlayerStatus.FIGHTING && getMatch().isEnded())) {
			if (playerStatus != PlayerStatus.QUEUEING) {
				setPlayerStatus(PlayerStatus.QUEUEING);
				getInventory().setInventoryForQueue();
			}

			val message = ChatMessages.JOINED_QUEUE.clone().replace("%queue%", queuetype.getDisplayName())
					.replace("%catagory%",
							gametype.isInCatagory()
									? " " + gametype.getCatagoryName()
									: "")
					.replace("%gametype%", gametype.getDisplayName());

			if (isInParty())
				for (val p : party.getPartyMembers())
					p.message(message);
			else
				message(message);

			val teamSize = queueSettings.getTeamSize();

			return QueueSystem.addPlayerToQueue(isInParty() ? party : this,
					QueueSettings.toEntry(queuetype, gametype, teamSize, queueSettings.isBotQueue(),
							queueSettings.getOpponentDifficulty(),
							queueSettings.getBotTeamSetting(), queueSettings.getEnabledArenas()));
		}

		return false;
	}

	public void teleportToLobby() {
		if (playerStatus == PlayerStatus.FIGHTING && !getMatch().isEnded())
			return;

		PlayerUtil.teleport(this.getPlayer(), ProfileManager.getSpawnLocation());

		if (playerStatus != PlayerStatus.FOLLOWING && playerStatus != PlayerStatus.QUEUEING)
			setPlayerStatus(PlayerStatus.IDLE);
	}

	public void leaveKitEditor() {
		setScoreboard(DefaultScoreboard.INSTANCE);
		getInventory().setInventoryClickCancelled(true);
		this.kitEditor = null;
		teleportToLobby();
		getInventory().setInventoryForLobby();
	}

	public void leaveKitCreator() {
		setScoreboard(DefaultScoreboard.INSTANCE);
		this.player.setGameMode(GameMode.SURVIVAL);
		leaveKitEditor();
		openMenu(new MechanicsMenu(kitCreator.getSubmitAction()));
		this.kitCreator = null;
	}

	public void sendToKitEditor(Queuetype queuetype, Gametype gametype) {

		if (KitEditorManager.getLocation() == null) {
			message(ErrorMessages.KIT_EDITOR_LOCATION_NOT_SET);
			return;
		}

		message(ChatMessages.LEAVE_KIT_EDITOR);

		this.kitEditor = new KitEditor(gametype, queuetype, this);
		kitEditor.start();
	}

	public void sendToKitCreator(SubmitAction submitAction) {

		if (KitEditorManager.getLocation() == null) {
			message(ErrorMessages.KIT_EDITOR_LOCATION_NOT_SET);
			return;
		}

		message(ChatMessages.LEAVE_KIT_CREATOR);

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
		return p.getUuid().equals(player.getUniqueId());
	}

	public void setPlayerStatus(PlayerStatus newPlayerStatus) {
		val canFly = newPlayerStatus.getCanFly().apply(this);

		this.getPlayer().setAllowFlight(canFly);
		this.getPlayer().setFlying(canFly);

		this.playerStatus = newPlayerStatus;

		this.updateVisiblity();
	}

	public void setGameMode(GameMode gameMode) {
		this.player.setGameMode(gameMode);

		val canFly = this.playerStatus.getCanFly().apply(this);

		this.getPlayer().setAllowFlight(canFly);
		this.getPlayer().setFlying(canFly);
	}

	public void removeFromTab(UUID uuid) {
		setVisiblePlayersOnTab.remove(uuid);
		if (!visiblePlayersOnTab.contains(uuid) || uuid == this.getUuid())
			return;

		val player = Bukkit.getPlayer(uuid);

		if (player instanceof CraftPlayer craftPlayer)
			this.getPlayer().getHandle().playerConnection.sendPacket(
					new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,
							craftPlayer.getHandle()));
	}

	public void showOnTab(UUID uuid) {
		setVisiblePlayersOnTab.add(uuid);
		if (visiblePlayersOnTab.contains(uuid) || uuid == this.getUuid())
			return;

		val player = Bukkit.getPlayer(uuid);

		if (player instanceof CraftPlayer craftPlayer)
			this.getPlayer().getHandle().playerConnection.sendPacket(
					new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle()));
	}

	public void removeFromView(UUID uuid) {
		setVisiblePlayers.remove(uuid);
		if (!visiblePlayers.contains(uuid) || uuid == this.getUuid())
			return;
		val player = Bukkit.getPlayer(uuid);

		if (player instanceof CraftPlayer craftPlayer
				&& getPlayer().getHandle().getWorld() instanceof WorldServer worldServer) {
			val entry = worldServer.tracker.trackedEntities
					.get(craftPlayer.getHandle().getId());
			if (entry != null && !entry.trackedPlayers.contains(this.getPlayer().getHandle()))
				entry.clear(this.getPlayer().getHandle());
		}
	}

	public void showPlayer(UUID uuid) {
		setVisiblePlayers.add(uuid);
		if (visiblePlayers.contains(uuid) || uuid == this.getUuid())
			return;

		val player = Bukkit.getPlayer(uuid);

		if (player instanceof CraftPlayer craftPlayer
				&& getPlayer().getHandle().getWorld() instanceof WorldServer worldServer) {
			val entry = worldServer.tracker.trackedEntities
					.get(craftPlayer.getHandle().getId());
			if (entry != null && !entry.trackedPlayers.contains(this.getPlayer().getHandle()))
				entry.updatePlayer(this.getPlayer().getHandle());
		}
	}

	public void updateVisiblity() {

		val players = getPlayer().getWorld().getPlayers();

		for (val uuid : getSetVisiblePlayers())
			if (!testVisibility(uuid))
				removeFromView(uuid);

		for (val uuid : getSetVisiblePlayersOnTab())
			if (!testTabVisibility(uuid))
				removeFromTab(uuid);

		for (val player : players) {
			val uuid = player.getUniqueId();
			var removedFromTab = false;

			if (testTabVisibility(uuid))
				showOnTab(uuid);
			else {
				removeFromTab(uuid);
				removedFromTab = true;
			}

			if (!removedFromTab && testVisibility(uuid))
				showPlayer(uuid);
			else
				removeFromView(uuid);

			val profile = ProfileManager.getProfile(uuid);

			if (profile == null)
				continue;

			removedFromTab = false;

			if (profile.testTabVisibility(this.uuid))
				profile.showOnTab(this.uuid);
			else {
				profile.removeFromTab(this.uuid);
				removedFromTab = true;
			}

			if (!removedFromTab && profile.testVisibility(this.uuid))
				profile.showPlayer(this.uuid);
			else
				profile.removeFromView(this.uuid);
		}
	}

	public void resetQueueSettings() {
		queueSettings = new QueueSettings();
	}

	public void resetDuelSettings() {
		duelSettings = new DuelSettings();
	}

	public void startPartyOpenCooldown() {
		partyOpenCooldown = true;

		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PracticePlugin.INSTANCE,
				() -> partyOpenCooldown = false, 400);
	}

	public void setTournament(Tournament tournament) {
		if (tournament != null)
			getInventory().setInventoryForTournament();
		this.tournament = tournament;
	}

	public void setEvent(Event event) {
		if (event != null)
			getInventory().setInventoryForEvent();
		this.event = event;
	}

	public boolean isInTournament() {
		return this.tournament != null;
	}

	public boolean isInEvent() {
		return this.event != null;
	}

	public void removeFromTournament() {

		teleportToLobby();
		getInventory().setInventoryForLobby();

		if (!isInTournament())
			return;

		tournament.removePlayer(this);

		tournament = null;
	}

	public void removeFromEvent() {

		teleportToLobby();
		getInventory().setInventoryForLobby();

		if (!isInEvent())
			return;

		event.removePlayer(this);

		event = null;
	}

	public void setScoreboardEnabled(boolean enabled) {
		this.scoreboardEnabled = enabled;

		if (enabled) {
			this.scoreboardHandler = new ScoreboardHandler(player);
			setScoreboard(new DefaultScoreboard());
		} else
			disableScoreboard();
	}

	@Override
	public Queue<Profile> getProfiles() {
		return new LinkedList<>(Collections.singletonList(this));
	}
}
