package gg.mineral.practice.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.handler.RequestHandler;
import gg.mineral.practice.entity.handler.SpectateHandler;
import gg.mineral.practice.events.Event;
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
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.match.data.MatchStatisticCollector;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.QueueSearchTask2v2;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.traits.Spectatable;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.Registry;
import gg.mineral.practice.util.math.PearlCooldown;
import gg.mineral.practice.util.messages.Message;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.world.BlockData;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.WorldServer;

@Getter
public class Profile extends ProfileData {
	@NonNull
	private final CraftPlayer player;
	private final PlayerInventory inventory;
	private Match<?> match;
	@Setter
	@Nullable
	private Scoreboard scoreboard;
	private ScoreboardHandler scoreboardHandler;
	private int scoreboardTaskId, fakeBlockTaskId;
	private MatchData matchData;
	private SpectateHandler spectateHandler = new SpectateHandler(this);
	private RequestHandler requestHandler = new RequestHandler(this);
	private MatchStatisticCollector matchStatisticCollector = new MatchStatisticCollector(this);
	private boolean playersVisible = false, partyOpenCooldown = false, scoreboardEnabled = true;
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
	private PearlCooldown pearlCooldown = new PearlCooldown(this);
	@Setter
	private boolean kitLoaded = false, inMatchCountdown = false;
	private Registry<BlockData, String> fakeBlocks = new Registry<>(BlockData::toString);

	@Getter
	private final Set<UUID> visiblePlayers = new ObjectOpenHashSet<>();
	@Getter
	private final Set<UUID> visiblePlayersOnTab = new ObjectOpenHashSet<>();

