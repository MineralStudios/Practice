package gg.mineral.practice.match;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitScheduler;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.match.data.MatchStatisticCollector;
import gg.mineral.practice.match.data.QueueMatchData;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.QueueSearchTask2v2;
import gg.mineral.practice.scoreboard.impl.BoxingScoreboard;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.InMatchScoreboard;
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard;
import gg.mineral.practice.traits.Spectatable;
import gg.mineral.practice.util.CoreConnector;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.math.Countdown;
import gg.mineral.practice.util.math.MathUtil;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.Strings;
import gg.mineral.practice.util.messages.impl.TextComponents;
import gg.mineral.practice.util.world.BlockData;
import gg.mineral.practice.util.world.BlockUtil;
import gg.mineral.practice.util.world.WorldUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Match<D extends MatchData> implements Spectatable {

	@Getter
	ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<Profile>();
	@Getter
	ProfileList participants = new ProfileList();
	@Getter
	protected Profile profile1;
	@Getter
	protected Profile profile2;
	@Getter
	boolean ended = false;
	@Getter
	int placedTnt;
	@Getter
	private final D data;
	@Getter
	GlueList<Location> buildLog = new GlueList<>();
	@Getter
	static int postMatchTime = 60;
	@Getter
	Queue<Item> itemRemovalQueue = new ConcurrentLinkedQueue<>();
	org.bukkit.World world = null;

	public Match(Profile profile1, Profile profile2, D matchData) {
		this(matchData);
		this.profile1 = profile1;
		this.profile2 = profile2;
		addParicipants(profile1, profile2);
	}

	public Match(D matchData) {
		this.data = matchData;
	}

	public void prepareForMatch(ProfileList profiles) {
		for (Profile profile : profiles)
			prepareForMatch(profile);
	}

	public Kit getKit(Profile p, int loadoutSlot) {
		ItemStack[] customKit = data instanceof QueueMatchData qData
				? qData.getQueueEntry().getCustomKits(p).get(loadoutSlot)
				: null;
		return getKit(customKit);
	}

	public Kit getKit(ItemStack[] customKit) {
		Kit kit = getKit();

		if (customKit != null)
			kit.setContents(customKit);

		return kit;
	}

	public Kit getKit() {
		return new Kit(data.getKit());
	}

	public void setAttributes(Profile p) {
		p.getMatchStatisticCollector().clearHitCount();
		p.setDead(false);
		p.getPlayer().setMaximumNoDamageTicks(data.getNoDamageTicks());
		p.getPlayer().setKnockback(data.getKnockback());
		p.getInventory().setInventoryClickCancelled(false);
		p.getPlayer().setSaturation(20);
		p.getPlayer().setFoodLevel(20);
	}

	public void setPotionEffects(Profile p) {
		if (!data.isDamage())
			p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 255));

		if (data.isBoxing())
			p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, 1));
	}

	public void setVisibility(Profile p) {
		for (Match<?> match : MatchManager.getMatches())
			match.updateVisiblity(this, p);

		for (Profile participant : participants)
			p.getPlayer().showPlayer(participant.getPlayer());
	}

	public void prepareForMatch(Profile p) {

		QueueSearchTask.removePlayer(p);
		QueueSearchTask2v2.removePlayer(p);

		p.setMatch(this);
		p.getRequestHandler().getRecievedDuelRequests().clear();
		p.getMatchStatisticCollector().start();
		p.setKitLoaded(false);

		if (CoreConnector.connected()) {
			if (this instanceof PartyMatch || this instanceof TeamMatch) {
				// CoreConnector.INSTANCE.getNameTagAPI().clearTagOnMatchStart(p.getPlayer(),
				// p.getPlayer());
			} else {
				// CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(p.getPlayer(),
				// p.getPlayer());
			}
		}

		giveLoadoutSelection(p);
		setAttributes(p);
		setPotionEffects(p);
		setVisibility(p);
		// setDisplayNames(p);
		setScoreboard(p);
		handleFollowers(p);
	}

	public void encapsulate(Profile profile) {

		BlockData blockData = new BlockData(profile.getPlayer().getLocation(), Material.BEDROCK,
				(byte) 0);

		int radius = 1;

		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (x == -radius || x == radius || z == -radius || z == radius)
					for (int i = 0; i <= 2; i++)
						BlockUtil.sendFakeBlock(profile,
								blockData.clone().setType(Material.IRON_FENCE).translate(x, i, z));

				BlockUtil.sendFakeBlock(profile, blockData.clone().translate(x, -1, z));
				BlockUtil.sendFakeBlock(profile, blockData.clone().translate(x, 3, z));
			}
		}
	}

	public void giveLoadoutSelection(Profile p) {

		Int2ObjectOpenHashMap<ItemStack[]> map = data instanceof QueueMatchData qData
				? qData.getQueueEntry().getCustomKits(p)
				: null;

		p.getInventory().clear();

		if (map == null ? true : map.isEmpty())
			return;

		if (map.size() == 1) {
			p.giveKit(getKit(map.values().iterator().next()));
			return;
		}

		for (Entry<ItemStack[]> entry : map.int2ObjectEntrySet())
			p.getInventory().setItem(entry.getIntKey(),
					ItemStacks.LOAD_KIT.name(CC.B + CC.GOLD + "Load Kit #" + entry.getIntKey()).build(), profile -> {
						p.giveKit(getKit(entry.getValue()));
						return true;
					});
	}

	public void onCountdownStart(Profile p) {
		encapsulate(p);
	}

	public void onMatchStart(Profile p) {

		if (!p.isKitLoaded())
			p.giveKit(getKit());

		BlockUtil.clearFakeBlocks(p);
	}

	public void onMatchStart() {
	}

	public void setScoreboard(Profile p) {
		if (data.isBoxing()) {
			p.setScoreboard(BoxingScoreboard.INSTANCE);
			return;
		}

		p.setScoreboard(InMatchScoreboard.INSTANCE);
	}

	public void updateVisiblity(Match<?> match, Profile profile) {
		if (match.getParticipants().contains(profile) || match.getSpectators().contains(profile))
			return;

		for (Profile participant : participants) {
			participant.getPlayer().hidePlayer(profile.getPlayer(), false);
			profile.getPlayer().hidePlayer(participant.getPlayer(), false);
		}
	}

	public void increasePlacedTnt() {
		placedTnt++;
	}

	public void decreasePlacedTnt() {
		placedTnt--;
	}

	public void handleFollowers(Profile profile) {
		for (Profile p : profile.getSpectateHandler().getFollowers()) {
			p.getPlayer().showPlayer(profile.getPlayer());
			p.getSpectateHandler().spectate(profile);
		}
	}

	public void handleOpponentMessages() {
		handleOpponentMessages(profile1, profile2);
		handleOpponentMessages(profile2, profile1);
	}

	public void handleOpponentMessages(Profile profile1, Profile profile2) {
		StringBuilder sb = new StringBuilder("Opponent: " + CC.AQUA + profile2.getName());

		if (data instanceof QueueMatchData qData)
			sb.append(qData.isRanked()
					? CC.WHITE + "\nElo: " + CC.AQUA + qData.getQueueEntry().getGametype().getElo(profile2)
					: "");

		profile1.getPlayer().sendMessage(CC.BOARD_SEPARATOR);
		profile1.getPlayer().sendMessage(sb.toString());
		profile1.getPlayer().sendMessage(CC.BOARD_SEPARATOR);
	}

	public void setWorldParameters(World world) {
		net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) world).getHandle();
		nmsWorld.getWorldData().f(false);
		nmsWorld.getWorldData().setThundering(false);
		nmsWorld.getWorldData().setStorm(false);
		nmsWorld.allowMonsters = false;
	}

	public boolean noArenas() {
		boolean arenaNull = data.getArena() == null;

		if (arenaNull) {
			ProfileManager.broadcast(participants, ErrorMessages.ARENA_NOT_FOUND);
			end(profile1);
		}

		return arenaNull;
	}

	public void setupLocations(Location location1, Location location2) {

		if (data.isGriefing() || data.isBuild()) {
			world = data.getArena().generate();
			location1.setWorld(world);
			location2.setWorld(world);
		}

		setWorldParameters(location1.getWorld());

	}

	public void teleportPlayers(Location location1, Location location2) {
		PlayerUtil.teleport(profile1.getPlayer(), location1);
		PlayerUtil.teleport(profile2.getPlayer(), location2);
	}

	public void startCountdown() {
		Countdown countdown = new Countdown(5, this);
		countdown.start();
	}

	public void start() {
		if (noArenas())
			return;

		MatchManager.registerMatch(this);
		Location location1 = data.getArena().getLocation1().clone();
		Location location2 = data.getArena().getLocation2().clone();

		setupLocations(location1, location2);
		teleportPlayers(location1, location2);

		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

		scheduler.scheduleSyncDelayedTask(PracticePlugin.INSTANCE, () -> {
			prepareForMatch(participants);
			handleOpponentMessages();
			startCountdown();
		}, 5L);
	}

	public void end(Profile victim) {
		if (isEnded())
			return;

		ended = true;
		end(getOpponent(victim), victim);
	}

	public Profile getOpponent(Profile p) {
		Profile p1 = getProfile1();
		return p1.equals(p) ? getProfile2() : p1;
	}

	public boolean incrementTeamHitCount(Profile attacker, Profile victim) {
		attacker.getMatchStatisticCollector().increaseHitCount();
		victim.getMatchStatisticCollector().resetCombo();

		if (attacker.getMatchStatisticCollector().getHitCount() >= 100 && getData().isBoxing()) {
			end(victim);
			return true;
		}

		return false;
	}

	public CompletableFuture<Void> updateElo(Gametype gametype, Profile attacker, Profile victim) {
		return gametype.getEloMap(attacker, victim)
				.thenAccept(map -> {
					int attackerElo = map.getInt(attacker.getUuid());
					int victimElo = map.getInt(victim.getUuid());
					int newAttackerElo = MathUtil.getNewRating(attackerElo, victimElo, true);
					int newVictimElo = MathUtil.getNewRating(victimElo, attackerElo, false);

					gametype.setElo(newAttackerElo, attacker);
					gametype.setElo(newVictimElo, victim);
					gametype.updatePlayerLeaderboard(victim, newVictimElo, victimElo);
					gametype.updatePlayerLeaderboard(attacker, newAttackerElo, attackerElo);

					String rankedMessage = CC.GREEN + attacker.getName() + " (+" + (newAttackerElo - attackerElo) + ") "
							+ CC.RED
							+ victim.getName() + " (" + (newVictimElo - victimElo) + ")";
					attacker.getPlayer().sendMessage(rankedMessage);
					victim.getPlayer().sendMessage(rankedMessage);
				});

	}

	public void end(Profile attacker, Profile victim) {
		attacker.getMatchStatisticCollector().end(true);
		victim.getMatchStatisticCollector().end(false);

		deathAnimation(attacker, victim);

		setInventoryStats(attacker, attacker.getMatchStatisticCollector());
		setInventoryStats(victim, victim.getMatchStatisticCollector());

		TextComponent winMessage = getWinMessage(attacker), loseMessage = getLoseMessage(victim);

		for (Profile profile : getParticipants()) {
			profile.getPlayer().sendMessage(CC.SEPARATOR);
			profile.getPlayer().sendMessage(Strings.MATCH_RESULTS);
			profile.getPlayer().spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage);
			profile.getPlayer().sendMessage(CC.SEPARATOR);
		}

		if (data instanceof QueueMatchData qData && qData.isRanked())
			updateElo(qData.getGametype(), attacker, victim);

		resetPearlCooldown(attacker, victim);
		attacker.setScoreboard(MatchEndScoreboard.INSTANCE);
		victim.setScoreboard(DefaultScoreboard.INSTANCE);
		MatchManager.remove(this);

		victim.heal();
		victim.removePotionEffects();
		sendBackToLobby(victim);

		giveQueueAgainItem(attacker);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			if (attacker.getPlayerStatus() == PlayerStatus.FIGHTING && !attacker.getMatch().isEnded())
				return;

			attacker.setScoreboard(DefaultScoreboard.INSTANCE);
			sendBackToLobby(attacker);

			if (CoreConnector.connected()) {

				// CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(profile1.getPlayer(),
				// profile2.getPlayer());
				/*
				 * CoreConnector.INSTANCE.getUuidChecker().check(attacker.getPlayer().
				 * getDisplayName());
				 * int mineralsAmount = data.isRanked() ? 100 : 20;
				 * CoreConnector.INSTANCE.getMineralsSQL().addMinerals(attacker.getPlayer(),
				 * de.jeezycore.utils.UUIDChecker.uuid, mineralsAmount,
				 * "&7You &2successfully &7earned &9" + mineralsAmount + " &fminerals&7.");
				 */
			}

		}, getPostMatchTime());

		for (Profile spectator : getSpectators()) {
			spectator.getPlayer().sendMessage(CC.SEPARATOR);
			spectator.getPlayer().sendMessage(Strings.MATCH_RESULTS);
			spectator.getPlayer().spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage);
			spectator.getPlayer().sendMessage(CC.SEPARATOR);
			spectator.getSpectateHandler().stopSpectating();
		}

		clearWorld();
	}

	public TextComponent getWinMessage(Profile profile) {
		TextComponent winMessage = new TextComponent(CC.GREEN + " Winner: " + CC.GRAY + profile.getName());
		winMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(CC.GREEN + "Health Potions Remaining: "
						+ profile.getMatchStatisticCollector().getPotionsRemaining() + "\n" + CC.GREEN
						+ "Hits: " + profile.getMatchStatisticCollector().getHitCount() + "\n" + CC.GREEN + "Health: "
						+ profile.getMatchStatisticCollector().getRemainingHealth()).create()));
		winMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + profile.getName()));
		return winMessage;
	}

	public TextComponent getLoseMessage(Profile profile) {
		TextComponent loseMessage = new TextComponent(CC.RED + "Loser: " + CC.GRAY + profile.getName());
		loseMessage.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(CC.RED + "Health Potions Remaining: "
								+ profile.getMatchStatisticCollector().getPotionsRemaining() + "\n"
								+ CC.RED + "Hits: " + profile.getMatchStatisticCollector().getHitCount() + "\n"
								+ CC.RED + "Health: 0")
								.create()));
		loseMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + profile.getName()));
		return loseMessage;
	}

	public void deathAnimation(Profile attacker, Profile victim) {
		attacker.heal();
		attacker.removePotionEffects();
		attacker.getInventory().clear();
		attacker.getPlayer().hidePlayer(victim.getPlayer(), false);
	}

	public void giveQueueAgainItem(Profile profile) {
		if (data instanceof QueueMatchData qData)
			Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE,
					() -> profile.getInventory().setItem(profile.getInventory().getHeldItemSlot(),
							ItemStacks.QUEUE_AGAIN,
							() -> profile.addPlayerToQueue(qData.getQueueEntry())),
					20);
	}

	public void sendBackToLobby(Profile profile) {
		profile.teleportToLobby();
		profile.getInventory().setInventoryForLobby();
		profile.removeFromMatch();
	}

	public void resetPearlCooldown(Profile... profiles) {
		for (Profile profile : profiles)
			profile.setPearlCooldown(0);
	}

	public void clearItems() {
		boolean arenaInUse = false;

		for (Match<?> match : MatchManager.getMatches()) {
			if (!match.isEnded() && match.getData().getArena().equals(data.getArena())) {
				arenaInUse = true;
				break;
			}
		}

		for (Item item : arenaInUse ? itemRemovalQueue
				: data.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class))
			item.remove();

		for (Arrow arrow : data.getArena().getLocation1().getWorld().getEntitiesByClass(Arrow.class)) {
			ProjectileSource shooter = arrow.getShooter();

			if (shooter instanceof Player pShooter) {
				Profile profile = ProfileManager.getProfile(pShooter.getUniqueId());

				if (profile == null) {
					arrow.remove();
					continue;
				}

				if (profile.getPlayerStatus() != PlayerStatus.FIGHTING
						|| (profile.getPlayerStatus() == PlayerStatus.FIGHTING && profile.getMatch().isEnded()))
					arrow.remove();
			}
		}

	}

	public void clearWorld() {
		clearItems();

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			if (world != null) {
				WorldUtil.deleteWorld(world);
				return;
			}

			for (Location location : buildLog)
				location.getBlock().setType(Material.AIR);

		}, getPostMatchTime() + 1);
	}

	public InventoryStatsMenu setInventoryStats(Profile profile, MatchStatisticCollector matchStatisticCollector) {

		InventoryStatsMenu menu = new InventoryStatsMenu(profile, getOpponent(profile).getName(),
				matchStatisticCollector);

		if (!(this instanceof PartyMatch || this instanceof TeamMatch))
			ProfileManager.setInventoryStats(profile, menu);

		return menu;
	}

	public void addParicipants(Profile... players) {
		participants.addAll(Arrays.asList(players));
	}

	public ProfileList getTeam(Profile profile) {
		return new ProfileList(Arrays.asList(profile));
	}

	/*
	 * private void setDisplayNames(Profile profile) {
	 * 
	 * org.bukkit.scoreboard.Scoreboard scoreboard =
	 * Bukkit.getScoreboardManager().getNewScoreboard();
	 * 
	 * Team teammates = scoreboard.registerNewTeam("teammates");
	 * Team opponents = scoreboard.registerNewTeam("opponents");
	 * 
	 * teammates.setPrefix(CC.GREEN);
	 * opponents.setPrefix(CC.RED);
	 * 
	 * teammates.addEntry(profile.getName());
	 * opponents.addEntry(getOpponent(profile).getName());
	 * 
	 * profile.getPlayer().setScoreboard(scoreboard);
	 * }
	 */
}
