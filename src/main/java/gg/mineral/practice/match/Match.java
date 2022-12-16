package gg.mineral.practice.match;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.jeezycore.utils.NameTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.scoreboard.impl.BoxingScoreboard;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.InMatchScoreboard;
import gg.mineral.practice.traits.Spectatable;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.math.Countdown;
import gg.mineral.practice.util.math.MathUtil;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.world.WorldUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class Match implements Spectatable {
	NameTag nameTag = new NameTag();
	@Getter
	ProfileList participants = new ProfileList();
	@Getter
	Profile profile1, profile2;
	boolean ended = false;
	@Getter
	int placedTnt;
	@Getter
	MatchData data;
	@Getter
	GlueList<Location> buildLog = new GlueList<>();
	org.bukkit.World world = null;

	public Match(Profile player1, Profile player2, MatchData matchData) {
		this(matchData);
		this.profile1 = player1;
		this.profile2 = player2;
		addParicipants(player1, player2);
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
		p.clearHitCount();
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
		p.giveKit(getKit(p));

		setAttributes(p);
		setPotionEffects(p);
		setVisibility(p);
		setDisplayNames(p);
		setScoreboard(p);
	}

	public void setScoreboard(Profile p) {
		if (data.getGametype().getBoxing()) {
			new BoxingScoreboard(p).setBoard();
			return;
		}

		new InMatchScoreboard(p).setBoard();
	}

	public void updateVisiblity(Match match, Profile profile) {
		if (match.equals(this)) {
			return;
		}

		if (match.getData().getArena().equals(getData().getArena())) {
			for (int i = 0; i < participants.size(); i++) {
				Profile participant = participants.get(i);
				participant.getPlayer().hidePlayer(profile.getPlayer(), false);
				profile.getPlayer().hidePlayer(participant.getPlayer(), false);
			}
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
		profile1.teleport(location1);
		profile2.teleport(location2);
	}

	public void startCountdown() {
		Countdown countdown = new Countdown(5, this);
		countdown.start();
	}

	public void start() {
		if (noArenas())
			return;

		MatchManager.registerMatch(this);
		nameTag.clearTagOnMatchStart(profile1.getPlayer(), profile2.getPlayer());
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
		if (ended) {
			return;
		}

		ended = true;
		end(getOpponent(victim), victim);
	}

	public Profile getOpponent(Profile p) {
		Profile p1 = getProfile1();
		return p1.equals(p) ? getProfile2() : p1;
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
		int attackerHealth = (int) attacker.getPlayer().getHealth();
		attacker.heal();
		attacker.removePotionEffects();
		attacker.getPlayer().hidePlayer(victim.getPlayer());

		int attackerAmountOfPots = attacker.getNumber(Material.POTION, (short) 16421)
				+ attacker.getNumber(Material.MUSHROOM_SOUP);

		setInventoryStats(attacker, attackerHealth, attackerAmountOfPots);

		int victimAmountOfPots = victim.getNumber(Material.POTION, (short) 16421)
				+ victim.getNumber(Material.MUSHROOM_SOUP);

		setInventoryStats(victim, 0, victimAmountOfPots);
		String viewinv = CC.YELLOW + "Match Results";
		TextComponent winmessage = new TextComponent(CC.GREEN + "Winner: " + CC.GRAY + attacker.getName());
		TextComponent splitter = new TextComponent(CC.D_GRAY + " - ");
		TextComponent losemessage = new TextComponent(CC.RED + "Loser: " + CC.GRAY + victim.getName());
		losemessage
				.setHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder(CC.RED + "Health Potions Remaining: " + victimAmountOfPots + "\n"
										+ CC.RED + "Hits: " + victim.getHitCount() + "\n" + CC.RED + "Health: 0")
										.create()));
		winmessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(CC.GREEN + "Health Potions Remaining: " + attackerAmountOfPots + "\n" + CC.GREEN
						+ "Hits: " + attacker.getHitCount() + "\n" + CC.GREEN + "Health: " + attackerHealth).create()));
		losemessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + victim.getName()));
		winmessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + attacker.getName()));
		attacker.getPlayer().sendMessage(CC.SEPARATOR);
		attacker.getPlayer().sendMessage(viewinv);
		attacker.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);
		victim.getPlayer().sendMessage(CC.SEPARATOR);
		victim.getPlayer().sendMessage(viewinv);
		victim.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);

		if (data.isRanked()) {
			updateElo(attacker, victim).whenComplete((type, ex) -> {
				attacker.getPlayer().sendMessage(CC.SEPARATOR);
				victim.getPlayer().sendMessage(CC.SEPARATOR);
			});
		} else {
			attacker.getPlayer().sendMessage(CC.SEPARATOR);
			victim.getPlayer().sendMessage(CC.SEPARATOR);
		}

		attacker.getPlayer().sendMessage(CC.SEPARATOR);
		victim.getPlayer().sendMessage(CC.SEPARATOR);
		attacker.setPearlCooldown(0);
		victim.setPearlCooldown(0);
		new DefaultScoreboard(profile1).setBoard();
		new DefaultScoreboard(profile2).setBoard();
		victim.removeFromMatch();
		MatchManager.remove(this);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
			public void run() {
				if (victim.getPlayer().isDead()) {
					victim.getPlayer().getHandle().playerConnection
							.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
				}

				victim.heal();
				victim.removePotionEffects();
				victim.teleportToLobby();
				victim.setInventoryForLobby();
			}
		}, 1);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
			public void run() {

				attacker.removeFromMatch();
				attacker.teleportToLobby();
				attacker.setInventoryForLobby();
				nameTag.giveTagAfterMatch(profile1.getPlayer(), profile2.getPlayer());

				for (Profile p : getSpectators()) {
					p.getPlayer().sendMessage(CC.SEPARATOR);
					p.getPlayer().sendMessage(viewinv);
					p.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);
					p.getPlayer().sendMessage(CC.SEPARATOR);
					p.stopSpectating();
				}
			}
		}, 40);

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
	}

	public void setInventoryStats(Profile p, int health, int amountOfPots) {
		amountOfPots = amountOfPots == 0 ? 1 : amountOfPots;

		InventoryStatsMenu menu = new InventoryStatsMenu(p, getOpponent(p).getName());

		try {
			PlayerInventory inv = p.getInventory();
			menu.setContents(inv.getContents());
			menu.setSlot(36, inv.getHelmet());
			menu.setSlot(37, inv.getChestplate());
			menu.setSlot(38, inv.getLeggings());
			menu.setSlot(39, inv.getBoots());
		} catch (Exception e) {
		}

		ItemStack potItem = new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 16421))
				.name("Health Potions Left").amount(amountOfPots).build();
		ItemStack healthItem = health == 0 ? ItemStacks.NO_HEALTH
				: new ItemBuilder(new ItemStack(Material.POTION, health, (short) 8193))
						.name("Health: " + health).build();

		ItemStack hits = new ItemBuilder(Material.BLAZE_ROD)
				.name(p.getHitCount() + " Hits").lore("Longest Combo: " + p.getLongestCombo()).build();
		menu.setSlot(47, hits);
		menu.setSlot(45, healthItem);
		menu.setSlot(46, potItem);

		if (this instanceof PartyMatch) {
			ProfileManager.setPartyInventoryStats(p, menu);
		}

		ProfileManager.setInventoryStats(p, menu);
		p.getInventory().clear();
	}

	public void addParicipants(Profile... players) {
		participants.addAll(Arrays.asList(players));
	}

	public List<Profile> getTeam(Profile p) {
		return Arrays.asList(p);
	}

	private void setDisplayNames(Profile player) {

		org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		Team teammates = scoreboard.registerNewTeam("teammates");
		Team opponents = scoreboard.registerNewTeam("opponents");

		teammates.setPrefix(CC.GREEN);
		opponents.setPrefix(CC.RED);

		teammates.addEntry(player.getName());
		opponents.addEntry(getOpponent(player).getName());

		player.getPlayer().setScoreboard(scoreboard);
	}
}