	public Profile(org.bukkit.entity.Player player) {
		super(player.getUniqueId(), player.getName());
		this.player = (CraftPlayer) player;
		this.matchData = new MatchData();
		this.inventory = new PlayerInventory(this);
		this.scoreboardHandler = new ScoreboardHandler(player);

		scoreboardTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (getScoreboard() != null && scoreboardHandler != null)
				getScoreboard().updateBoard(scoreboardHandler, this);
		}, 0, 10);

		fakeBlockTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE,
				() -> fakeBlocks.getRegisteredObjects().forEach(blockData -> blockData.update(this.getPlayer())), 0, 3);

		pearlCooldown.start();
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

	public void setMatch(Match<?> match) {
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
		Iterator<PotionEffect> it = player.getActivePotionEffects().iterator();

		while (it.hasNext()) {
			try {
				PotionEffect e = it.next();

				if (e == null)
					continue;

				player.removePotionEffect(e.getType());
			} catch (Exception ex) {
				continue;
			}
		}
	}

	public boolean testVisibility(UUID uuid) {
		Profile p = ProfileManager.getProfile(uuid);

		if (playerStatus == PlayerStatus.FIGHTING && p.getPlayerStatus() == PlayerStatus.FIGHTING
				&& match.getParticipants().contains(p))
			return true;

		Spectatable spectatable = spectateHandler.getSpectatable();

		if (spectatable != null) {
			if ((playerStatus == PlayerStatus.FOLLOWING || playerStatus == PlayerStatus.SPECTATING)
					&& p.getPlayerStatus() == PlayerStatus.FIGHTING
					&& spectatable.getParticipants().contains(p))
				return true;
		}

		if (this.isPlayersVisible())
			if ((playerStatus == PlayerStatus.QUEUEING || playerStatus == PlayerStatus.IDLE)
					&& (p.getPlayerStatus() == PlayerStatus.QUEUEING || p.getPlayerStatus() == PlayerStatus.IDLE)
					&& p.getPlayer().hasPermission("practice.visible"))
				return true;

		return uuid == this.getUuid();
	}

	public boolean testTabVisibility(UUID uuid) {
		Profile p = ProfileManager.getProfile(uuid);

		boolean isBot = BotAPI.INSTANCE.isFakePlayer(uuid);

		if (!isBot && playerStatus == PlayerStatus.FIGHTING && p.getPlayerStatus() == PlayerStatus.FIGHTING
				&& match.getParticipants().contains(p))
			return true;

		Spectatable spectatable = spectateHandler.getSpectatable();

		if (spectatable != null) {
			if (!isBot && (playerStatus == PlayerStatus.FOLLOWING || playerStatus == PlayerStatus.SPECTATING)
					&& p.getPlayerStatus() == PlayerStatus.FIGHTING
					&& spectatable.getParticipants().contains(p))
				return true;
		}

		return uuid == this.getUuid();
	}

	public void message(Message m) {
		m.send(this.getPlayer());
	}

	public void removeFromQueue() {
		QueueSearchTask.removePlayer(this);
		QueueSearchTask2v2.removePlayer(this);
		message(ChatMessages.LEFT_QUEUE);
		setPlayerStatus(PlayerStatus.IDLE);
	}

	public void removeFromQueue(QueueEntry queueEntry) {

		if ((getMatchData().isTeam2v2() && QueueSearchTask2v2.removePlayer(this, queueEntry))
				|| QueueSearchTask.removePlayer(this, queueEntry)) {
			removeFromQueue();
			player.closeInventory();

			if (isInParty())
				getInventory().setInventoryForParty();
			else
				getInventory().setInventoryForLobby();

		}
	}

	public void addPlayerToQueue(QueueEntry queueEntry) {
		if (playerStatus == PlayerStatus.IDLE || playerStatus == PlayerStatus.QUEUEING
				|| (playerStatus == PlayerStatus.FIGHTING && getMatch().isEnded())) {
			if (playerStatus != PlayerStatus.QUEUEING) {
				setPlayerStatus(PlayerStatus.QUEUEING);
				getInventory().setInventoryForQueue();
			}

			message(ChatMessages.JOINED_QUEUE.clone().replace("%queue%", queueEntry.getQueuetype().getDisplayName())
					.replace("%catagory%",
							queueEntry.getGametype().isInCatagory() ? " " + queueEntry.getGametype().getCatagoryName()
									: "")
					.replace("%gametype%", queueEntry.getGametype().getDisplayName()));

			if (getMatchData().isTeam2v2()) {
				if (isInParty())
					QueueSearchTask2v2.addParty(this.getParty(), queueEntry);
				else
					QueueSearchTask2v2.addPlayer(this, queueEntry);

				return;
			}

			QueueSearchTask.addPlayer(this, queueEntry);
		}
	}

	public void teleportToLobby() {
		if (playerStatus == PlayerStatus.FIGHTING && !getMatch().isEnded())
			return;

		PlayerUtil.teleport(this.getPlayer(), ProfileManager.getSpawnLocation());
		updateVisiblity();

		if (playerStatus != PlayerStatus.FOLLOWING && playerStatus != PlayerStatus.QUEUEING)
			setPlayerStatus(PlayerStatus.IDLE);

	}

	public void leaveKitEditor() {
		setScoreboard(new DefaultScoreboard());
		getInventory().setInventoryClickCancelled(true);
		this.kitEditor = null;
		teleportToLobby();
		getInventory().setInventoryForLobby();
	}

	public void leaveKitCreator() {
		setScoreboard(new DefaultScoreboard());
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

		message(ChatMessages.LEAVE_KIT_EDITOR);

		this.kitEditor = new KitEditor(queueEntry, this);
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
		boolean canFly = newPlayerStatus.getCanFly().apply(this);

		this.getPlayer().setAllowFlight(canFly);
		this.getPlayer().setFlying(canFly);

		this.updateVisiblity();

		this.playerStatus = newPlayerStatus;
	}

	public void setGameMode(GameMode gameMode) {
		this.player.setGameMode(gameMode);

		boolean canFly = this.playerStatus.getCanFly().apply(this);

		this.getPlayer().setAllowFlight(canFly);
		this.getPlayer().setFlying(canFly);
	}

	public void removeFromTab(UUID uuid) {
		if (!visiblePlayersOnTab.contains(uuid))
			return;
		Profile profile = ProfileManager.getProfile(uuid);
		if (profile == null)
			return;

		this.getPlayer().getHandle().playerConnection.sendPacket(
				new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,
						profile.getPlayer().getHandle()));
	}

	public void showOnTab(UUID uuid) {
		if (visiblePlayersOnTab.contains(uuid))
			return;
		Profile profile = ProfileManager.getProfile(uuid);
		if (profile == null)
			return;

		this.getPlayer().getHandle().playerConnection.sendPacket(
				new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, profile.getPlayer().getHandle()));
	}

	public void removeFromView(UUID uuid) {
		if (!visiblePlayers.contains(uuid))
			return;
		Profile profile = ProfileManager.getProfile(uuid);
		if (profile == null)
			return;
		EntityTrackerEntry entry = ((WorldServer) getPlayer().getHandle().getWorld()).tracker.trackedEntities
				.get(profile.getPlayer().getHandle().getId());
		if (entry != null && !entry.trackedPlayers.contains(this.getPlayer().getHandle()))
			entry.clear(this.getPlayer().getHandle());
	}

	public void showPlayer(UUID uuid) {
		if (visiblePlayers.contains(uuid))
			return;
		Profile profile = ProfileManager.getProfile(uuid);
		if (profile == null)
			return;
		EntityTrackerEntry entry = ((WorldServer) getPlayer().getHandle().getWorld()).tracker.trackedEntities
				.get(profile.getPlayer().getHandle().getId());
		if (entry != null && !entry.trackedPlayers.contains(this.getPlayer().getHandle()))
			entry.updatePlayer(this.getPlayer().getHandle());
	}

	public void updateVisiblity() {

		List<Player> players = getPlayer().getWorld().getPlayers();

		for (UUID uuid : getVisiblePlayers())
			if (!testVisibility(uuid))
				removeFromView(uuid);

		for (UUID uuid : getVisiblePlayersOnTab())
			if (!testTabVisibility(uuid))
				removeFromTab(uuid);

		for (Player player : players) {
			Profile profile = ProfileManager.getProfile(player.getUniqueId());

			if (profile == null)
				continue;
			if (testVisibility(player.getUniqueId()))
				showPlayer(player.getUniqueId());
			else
				removeFromView(player.getUniqueId());

			if (profile.testVisibility(player.getUniqueId()))
				profile.showPlayer(uuid);
			else
				profile.removeFromView(uuid);

			if (testTabVisibility(player.getUniqueId()))
				showOnTab(player.getUniqueId());
			else
				removeFromTab(player.getUniqueId());

			if (profile.testTabVisibility(player.getUniqueId()))
				profile.showOnTab(uuid);
			else
				profile.removeFromTab(uuid);
		}
	}

	public void resetMatchData() {
		matchData = new MatchData();
	}

	public void setPearlCooldown(int i) {
		pearlCooldown.setTimeRemaining(i);
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

	public void setScoreboardEnabled(boolean b) {
		this.scoreboardEnabled = b;

		if (b) {
			this.scoreboardHandler = new ScoreboardHandler(player);
			setScoreboard(new DefaultScoreboard());
		} else
			disableScoreboard();
	}
}
