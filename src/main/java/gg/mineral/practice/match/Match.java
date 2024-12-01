package gg.mineral.practice.match;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

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
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.match.data.MatchStatisticCollector;
import gg.mineral.practice.queue.QueueSystem;
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
import gg.mineral.practice.util.math.PearlCooldown;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.Strings;
import gg.mineral.practice.util.messages.impl.TextComponents;
import gg.mineral.practice.util.world.WorldUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

@RequiredArgsConstructor
public class Match implements Spectatable {

    @Getter
    ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<Profile>();
    @Getter
    ProfileList participants = new ProfileList();
    @Getter
    protected Profile profile1, profile2;
    @Getter
    boolean ended = false;
    @Getter
    int placedTnt;
    @Getter
    private final MatchData data;
    @Getter
    GlueList<Location> buildLog = new GlueList<>();
    @Getter
    static int postMatchTime = 60;
    @Getter
    Queue<Item> itemRemovalQueue = new ConcurrentLinkedQueue<>();
    org.bukkit.World world = null;
    private Map<UUID, MatchStatisticCollector> matchStatisticMap = new Object2ObjectOpenHashMap<>();
    @Getter
    protected static PearlCooldown pearlCooldown = new PearlCooldown();
    protected int timeRemaining, timeTaskId;

    public Match(Profile profile1, Profile profile2, MatchData matchData) {
        this(matchData);
        this.profile1 = profile1;
        this.profile2 = profile2;
        addParicipants(profile1, profile2);
    }

    public void prepareForMatch(ProfileList profiles) {
        for (val profile : profiles)
            prepareForMatch(profile);
    }

    protected int getTimeLimitSec() {
        val mins = 5 * Math.log10(5 * participants.size());
        return (int) (mins * 60);
    }

