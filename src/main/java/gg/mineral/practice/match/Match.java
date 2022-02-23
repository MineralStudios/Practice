package gg.mineral.practice.match;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.core.utils.message.ChatMessage;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.managers.QueueEntryManager;
import gg.mineral.practice.scoreboard.BoxingScoreboard;
import gg.mineral.practice.scoreboard.InMatchScoreboard;
import gg.mineral.practice.scoreboard.PartyMatchScoreboard;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.util.Countdown;
import gg.mineral.practice.util.MathUtil;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.WorldUtil;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.ChatMessages;
import gg.mineral.practice.util.messages.ErrorMessages;
import land.strafe.api.collection.GlueList;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;
import net.minecraft.server.v1_8_R3.World;

public class Match {
	ProfileList participants = new ProfileList();
	Profile player1;
	Profile player2;
	boolean ended = false;
	int tntAmount;
	ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<>();
	MatchData m;
	GlueList<Location> buildLog = new GlueList<>();
	final MatchManager matchManager = PracticePlugin.INSTANCE.getMatchManager();
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();
	final QueueEntryManager queueEntryManager = PracticePlugin.INSTANCE.getQueueEntryManager();
	static final ExecutorService executor = Executors.newCachedThreadPool();
	org.bukkit.World world = null;

	public Match(Profile player1, Profile player2, MatchData m) {
		this.m = m;
		this.player1 = player1;
		this.player2 = player2;
		addParicipants(player1, player2);
	}

	public Match(MatchData m) {
		this.m = m;
	}

	public void prepareForMatch(Profile... profiles) {
		for (Profile profile : profiles) {
			prepareForMatch(profile);
		}
	}

