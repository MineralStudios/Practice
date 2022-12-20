package gg.mineral.practice.match;

import java.util.Arrays;
import java.util.List;
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

import de.jeezycore.utils.NameTag;
import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
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
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.math.Countdown;
import gg.mineral.practice.util.math.MathUtil;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.Strings;
import gg.mineral.practice.util.messages.impl.TextComponents;
import gg.mineral.practice.util.world.WorldUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;

public class Match implements Spectatable {
	NameTag nameTag = new NameTag();
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
	int postMatchTime = 40;
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

	public Kit getKit(Profile p) {
		Kit kit = new Kit(data.getKit());
		ItemStack[] customKit = data.getQueueEntry() != null ? data.getQueueEntry().getCustomKit(p) : null;

		if (customKit != null) {
			kit.setContents(customKit);
		}

		return kit;
	}

	public void setAttributes(Profile p) {
		p.getMatchStatisticCollector().clearHitCount();
		p.getPlayer().setMaximumNoDamageTicks(data.getNoDamageTicks());
		p.getPlayer().setKnockback(data.getKnockback());
		p.getPlayer().setAllowFlight(false);
		p.setInventoryClickCancelled(false);
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
		p.giveKit(getKit(p));

		nameTag.clearTagOnMatchStart(p.getPlayer(), p.getPlayer());

		setAttributes(p);
		setPotionEffects(p);
		setVisibility(p);
		setDisplayNames(p);
		setScoreboard(p);
	}

	public void setScoreboard(Profile p) {
		if (data.getBoxing()) {
			new BoxingScoreboard(p).setBoard();
			return;
		}

		new InMatchScoreboard(p).setBoard();
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

	public void handleFollowers() {
		for (Profile p : profile1.getFollowers()) {
			p.getPlayer().showPlayer(profile1.getPlayer());
			p.spectate(profile1);
		}

		for (Profile p : profile2.getFollowers()) {
			p.getPlayer().showPlayer(profile2.getPlayer());
			p.spectate(profile2);
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
		handleFollowers();
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
		}

		resetPearlCooldown(attacker, victim);
		new MatchEndScoreboard(attacker).setBoard();
		new DefaultScoreboard(victim).setBoard();
		MatchManager.remove(this);

		victim.heal();
		victim.removePotionEffects();
		sendBackToLobby(victim);

		giveQueueAgainItem(attacker);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
			new DefaultScoreboard(attacker).setBoard();
			sendBackToLobby(attacker);
			nameTag.giveTagAfterMatch(profile1.getPlayer(), profile2.getPlayer());
		}, getPostMatchTime());

		for (Profile p : getSpectators()) {
			p.getPlayer().sendMessage(CC.SEPARATOR);
			p.getPlayer().sendMessage(Strings.MATCH_RESULTS);
			p.getPlayer().spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage);
			p.getPlayer().sendMessage(CC.SEPARATOR);
			p.stopSpectating();
		}

		clearWorld();
	}

	public TextComponent getWinMessage(Profile profile) {
		TextComponent winMessage = new TextComponent(CC.RED + " Loser: " + CC.GRAY + profile.getName());
		winMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(CC.GREEN + "Health Potions Remaining: "
						+ profile.getMatchStatisticCollector().getPotionsRemaining() + "\n" + CC.GREEN
						+ "Hits: " + profile.getMatchStatisticCollector().getHitCount() + "\n" + CC.GREEN + "Health: "
						+ profile.getMatchStatisticCollector().getRemainingHealth()).create()));
		winMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + profile.getName()));
		return winMessage;
	}

	public TextComponent getLoseMessage(Profile profile) {
		TextComponent loseMessage = new TextComponent(CC.GREEN + "Winner: " + CC.GRAY + profile.getName());
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
		attacker.getPlayer().hidePlayer(victim.getPlayer());
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
		profile.setInventoryForLobby();
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

		ItemStack potItem = new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 16421))
				.name("Health Potions Left")
				.lore("Thrown: " + matchStatisticCollector.getPotionsThrown(),
						"Missed: " + matchStatisticCollector.getPotionsMissed(),
						"Stolen: " + matchStatisticCollector.getPotionsStolen(),
						"Accuracy: " + matchStatisticCollector.getPotionAccuracy() + "%")
				.amount(Math.max(matchStatisticCollector.getPotionsRemaining(), 1)).build();
		ItemStack soupItem = new ItemBuilder(Material.MUSHROOM_SOUP)
				.name("Soup Left")
				.amount(Math.max(matchStatisticCollector.getSoupsRemaining(), 1)).build();
		ItemStack healthItem = matchStatisticCollector.getRemainingHealth() == 0 ? ItemStacks.NO_HEALTH
				: new ItemBuilder(
						new ItemStack(Material.POTION, matchStatisticCollector.getRemainingHealth(), (short) 8193))
						.name("Health: " + matchStatisticCollector.getRemainingHealth()).build();

		ItemStack hits = new ItemBuilder(Material.BLAZE_ROD)
				.name(profile.getMatchStatisticCollector().getHitCount() + " Hits")
				.lore("Longest Combo: " + matchStatisticCollector.getLongestCombo(),
						"Average Combo: " + matchStatisticCollector.getAverageCombo(),
						"W Tap Accuracy: " + matchStatisticCollector.getWTapAccuracy() + "%")
				.build();
		ItemStack clicks = new ItemBuilder(Material.GHAST_TEAR)
				.name("Highest CPS: " + matchStatisticCollector.getHighestCps())
				.lore().build();
		ItemStack effects = new ItemBuilder(Material.BLAZE_POWDER)
				.name("Potion Effects")
				.lore(matchStatisticCollector.getPotionEffectStringArray()).build();

		menu.setSlot(45, healthItem);
		menu.setSlot(46, potItem);
		menu.setSlot(47, soupItem);
		menu.setSlot(48, hits);
		menu.setSlot(49, clicks);
		menu.setSlot(50, effects);

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
