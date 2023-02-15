package gg.mineral.practice.entity;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.handler.RequestHandler;
import gg.mineral.practice.entity.handler.SpectateHandler;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.inventory.PlayerInventory;
import gg.mineral.practice.inventory.PracticeMenu;
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
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.Registry;
import gg.mineral.practice.util.math.PearlCooldown;
import gg.mineral.practice.util.messages.Message;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.world.BlockData;
import lombok.Getter;
import lombok.Setter;

public class Profile {
	@Getter
	final CraftPlayer player;
	@Getter
	final PlayerInventory inventory;
	@Getter
	Match match;
	@Getter
	@Setter
	Scoreboard scoreboard;
	@Getter
	ScoreboardHandler scoreboardHandler;
	Integer scoreboardTaskId, fakeBlockTaskId;
	@Getter
	MatchData matchData;
	@Getter
	SpectateHandler spectateHandler = new SpectateHandler(this);
	@Getter
	RequestHandler requestHandler = new RequestHandler(this);
	@Getter
	MatchStatisticCollector matchStatisticCollector = new MatchStatisticCollector(this);
	@Getter
	boolean playersVisible = true, partyOpenCooldown = false;
	@Setter
	@Getter
	PracticeMenu openMenu;
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
	@Setter
	Profile killer;
	@Getter
	PearlCooldown pearlCooldown = new PearlCooldown(this);
	@Getter
	@Setter
	boolean kitLoaded = false, inMatchCountdown = false;
	@Getter
	Registry<BlockData, String> fakeBlocks = new Registry<>(BlockData::toString);

	public Profile(org.bukkit.entity.Player player) {
		this.player = (CraftPlayer) player;
		this.matchData = new MatchData();
		this.inventory = new PlayerInventory(this);
		this.scoreboardHandler = new ScoreboardHandler(player);

		scoreboardTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (getScoreboard() != null) {
				getScoreboard().updateBoard(scoreboardHandler, this);
			}
		}, 0, 10);

		fakeBlockTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			fakeBlocks.getRegisteredObjects().forEach(blockData -> {
				blockData.update(this.getPlayer());
			});
		}, 0, 3);

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
		Bukkit.getScheduler().cancelTask(scoreboardTaskId);
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
		if (playerStatus == PlayerStatus.FIGHTING && !getMatch().isEnded()) {
			return;
		}

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
		kitLoaded = true;
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

	public void removeFromQueue(QueueEntry queueEntry) {
		if (QueueSearchTask.removePlayer(this, queueEntry)) {
			removeFromQueue();
			player.closeInventory();
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
					.replace("%gametype%", queueEntry.getGametype().getDisplayName()));
			QueueSearchTask.addPlayer(this, queueEntry);
		}
	}

	public void teleportToLobby() {
		if (playerStatus == PlayerStatus.FIGHTING && !getMatch().isEnded()) {
			return;
		}

		PlayerUtil.teleport(this.getPlayer(), ProfileManager.getSpawnLocation());
		updateVisiblity();

		if (playerStatus != PlayerStatus.FOLLOWING && playerStatus != PlayerStatus.QUEUEING) {
			setPlayerStatus(PlayerStatus.IDLE);
		}
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
		return p.getUUID().equals(player.getUniqueId());
	}

	public UUID getUUID() {
		return player.getUniqueId();
	}

	public void setPlayerStatus(PlayerStatus newPlayerStatus) {
		playerStatus = newPlayerStatus;
	}

	private void updateVisiblity() {
		List<org.bukkit.entity.Player> playersInWorld = getPlayer().getWorld().getPlayers();
		if (!this.isPlayersVisible()) {
			for (Player player : playersInWorld) {
				getPlayer().hidePlayer(player, false);
			}
			return;
		}

		for (Player player : playersInWorld) {
			getPlayer().showPlayer(player);
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
		return this.event != null;
	}

	public void removeFromTournament() {

		teleportToLobby();
		getInventory().setInventoryForLobby();

		if (!isInTournament()) {
			return;
		}

		tournament.removePlayer(this);

		tournament = null;
	}

	public void removeFromEvent() {

		teleportToLobby();
		getInventory().setInventoryForLobby();

		if (!isInEvent()) {
			return;
		}

		event.removePlayer(this);

		event = null;
	}
}