	public void prepareForMatch(Profile p) {

		if (!p.bukkit().isOnline()) {
			end(p);
			return;
		}

		p.clearHitCount();

		Kit kit = new Kit(m.getKit());

		if (m.getQueueEntry() != null) {
			ItemStack[] customKit = m.getQueueEntry().getCustomKit(p);

			if (customKit != null) {
				kit.setContents(customKit);
			}
		}

		p.giveKit(kit);

		p.setMatch(this);
		p.bukkit().setMaximumNoDamageTicks(m.getNoDamageTicks());
		p.bukkit().setKnockback(m.getKnockback());

		if (!m.getDamage()) {
			p.bukkit().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 255));
		}

		if (m.getBoxing()) {
			p.bukkit().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, 1));
		}

		for (int i = 0; i < participants.size(); i++) {
			p.bukkit().showPlayer(participants.get(i).bukkit());
		}

		for (Match match : matchManager.getMatchs()) {
			match.updateVisiblity(this, p);
		}

		p.bukkit().setAllowFlight(false);
		p.setInventoryClickCancelled(false);

		if (this instanceof PartyMatch) {
			new PartyMatchScoreboard(p).setBoard();
			return;
		}

		setDisplayNames(p);

		if (m.getGametype().getBoxing()) {
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
				participant.bukkit().hidePlayer(profile.bukkit(), false);
				profile.bukkit().hidePlayer(participant.bukkit(), false);
			}
		}
	}

	public ConcurrentLinkedDeque<Profile> getSpectators() {
		return spectators;
	}

	public void increasePlacedTnt() {
		tntAmount++;
	}

	public int getPlacedTnt() {
		return tntAmount;
	}

	public void decreasePlacedTnt() {
		tntAmount--;
	}

	public void addSpectator(Profile player) {
		spectators.add(player);
	}

	public void handleFollowers() {
		for (Profile p : player1.getFollowers()) {
			p.teleport(player1);
			p.message(ChatMessages.SPECTATING.clone().replace("%player%", player1.getName()));
			ChatMessage broadcastedMessage = ChatMessages.SPECTATING_YOUR_MATCH.clone().replace("%player%",
					p.getName());
			player1.message(broadcastedMessage);
			player2.message(broadcastedMessage);
			p.bukkit().showPlayer(player1.bukkit());
			p.spectate(player1);
		}

		for (Profile p : player2.getFollowers()) {
			p.teleport(player2);
			p.message(ChatMessages.SPECTATING.clone().replace("%player%", player2.getName()));
			ChatMessage broadcastedMessage = ChatMessages.SPECTATING_YOUR_MATCH.clone().replace("%player%",
					p.getName());
			player1.message(broadcastedMessage);
			player2.message(broadcastedMessage);
			p.bukkit().showPlayer(player2.bukkit());
			p.spectate(player2);
		}
	}

	public void handleOpponentMessages() {
		String s1 = "Opponent: " + player2.getName();
		String s2 = "Opponent: " + player1.getName();

		if (m.isRanked()) {
			s1 += " (Elo: " + m.getQueueEntry().getGametype().getElo(player2) + ")";
			s2 += " (Elo: " + m.getQueueEntry().getGametype().getElo(player1) + ")";
		}

		player1.bukkit().sendMessage(CC.ACCENT + s1);
		player2.bukkit().sendMessage(CC.ACCENT + s2);
	}

	public void setWorldParameters(World world) {
		world.getWorldData().f(false);
		world.getWorldData().setThundering(false);
		world.getWorldData().setStorm(false);
		world.allowMonsters = false;
	}

	public void start() {

		if (m.getArena() == null) {
			playerManager.broadcast(participants, ErrorMessages.ARENA_NOT_FOUND);
			end(player1);
			return;
		}

		matchManager.registerMatch(this);
		Location location1 = m.getArena().getLocation1();
		Location location2 = m.getArena().getLocation2();
		location1.setDirection(m.getArena().getLocation1EyeVector());
		location2.setDirection(m.getArena().getLocation2EyeVector());

		if (m.getGriefing() || m.getBuild()) {
			world = m.getArena().generate();
			location1.setWorld(world);
			location2.setWorld(world);
		}

		setWorldParameters(((CraftWorld) location1.getWorld()).getHandle());
		handleFollowers();
		prepareForMatch(player1, player2);
		player1.teleport(location1);
		player2.teleport(location2);
		handleOpponentMessages();
		Countdown countdown = new Countdown(5, this);
		countdown.start();
	}

	public void end(Profile victim) {
		if (ended) {
			return;
		}

		ended = true;
		end(getOpponent(victim), victim);
	}

	public Profile getOpponent(Profile p) {
		Profile p1 = getPlayer1();
		return p1.equals(p) ? getPlayer2() : p1;
	}

	public void end(Profile attacker, Profile victim) {
		int attackerHealth = (int) attacker.bukkit().getHealth();
		attacker.heal();
		attacker.removePotionEffects();
		attacker.bukkit().hidePlayer(victim.bukkit());

		String rankedMessage = null;

		if (m.isRanked()) {
			Gametype g = m.getQueueEntry().getGametype();
			int attackerElo = g.getElo(attacker);
			int victimElo = g.getElo(victim);
			int newAttackerElo = MathUtil.getNewRating(attackerElo, victimElo, true);
			int newVictimElo = MathUtil.getNewRating(victimElo, attackerElo, false);
			rankedMessage = CC.GREEN + attacker.getName() + " (+" + (newAttackerElo - attackerElo) + ") " + CC.RED
					+ victim.getName() + " (" + (newVictimElo - victimElo) + ")";
			executor.execute(() -> {
				g.setElo(newAttackerElo, attacker);
				g.setElo(newVictimElo, victim);
				g.updatePlayerLeaderboard(victim, newVictimElo);
				g.updatePlayerLeaderboard(attacker, newAttackerElo);
			});
		}

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
		attacker.bukkit().sendMessage(CC.SEPARATOR);
		attacker.bukkit().sendMessage(viewinv);
		attacker.bukkit().spigot().sendMessage(winmessage, splitter, losemessage);
		victim.bukkit().sendMessage(CC.SEPARATOR);
		victim.bukkit().sendMessage(viewinv);
		victim.bukkit().spigot().sendMessage(winmessage, splitter, losemessage);

		if (m.isRanked()) {
			attacker.bukkit().sendMessage(rankedMessage);
			victim.bukkit().sendMessage(rankedMessage);
		}

		attacker.bukkit().sendMessage(CC.SEPARATOR);
		victim.bukkit().sendMessage(CC.SEPARATOR);
		attacker.setPearlCooldown(0);
		victim.setPearlCooldown(0);
		new Scoreboard(player1).setBoard();
		new Scoreboard(player2).setBoard();
		victim.removeFromMatch();
		matchManager.remove(this);

		Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
			public void run() {
				if (victim.bukkit().isDead()) {
					victim.bukkit().getHandle().playerConnection
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

				if (getSpectators().size() > 0) {
					for (Profile p : getSpectators()) {
						p.bukkit().sendMessage(CC.SEPARATOR);
						p.bukkit().sendMessage(viewinv);
						p.bukkit().spigot().sendMessage(winmessage, splitter, losemessage);
						p.bukkit().sendMessage(CC.SEPARATOR);
						p.stopSpectating();
					}
				}
			}
		}, 40);

		if (world != null) {
			WorldUtil.deleteWorld(world);
			return;
		}

		for (Item item : m.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class)) {
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
				.name(p.getHitCount() + " Hits").build();
		menu.setSlot(47, hits);
		menu.setSlot(45, healthItem);
		menu.setSlot(46, potItem);

		if (this instanceof PartyMatch) {
			playerManager.setPartyInventoryStats(p, menu);
		}

		playerManager.setInventoryStats(p, menu);
		p.clearInventory();
	}

	public Profile getPlayer1() {
		return player1;
	}

	public Profile getPlayer2() {
		return player2;
	}

	public GlueList<Location> getBuildLog() {
		return buildLog;
	}

	public MatchData getData() {
		return m;
	}

	public void addParicipants(Profile... players) {
		participants.addAll(Arrays.asList(players));
	}

	public ProfileList getParticipants() {
		return participants;
	}

	public ProfileList getTeam(Profile p) {
		return new ProfileList(Arrays.asList(p));
	}

	public void setDisplayNames(Profile player) {

		org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		Team teammates = scoreboard.registerNewTeam("teammates");
		Team opponents = scoreboard.registerNewTeam("opponents");

		teammates.setPrefix(CC.GREEN);
		opponents.setPrefix(CC.RED);

		teammates.addEntry(player.getName());
		opponents.addEntry(getOpponent(player).getName());

		player.bukkit().setScoreboard(scoreboard);
	}
}