    protected void startMatchTimeLimit() {
        this.timeRemaining = getTimeLimitSec();
        this.timeTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE,
                () -> {
                    if (isEnded())
                        return;
                    if (timeRemaining-- <= 0)
                        end(profile1);
                }, 0, 20);
    }

    public Kit getKit(Profile p, int loadoutSlot) {
        val customKit = data.getCustomKits(p).get(loadoutSlot);
        return getKit(customKit);
    }

    public Kit getKit(@Nullable ItemStack[] customKit) {
        val kit = getKit();

        if (customKit != null)
            kit.setContents(customKit);

        return kit;
    }

    public Kit getKit() {
        return new Kit(data.getKit());
    }

    public void stat(UUID uuid, Consumer<MatchStatisticCollector> consumer) {
        if (isEnded())
            return;
        val profile = this.getParticipants().get(uuid);

        if (profile == null)
            return;

        val collector = matchStatisticMap.computeIfAbsent(profile.getUuid(),
                u -> new MatchStatisticCollector(profile));
        consumer.accept(collector);
    }

    protected void stat(Profile profile, Consumer<MatchStatisticCollector> consumer) {
        val collector = matchStatisticMap.computeIfAbsent(profile.getUuid(),
                u -> new MatchStatisticCollector(profile));
        consumer.accept(collector);
    }

    public <T> T computeStat(UUID uuid, Function<MatchStatisticCollector, T> function) {
        val profile = this.getParticipants().get(uuid);
        val collector = matchStatisticMap.computeIfAbsent(profile.getUuid(),
                u -> new MatchStatisticCollector(profile));
        return function.apply(collector);
    }

    public void setAttributes(Profile p) {
        stat(p, MatchStatisticCollector::clearHitCount);
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

    public void prepareForMatch(Profile p) {

        QueueSystem.removePlayerFromQueue(p);

        val currentMatch = p.getMatch();
        p.setMatch(this);

        if (currentMatch != null && !currentMatch.isEnded())
            currentMatch.end(p);

        p.getRequestHandler().getRecievedDuelRequests().clear();
        stat(p, MatchStatisticCollector::start);
        p.setKitLoaded(false);

        giveLoadoutSelection(p);
        setAttributes(p);
        setPotionEffects(p);
        setScoreboard(p);
        handleFollowers(p);
        p.getPlayer().getHandle().getBacktrackSystem().setEnabled(data.isOldCombat());
    }

    public void rideInvisibleArmorStand(Profile profile) {
        val loc = profile.getPlayer().getLocation();
        val x = loc.getX();
        val y = loc.getY();
        val z = loc.getZ();
        val handle = profile.getPlayer().getHandle();
        val entity = new EntityArmorStand(handle.getWorld(), x, y, z);
        entity.setInvisible(true);
        entity.setCustomNameVisible(false);
        entity.setGravity(false);
        val spawnArmorStand = new PacketPlayOutSpawnEntityLiving();
        spawnArmorStand.setA(entity.getId());
        spawnArmorStand.setB(30);
        spawnArmorStand.setC(MathHelper.floor(entity.locX * 32.0D));
        spawnArmorStand.setD(MathHelper.floor(entity.locY * 32.0D));
        spawnArmorStand.setE(MathHelper.floor(entity.locZ * 32.0D));
        spawnArmorStand.setI((byte) ((int) (entity.yaw * 256.0F / 360.0F)));
        spawnArmorStand.setJ((byte) ((int) (entity.pitch * 256.0F / 360.0F)));
        spawnArmorStand.setK((byte) ((int) (entity.aK * 256.0F / 360.0F)));
        spawnArmorStand.setL(entity.getDataWatcher());
        val ridingPacket = new PacketPlayOutAttachEntity(0, handle, entity);
        for (val p : getParticipants()) {
            val h = p.getPlayer().getHandle();
            h.playerConnection.sendPacket(spawnArmorStand);
            h.playerConnection.sendPacket(ridingPacket);
        }
        profile.setRidingEntityID(entity.getId());
    }

    public void destroyArmorStand(Profile profile) {
        profile.getPlayer().setFallDistance(0);
        val entityID = profile.getRidingEntityID();
        val destroyPacket = new PacketPlayOutEntityDestroy(entityID);
        for (val p : getParticipants())
            p.getPlayer().getHandle().playerConnection.sendPacket(destroyPacket);
        profile.setRidingEntityID(-1);
    }

    public void giveLoadoutSelection(Profile p) {

        val map = data.getCustomKits(p);

        p.getInventory().clear();

        if (map == null ? true : map.isEmpty())
            return;

        if (map.size() == 1) {
            p.giveKit(getKit(map.values().iterator().next()));
            return;
        }

        for (val entry : map.int2ObjectEntrySet())
            p.getInventory().setItem(entry.getIntKey(),
                    ItemStacks.LOAD_KIT.name(CC.B + CC.GOLD + "Load Kit #" + entry.getIntKey()).build(), profile -> {
                        p.giveKit(getKit(entry.getValue()));
                        return true;
                    });
    }

    public void onCountdownStart(Profile p) {
        rideInvisibleArmorStand(p);
    }

    public void onMatchStart(Profile p) {
        destroyArmorStand(p);

        if (!p.isKitLoaded())
            p.giveKit(getKit());
    }

    public void onMatchStart() {
        startMatchTimeLimit();
    }

    public void setScoreboard(Profile p) {
        p.setScoreboard(data.isBoxing() ? BoxingScoreboard.INSTANCE : InMatchScoreboard.INSTANCE);
    }

    public void increasePlacedTnt() {
        placedTnt++;
    }

    public void decreasePlacedTnt() {
        placedTnt--;
    }

    public void handleFollowers(Profile profile) {
        for (val p : profile.getSpectateHandler().getFollowers())
            p.getSpectateHandler().spectate(profile);
    }

    public void handleOpponentMessages() {
        handleOpponentMessages(profile1, profile2);
        handleOpponentMessages(profile2, profile1);
    }

    public void handleOpponentMessages(Profile profile1, Profile profile2) {
        val sb = new StringBuilder("Opponent: " + CC.AQUA + profile2.getName());
        sb.append(data.isRanked()
                ? CC.WHITE + "\nElo: " + CC.AQUA + data.getElo(profile2)
                : "");

        profile1.getPlayer().sendMessage(CC.BOARD_SEPARATOR);
        profile1.getPlayer().sendMessage(sb.toString());
        profile1.getPlayer().sendMessage(CC.BOARD_SEPARATOR);
    }

    public void setWorldParameters(World world) {
        val nmsWorld = ((CraftWorld) world).getHandle();
        nmsWorld.getWorldData().f(false);
        nmsWorld.getWorldData().setThundering(false);
        nmsWorld.getWorldData().setStorm(false);
        nmsWorld.allowMonsters = false;
    }

    public boolean noArenas() {
        boolean arenaNull = data.getArenaId() == -1;

        if (arenaNull) {
            ProfileManager.broadcast(participants, ErrorMessages.ARENA_NOT_FOUND);
            end(profile1);
        }

        return arenaNull;
    }

    public void setupLocations(Location location1, Location location2) {

        if (data.isGriefing() || data.isBuild()) {
            val arena = ArenaManager.getArenas().get(data.getArenaId());
            this.world = arena.generate();
            location1.setWorld(world);
            location2.setWorld(world);
        }

        setWorldParameters(location1.getWorld());

    }

    public void teleportPlayers(Location location1, Location location2) {
        PlayerUtil.teleport(profile1, location1);
        PlayerUtil.teleport(profile2, location2);
    }

    public void startCountdown() {
        val countdown = new Countdown(5, this);
        countdown.start();
    }

    public void start() {
        if (noArenas())
            return;

        MatchManager.registerMatch(this);
        val arena = ArenaManager.getArenas().get(data.getArenaId());
        val location1 = arena.getLocation1().clone();
        val location2 = arena.getLocation2().clone();

        setupLocations(location1, location2);
        teleportPlayers(location1, location2);

        prepareForMatch(participants);
        handleOpponentMessages();
        startCountdown();
    }

    public void end(Profile victim) {
        if (isEnded())
            return;

        ended = true;
        end(getOpponent(victim), victim);
    }

    public Profile getOpponent(Profile p) {
        val p1 = getProfile1();
        return p1.equals(p) ? getProfile2() : p1;
    }

    public boolean incrementTeamHitCount(Profile attacker, Profile victim) {
        stat(attacker, MatchStatisticCollector::increaseHitCount);
        stat(victim, MatchStatisticCollector::resetCombo);

        stat(attacker, collector -> {
            if (collector.getHitCount() >= 100 && getData().isBoxing())
                end(victim);
        });

        if (isEnded())
            return true;

        return false;
    }

    public CompletableFuture<Void> updateElo(Profile attacker, Profile victim) {
        val gametype = data.getGametype();

        if (gametype == null)
            return CompletableFuture.completedFuture(null);
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

                    val rankedMessage = CC.GREEN + attacker.getName() + " (+" + (newAttackerElo - attackerElo) + ") "
                            + CC.RED
                            + victim.getName() + " (" + (newVictimElo - victimElo) + ")";
                    attacker.getPlayer().sendMessage(rankedMessage);
                    victim.getPlayer().sendMessage(rankedMessage);
                });

    }

    public void end(Profile attacker, Profile victim) {
        stat(attacker, collector -> collector.end(true));
        stat(victim, collector -> collector.end(false));

        deathAnimation(attacker, victim);

        Bukkit.getScheduler().cancelTask(timeTaskId);

        stat(attacker, collector -> setInventoryStats(collector));
        stat(victim, collector -> setInventoryStats(collector));

        val winMessage = getWinMessage(attacker);
        val loseMessage = getLoseMessage(victim);

        for (val profile : getParticipants()) {
            profile.getPlayer().sendMessage(CC.SEPARATOR);
            profile.getPlayer().sendMessage(Strings.MATCH_RESULTS);
            profile.getPlayer().spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage);
            profile.getPlayer().sendMessage(CC.SEPARATOR);
        }

        if (data.isRanked())
            updateElo(attacker, victim);

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

        for (val spectator : getSpectators()) {
            spectator.getPlayer().sendMessage(CC.SEPARATOR);
            spectator.getPlayer().sendMessage(Strings.MATCH_RESULTS);
            spectator.getPlayer().spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage);
            spectator.getPlayer().sendMessage(CC.SEPARATOR);
            spectator.getSpectateHandler().stopSpectating();
        }

        clearWorld();
    }

    public TextComponent getWinMessage(Profile profile) {
        val winMessage = new TextComponent(CC.GREEN + " Winner: " + CC.GRAY + profile.getName());
        stat(profile, collector -> winMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(CC.GREEN + "Health Potions Remaining: "
                        + collector.getPotionsRemaining() + "\n" + CC.GREEN
                        + "Hits: " + collector.getHitCount() + "\n" + CC.GREEN + "Health: "
                        + collector.getRemainingHealth()).create())));
        winMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + profile.getName()));
        return winMessage;
    }

    public TextComponent getLoseMessage(Profile profile) {
        val loseMessage = new TextComponent(CC.RED + "Loser: " + CC.GRAY + profile.getName());
        stat(profile, collector -> loseMessage.setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(CC.RED + "Health Potions Remaining: "
                                + collector.getPotionsRemaining() + "\n"
                                + CC.RED + "Hits: " + collector.getHitCount() + "\n"
                                + CC.RED + "Health: 0")
                                .create())));
        loseMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + profile.getName()));
        return loseMessage;
    }

    public void deathAnimation(Profile attacker, Profile victim) {
        attacker.heal();
        attacker.removePotionEffects();
        attacker.getInventory().clear();
        attacker.removeFromView(victim.getUuid());
    }

    public void giveQueueAgainItem(Profile profile) {
        val queuetype = data.getQueuetype();
        val gametype = data.getGametype();

        if (queuetype == null || gametype == null)
            return;
        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE,
                () -> profile.getInventory().setItem(profile.getInventory().getHeldItemSlot(),
                        ItemStacks.QUEUE_AGAIN,
                        () -> profile.addPlayerToQueue(queuetype, gametype)),
                20);
    }

    public void sendBackToLobby(Profile profile) {
        if (!profile.getMatch().equals(this))
            return;
        profile.teleportToLobby();
        profile.getInventory().setInventoryForLobby();
        profile.removeFromMatch();
    }

    public void resetPearlCooldown(Profile... profiles) {
        for (val profile : profiles) {
            pearlCooldown.getCooldowns().removeInt(profile.getUuid());
            profile.getPlayer().setLevel(0);
        }
    }

    public void clearItems() {
        boolean arenaInUse = false;

        for (val match : MatchManager.getMatches()) {
            if (!match.isEnded() && match.getData().getArenaId() == data.getArenaId()) {
                arenaInUse = true;
                break;
            }
        }

        val arena = ArenaManager.getArenas().get(data.getArenaId());

        for (val item : arenaInUse ? itemRemovalQueue
                : arena.getLocation1().getWorld().getEntitiesByClass(Item.class))
            item.remove();

        for (val arrow : arena.getLocation1().getWorld().getEntitiesByClass(Arrow.class)) {
            val shooter = arrow.getShooter();

            if (shooter instanceof Player pShooter) {
                val profile = ProfileManager.getProfile(pShooter.getUniqueId());

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

            for (val location : buildLog)
                location.getBlock().setType(Material.AIR);

        }, getPostMatchTime() + 1);
    }

    public InventoryStatsMenu setInventoryStats(MatchStatisticCollector matchStatisticCollector) {

        val profile = matchStatisticCollector.getProfile();

        val menu = new InventoryStatsMenu(getOpponent(profile).getName(),
                matchStatisticCollector);

        if (!(this instanceof TeamMatch))
            ProfileManager.setInventoryStats(profile, menu);

        return menu;
    }

    public void addParicipants(Profile... players) {
        participants.addAll(Arrays.asList(players));
    }

    public ProfileList getTeam(Profile profile, boolean alive) {
        return new ProfileList(Collections.singletonList(profile));
    }
}
