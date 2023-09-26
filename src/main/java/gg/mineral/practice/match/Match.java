package gg.mineral.practice.match;

import java.util.Arrays;
import java.util.Map.Entry;
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
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.QueueSearchTask2v2;
import gg.mineral.practice.scoreboard.impl.BoxingScoreboard;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.InMatchScoreboard;
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard;
import gg.mineral.practice.traits.Spectatable;
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
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Match implements Spectatable {

	@Getter
	ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<>();
	@Getter
	ProfileList participants = new ProfileList();
	@Getter
	Profile profile1, profile2;
	@Getter
	boolean ended = false;
	@Getter
	int placedTnt;
	@Getter
	MatchData data;
	@Getter
	GlueList<Location> buildLog = new GlueList<>();
	@Getter
	static int postMatchTime = 60;
	@Getter
	Queue<Item> itemRemovalQueue = new ConcurrentLinkedQueue<>();
	org.bukkit.World world = null;

	public Match(Profile profile1, Profile profile2, MatchData matchData) {
		this(matchData);
		this.profile1 = profile1;
		this.profile2 = profile2;
		addParicipants(profile1, profile2);
	}

	public Match(MatchData matchData) {
		this.data = matchData;
	}

	public void prepareForMatch(ProfileList profiles) {
		for (Profile profile : profiles) {
			prepareForMatch(profile);
		}
	}

	public Kit getKit(Profile p, int loadoutSlot) {
		ItemStack[] customKit = data.getQueueEntry() != null ? data.getQueueEntry().getCustomKits(p).get(loadoutSlot)
				: null;
		return getKit(customKit);
	}

	public Kit getKit(ItemStack[] customKit) {
		Kit kit = getKit();

		if (customKit != null) {
			kit.setContents(customKit);
		}

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
		p.getPlayer().setAllowFlight(false);
		p.getInventory().setInventoryClickCancelled(false);
		p.getPlayer().setSaturation(20);
		p.getPlayer().setFoodLevel(20);
	}

	public void setPotionEffects(Profile p) {
		if (!data.getDamage()) {
			p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 255));
		}

		if (data.getBoxing()) {
			p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, 1));
		}
	}

	public void setVisibility(Profile p) {
		for (Profile participant : participants) {
			p.getPlayer().showPlayer(participant.getPlayer());
		}

		for (Match match : MatchManager.getMatches()) {
			match.updateVisiblity(this, p);
		}
	}

	public void prepareForMatch(Profile p) {

		QueueSearchTask.removePlayer(p);
		QueueSearchTask2v2.removePlayer(p);

		p.setMatch(this);
		p.getRequestHandler().getRecievedDuelRequests().clear();
		p.getMatchStatisticCollector().start();
		p.setKitLoaded(false);

		/*
		 * if (CoreConnector.connected()) {
		 * if (this instanceof PartyMatch || this instanceof TeamMatch) {
		 * CoreConnector.INSTANCE.getNameTagAPI().clearTagOnMatchStart(p.getPlayer(),
		 * p.getPlayer());
		 * } else {
		 * CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(p.getPlayer(),
		 * p.getPlayer());
		 * }
		 * }
		 */

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
				if (x == -radius || x == radius || z == -radius || z == radius) {
					for (int i = 0; i <= 2; i++) {
						BlockUtil.sendFakeBlock(profile,
								blockData.clone().setType(Material.IRON_FENCE).translate(x, i, z));
					}
				}

				BlockUtil.sendFakeBlock(profile, blockData.clone().translate(x, -1, z));
				BlockUtil.sendFakeBlock(profile, blockData.clone().translate(x, 3, z));
			}
		}
	}

	public void giveLoadoutSelection(Profile p) {

		Int2ObjectOpenHashMap<ItemStack[]> map = data.getQueueEntry() == null ? null
				: data.getQueueEntry().getCustomKits(p);

		p.getInventory().clear();

		if (map == null ? true : map.isEmpty()) {
			return;
		}

		for (Entry<Integer, ItemStack[]> entry : map.int2ObjectEntrySet()) {
			p.getInventory().setItem(entry.getKey(),
					ItemStacks.LOAD_KIT.name(CC.B + CC.GOLD + "Load Kit #" + entry.getKey()).build(), profile -> {
						p.giveKit(getKit(entry.getValue()));
						return true;
					});
		}

	}

	public void onCountdownStart(Profile p) {
		encapsulate(p);
	}

	public void onMatchStart(Profile p) {
		if (!p.isKitLoaded()) {
			p.giveKit(getKit());
		}

		BlockUtil.clearFakeBlocks(p);
	}

	public void setScoreboard(Profile p) {
		if (data.getBoxing()) {
			p.setScoreboard(BoxingScoreboard.INSTANCE);
			return;
		}

		p.setScoreboard(InMatchScoreboard.INSTANCE);
	}

	public void updateVisiblity(Match match, Profile profile) {
		if (match.equals(this) || !match.getData().getArena().equals(getData().getArena())) {
			return;
		}

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
		StringBuilder sb = new StringBuilder("Opponent: " + CC.AQUA + profile2.getName())
				.append(data.isRanked()
						? CC.WHITE + "\nElo: " + CC.AQUA + data.getQueueEntry().getGametype().getElo(profile2)
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

		if (data.getGriefing() || data.getBuild()) {
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
		prepareForMatch(participants);
		handleOpponentMessages();
		startCountdown();
	}

	public void end(Profile victim) {
		if (isEnded()) {
			return;
		}

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

		if (attacker.getMatchStatisticCollector().getHitCount() >= 100 && getData().getBoxing()) {
			end(victim);
			return true;
		}

		return false;
	}

	public CompletableFuture<Void> updateElo(Profile attacker, Profile victim) {
		Gametype gametype = data.getQueueEntry().getGametype();
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

		if (data.isRanked()) {
			updateElo(attacker, victim);

		}

		resetPearlCooldown(attacker, victim);
		attacker.setScoreboard(MatchEndScoreboard.INSTANCE);
		victim.setScoreboard(DefaultScoreboard.INSTANCE);
		MatchManager.remove(this);

		victim.heal();
		victim.removePotionEffects();
		sendBackToLobby(victim);

		giveQueueAgainItem(attacker);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			if (attacker.getPlayerStatus() == PlayerStatus.FIGHTING && !attacker.getMatch().isEnded()) {
				return;
			}

			attacker.setScoreboard(DefaultScoreboard.INSTANCE);
			sendBackToLobby(attacker);

			/*
			 * if (CoreConnector.connected()) {
			 * //
			 * CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(profile1.getPlayer()
			 * ,
			 * // profile2.getPlayer());
			 * CoreConnector.INSTANCE.getUuidChecker().check(attacker.getPlayer().
			 * getDisplayName());
			 * int mineralsAmount = data.isRanked() ? 100 : 20;
			 * CoreConnector.INSTANCE.getMineralsSQL().addMinerals(attacker.getPlayer(),
			 * de.jeezycore.utils.UUIDChecker.uuid, mineralsAmount,
			 * "&7You &2successfully &7earned &9" + mineralsAmount + " &fminerals&7.");
			 * }
			 */

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
		if (getData().getQueueEntry() != null) {
			Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
				int slot = profile.getInventory().getHeldItemSlot();

				profile.getInventory().setItem(slot, ItemStacks.QUEUE_AGAIN,
						() -> {
							profile.addPlayerToQueue(getData().getQueueEntry());
						});
			}, 20);
		}
	}

	public void sendBackToLobby(Profile profile) {
		profile.teleportToLobby();
		profile.getInventory().setInventoryForLobby();
		profile.removeFromMatch();
	}

	public void resetPearlCooldown(Profile... profiles) {
		for (Profile profile : profiles) {
			profile.setPearlCooldown(0);
		}
	}

	public void clearItems() {
		boolean arenaInUse = false;

		for (Match match : MatchManager.getMatches()) {
			if (!match.isEnded() && match.getData().getArena().equals(data.getArena())) {
				arenaInUse = true;
				break;
			}
		}

		for (Item item : arenaInUse ? itemRemovalQueue
				: data.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class)) {
			item.remove();
		}

		for (Arrow arrow : data.getArena().getLocation1().getWorld().getEntitiesByClass(Arrow.class)) {
			ProjectileSource shooter = arrow.getShooter();

			if (shooter instanceof Player) {
				Profile profile = ProfileManager.getProfile(p -> p.getUuid().equals(((Player) shooter).getUniqueId()));

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

			for (Location location : buildLog) {
				location.getBlock().setType(Material.AIR);
			}
		}, getPostMatchTime() + 1);
	}

	public InventoryStatsMenu setInventoryStats(Profile profile, MatchStatisticCollector matchStatisticCollector) {

		InventoryStatsMenu menu = new InventoryStatsMenu(profile, getOpponent(profile).getName());

		menu.setContents(matchStatisticCollector.getInventoryContents());
		menu.setSlot(36, matchStatisticCollector.getHelmet());
		menu.setSlot(37, matchStatisticCollector.getChestplate());
		menu.setSlot(38, matchStatisticCollector.getLeggings());
		menu.setSlot(39, matchStatisticCollector.getBoots());

		menu.setSlot(45, !matchStatisticCollector.isAlive() ? ItemStacks.NO_HEALTH
				: ItemStacks.HEALTH
						.name(CC.SECONDARY + CC.B + "Health")
						.lore(" ", CC.WHITE + "Remaining:", CC.GOLD + matchStatisticCollector.getRemainingHealth())
						.amount(matchStatisticCollector.getRemainingHealth()).build());

		menu.setSlot(46, ItemStacks.HEALTH_POTIONS_LEFT
				.lore(" ", CC.WHITE + "Thrown: " + CC.GOLD + matchStatisticCollector.getPotionsThrown(),
						CC.WHITE + "Missed: " + CC.GOLD + matchStatisticCollector.getPotionsMissed(),
						CC.WHITE + "Stolen: " + CC.GOLD + matchStatisticCollector.getPotionsStolen(),
						CC.WHITE + "Accuracy: " + CC.GOLD + matchStatisticCollector.getPotionAccuracy() + "%")
				.amount(Math.max(matchStatisticCollector.getPotionsRemaining(), 1)).build());

		menu.setSlot(47, ItemStacks.SOUP_LEFT
				.amount(Math.max(matchStatisticCollector.getSoupsRemaining(), 1)).build());

		menu.setSlot(48, ItemStacks.HITS
				.name(CC.SECONDARY + CC.B + profile.getMatchStatisticCollector().getHitCount() + " Hits")
				.lore(CC.WHITE + "Longest Combo: " + CC.GOLD + matchStatisticCollector.getLongestCombo(),
						CC.WHITE + "Average Combo: " + CC.GOLD + matchStatisticCollector.getAverageCombo(),
						CC.WHITE + "W Tap Accuracy: " + CC.GOLD + matchStatisticCollector.getWTapAccuracy() + "%")
				.build());

		menu.setSlot(49, ItemStacks.CLICKS
				.name(CC.SECONDARY + CC.B + "Highest CPS: " + matchStatisticCollector.getHighestCps()).build());

		menu.setSlot(50, ItemStacks.POTION_EFFECTS
				.lore(matchStatisticCollector.getPotionEffectStringArray()).build());

		if (!(this instanceof PartyMatch || this instanceof TeamMatch)) {
			ProfileManager.setInventoryStats(profile, menu);
		}

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
