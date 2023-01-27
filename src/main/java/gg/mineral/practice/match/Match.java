package gg.mineral.practice.match;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

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
import gg.mineral.practice.util.world.WorldUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;

public class Match implements Spectatable {

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
	int postMatchTime = 60;
	org.bukkit.World world = null;

	int mineralsAmount = 20;

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
		p.getPlayer().setMaximumNoDamageTicks(data.getNoDamageTicks());
		p.getPlayer().setKnockback(data.getKnockback());
		p.getPlayer().setAllowFlight(false);
		p.getInventory().setInventoryClickCancelled(false);
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
		for (int i = 0; i < participants.size(); i++) {
			p.getPlayer().showPlayer(participants.get(i).getPlayer());
		}

		for (Match match : MatchManager.getMatches()) {
			match.updateVisiblity(this, p);
		}
	}

	public void prepareForMatch(Profile p) {

		p.setMatch(this);
		p.getMatchStatisticCollector().start();
		p.setKitLoaded(false);

		if (CoreConnector.connected()) {
			CoreConnector.INSTANCE.getNameTagAPI().clearTagOnMatchStart(p.getPlayer(), p.getPlayer());
		}

		giveLoadoutSelection(p);
		setAttributes(p);
		setPotionEffects(p);
		setVisibility(p);
		setDisplayNames(p);
		setScoreboard(p);
		handleFollowers(p);
	}

	public void giveLoadoutSelection(Profile p) {

		Int2ObjectOpenHashMap<ItemStack[]> map = data.getQueueEntry() == null ? null
				: data.getQueueEntry().getCustomKits(p);

		if (map == null ? true : map.isEmpty()) {
			p.giveKit(getKit());
			return;
		}

		if (map.size() < 2) {
			ObjectIterator<ItemStack[]> iter = map.values().iterator();

			if (iter.hasNext()) {
				p.giveKit(getKit(iter.next()));
				return;
			}

			p.giveKit(getKit());
			return;
		}

		p.getInventory().clear();

		for (Entry<Integer, ItemStack[]> entry : map.int2ObjectEntrySet()) {
			p.getInventory().setItem(entry.getKey(),
					ItemStacks.LOAD_KIT.name(CC.B + CC.GOLD + "Load Kit #" + entry.getKey()).build(), profile -> {
						p.giveKit(getKit(entry.getValue()));
						return true;
					});
		}

	}

	public void onMatchStart(Profile p) {
		if (!p.isKitLoaded()) {
			p.giveKit(getKit());
		}
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

		for (int i = 0; i < participants.size(); i++) {
			Profile participant = participants.get(i);
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
		String s1 = "Opponent: " + profile2.getName();
		String s2 = "Opponent: " + profile1.getName();

		if (data.isRanked()) {
			s1 += " (Elo: " + data.getQueueEntry().getGametype().getElo(profile2) + ")";
			s2 += " (Elo: " + data.getQueueEntry().getGametype().getElo(profile1) + ")";
		}

		profile1.getPlayer().sendMessage(CC.ACCENT + s1);
		profile2.getPlayer().sendMessage(CC.ACCENT + s2);
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
					int attackerElo = map.getInt(attacker.getUUID());
					int victimElo = map.getInt(victim.getUUID());
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
		attacker.getMatchStatisticCollector().end();
		victim.getMatchStatisticCollector().end();

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
			mineralsAmount = 100;
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

			if (CoreConnector.connected()) {
				CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(profile1.getPlayer(), profile2.getPlayer());
				CoreConnector.INSTANCE.getUuidChecker().check(attacker.getPlayer().getDisplayName());
				CoreConnector.INSTANCE.getMineralsSQL().addMinerals(attacker.getPlayer(),
						de.jeezycore.utils.UUIDChecker.uuid, mineralsAmount,
						"&7You &2successfully &7earned &9" + mineralsAmount + " &fminerals&7.");
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

	public void clearWorld() {
		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			if (world != null) {
				WorldUtil.deleteWorld(world);
				return;
			}

			for (Item item : data.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class)) {
				EntityHuman lastHolder = ((EntityItem) ((CraftItem) item).getHandle()).lastHolder;

				if (lastHolder == null) {
					continue;
				}

				for (Profile participant : participants) {
					if (lastHolder.getBukkitEntity().getUniqueId() == participant.getUUID()) {
						item.remove();
					}
				}
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

		menu.setSlot(45, matchStatisticCollector.getRemainingHealth() <= 0 ? ItemStacks.NO_HEALTH
				: ItemStacks.HEALTH
						.name("Health: " + matchStatisticCollector.getRemainingHealth())
						.amount(matchStatisticCollector.getRemainingHealth()).build());

		menu.setSlot(46, ItemStacks.HEALTH_POTIONS_LEFT
				.lore("Thrown: " + matchStatisticCollector.getPotionsThrown(),
						"Missed: " + matchStatisticCollector.getPotionsMissed(),
						"Stolen: " + matchStatisticCollector.getPotionsStolen(),
						"Accuracy: " + matchStatisticCollector.getPotionAccuracy() + "%")
				.amount(Math.max(matchStatisticCollector.getPotionsRemaining(), 1)).build());

		menu.setSlot(47, ItemStacks.SOUP_LEFT
				.amount(Math.max(matchStatisticCollector.getSoupsRemaining(), 1)).build());

		menu.setSlot(48, ItemStacks.HITS
				.name(profile.getMatchStatisticCollector().getHitCount() + " Hits")
				.lore("Longest Combo: " + matchStatisticCollector.getLongestCombo(),
						"Average Combo: " + matchStatisticCollector.getAverageCombo(),
						"W Tap Accuracy: " + matchStatisticCollector.getWTapAccuracy() + "%")
				.build());

		menu.setSlot(49, ItemStacks.CLICKS
				.name("Highest CPS: " + matchStatisticCollector.getHighestCps()).build());

		menu.setSlot(50, ItemStacks.POTION_EFFECTS
				.lore(matchStatisticCollector.getPotionEffectStringArray()).build());

		if (!(this instanceof PartyMatch)) {
			ProfileManager.setInventoryStats(profile, menu);
		}

		return menu;
	}

	public void addParicipants(Profile... players) {
		participants.addAll(Arrays.asList(players));
	}

	public List<Profile> getTeam(Profile profile) {
		return Arrays.asList(profile);
	}

	private void setDisplayNames(Profile profile) {

		org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		Team teammates = scoreboard.registerNewTeam("teammates");
		Team opponents = scoreboard.registerNewTeam("opponents");

		teammates.setPrefix(CC.GREEN);
		opponents.setPrefix(CC.RED);

		teammates.addEntry(profile.getName());
		opponents.addEntry(getOpponent(profile).getName());

		profile.getPlayer().setScoreboard(scoreboard);
	}
}
