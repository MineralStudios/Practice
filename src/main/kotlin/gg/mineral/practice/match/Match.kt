package gg.mineral.practice.match

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.appender.PlayerAppender
import gg.mineral.practice.inventory.menus.InventoryStatsMenu
import gg.mineral.practice.kit.Kit
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.MatchManager.registerMatch
import gg.mineral.practice.managers.MatchManager.remove
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.practice.managers.ProfileManager.setInventoryStats
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.match.data.MatchStatisticCollector
import gg.mineral.practice.queue.QueueSystem
import gg.mineral.practice.scoreboard.impl.BoxingScoreboard
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard
import gg.mineral.practice.scoreboard.impl.InMatchScoreboard
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard
import gg.mineral.practice.traits.Spectatable
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.math.Countdown
import gg.mineral.practice.util.math.MathUtil
import gg.mineral.practice.util.math.PearlCooldown
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ErrorMessages
import gg.mineral.practice.util.messages.impl.Strings
import gg.mineral.practice.util.messages.impl.TextComponents
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer
import java.util.function.Function
import kotlin.math.log10
import kotlin.math.max

open class Match(
    open val data: MatchData,
    var profile1: Profile? = null,
    var profile2: Profile? = null
) : Spectatable, PlayerAppender {
    override var spectators: ConcurrentLinkedDeque<Profile> = ConcurrentLinkedDeque()
    final override val participants: ProfileList = ProfileList()
    override var ended: Boolean = false
    var placedTnt: Int = 0
    var buildLog: GlueList<Location> = GlueList()
    var itemRemovalQueue: Queue<Item> = ConcurrentLinkedQueue()

    override val world: World by lazy { this.generateWorld() }
    private val matchStatisticMap: MutableMap<UUID, MatchStatisticCollector> = Object2ObjectOpenHashMap()
    protected var timeRemaining: Int = 0
    protected var timeTaskId: Int = 0

    init {
        if (profile1 != null && profile2 != null)
            addParticipants(profile1!!, profile2!!)
    }

    val buildLimit: Int
        get() {
            val arena = arenas[data.arenaId] ?: return 0

            val maxSpawnY = max(arena.location1.y, arena.location2.y).toInt()

            return maxSpawnY + (data.gametype?.buildLimit ?: 0)
        }

    open fun generateWorld(): World {
        val arena = arenas[data.arenaId]
        return arena.generate()
    }

    fun prepareForMatch(profiles: ProfileList) {
        for (profile in profiles) prepareForMatch(profile)
    }

    protected val timeLimitSec: Int
        get() {
            val mins = 5 * log10((5 * participants.size).toDouble())
            return (mins * 60).toInt()
        }

    protected open fun startMatchTimeLimit() {
        this.timeRemaining = this.timeLimitSec
        this.timeTaskId = Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(
            PracticePlugin.INSTANCE,
            {
                if (ended) return@scheduleSyncRepeatingTask
                if (timeRemaining-- <= 0) profile1?.let { end(it) }
            }, 0, 20
        )
    }

    private fun getKit(customKit: Array<ItemStack?>): Kit {
        val kit = kit
        kit.contents = customKit
        return kit
    }

    val kit: Kit
        get() = Kit(data.kit)

    fun stat(uuid: UUID, consumer: Consumer<MatchStatisticCollector>) {
        if (ended) return

        val collector = matchStatisticMap.computeIfAbsent(
            uuid
        ) { _: UUID? ->
            val profile: Profile = this.participants.get(uuid) ?: throw IllegalStateException("Profile is null")
            MatchStatisticCollector(profile)
        }
        consumer.accept(collector)
    }

    protected fun stat(profile: Profile, consumer: Consumer<MatchStatisticCollector>) {
        val collector = matchStatisticMap.computeIfAbsent(
            profile.uuid
        ) { _: UUID? -> MatchStatisticCollector(profile) }
        consumer.accept(collector)
    }

    fun <T> computeStat(uuid: UUID, function: Function<MatchStatisticCollector, T>): T {
        val collector = matchStatisticMap.computeIfAbsent(
            uuid
        ) {
            val profile: Profile =
                this.participants.get(it) ?: throw IllegalArgumentException("Profile could not be found.")
            MatchStatisticCollector(profile)
        }
        return function.apply(collector)
    }

    private fun setAttributes(p: Profile) {
        stat(p) { it.clearHitCount() }
        p.dead = false
        p.player.maximumNoDamageTicks = data.noDamageTicks
        p.player.setKnockback(data.knockback)
        p.inventory.inventoryClickCancelled = false
        p.player.saturation = 20f
        p.player.foodLevel = 20
    }

    private fun setPotionEffects(p: Profile) {
        if (!data.damage) p.player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 255))

        if (data.boxing) p.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 999999999, 1))
    }

    fun prepareForMatch(p: Profile) {
        QueueSystem.removePlayerFromQueue(p)

        val currentMatch = p.match
        p.match = this

        if (currentMatch != null && !currentMatch.ended) currentMatch.end(p)

        p.recievedDuelRequests.clear()
        stat(p) { obj: MatchStatisticCollector -> obj.start() }
        p.kitLoaded = false

        giveLoadoutSelection(p)
        setAttributes(p)
        setPotionEffects(p)
        setScoreboard(p)
        handleFollowers(p)
        p.player.handle.backtrackSystem.isEnabled = data.oldCombat
    }

    private fun rideInvisibleArmorStand(profile: Profile) {
        val loc = profile.player.location
        val x = loc.x
        val y = loc.y
        val z = loc.z
        val handle = profile.player.handle
        val entity = EntityArmorStand(handle.getWorld(), x, y, z)
        entity.isInvisible = true
        entity.customNameVisible = false
        entity.setGravity(false)
        val spawnArmorStand = PacketPlayOutSpawnEntityLiving()
        spawnArmorStand.a = entity.id
        spawnArmorStand.b = 30
        spawnArmorStand.c = MathHelper.floor(entity.locX * 32.0)
        spawnArmorStand.d = MathHelper.floor(entity.locY * 32.0)
        spawnArmorStand.e = MathHelper.floor(entity.locZ * 32.0)
        spawnArmorStand.i = ((entity.yaw * 256.0f / 360.0f).toInt()).toByte()
        spawnArmorStand.j = ((entity.pitch * 256.0f / 360.0f).toInt()).toByte()
        spawnArmorStand.k = ((entity.aK * 256.0f / 360.0f).toInt()).toByte()
        spawnArmorStand.l = entity.dataWatcher
        val ridingPacket = PacketPlayOutAttachEntity(0, handle, entity)
        for (p in participants) {
            val h = p.player.handle
            h.playerConnection.sendPacket(spawnArmorStand)
            h.playerConnection.sendPacket(ridingPacket)
        }
        profile.ridingEntityID = entity.id
    }

    private fun destroyArmorStand(profile: Profile) {
        profile.player.fallDistance = 0f
        val entityID = profile.ridingEntityID
        val destroyPacket = PacketPlayOutEntityDestroy(entityID)
        for (p in participants) p.player.handle.playerConnection.sendPacket(destroyPacket)
        profile.ridingEntityID = -1
    }

    private fun giveLoadoutSelection(profile: Profile) {
        val map = data.getCustomKits(profile)

        profile.inventory.clear()

        if (map.isNullOrEmpty()) return

        if (map.size == 1) {
            profile.giveKit(getKit(map.values.iterator().next()))
            return
        }

        for (entry in map.int2ObjectEntrySet()) profile.inventory.setItem(
            entry.intKey,
            ItemStacks.LOAD_KIT.name(CC.B + CC.GOLD + "Load Kit #" + entry.intKey).build()
        ) { p: Profile ->
            p.giveKit(getKit(entry.value))
            true
        }
    }

    fun onCountdownStart(p: Profile) = rideInvisibleArmorStand(p)

    fun onMatchStart(p: Profile) {
        destroyArmorStand(p)
        if (!p.kitLoaded) p.giveKit(kit)
    }

    open fun onMatchStart() = startMatchTimeLimit()

    open fun setScoreboard(p: Profile) {
        p.scoreboard = if (data.boxing) BoxingScoreboard.INSTANCE else InMatchScoreboard.INSTANCE
    }

    fun increasePlacedTnt() = placedTnt++

    fun decreasePlacedTnt() = placedTnt--

    private fun handleFollowers(profile: Profile) {
        for (p in profile.followers) p.spectate(profile)
    }

    fun handleOpponentMessages() {
        profile1?.let { profile2?.let { it1 -> handleOpponentMessages(it, it1) } }
        profile2?.let { profile1?.let { it1 -> handleOpponentMessages(it, it1) } }
    }

    private fun handleOpponentMessages(profile1: Profile, profile2: Profile) {
        val sb = "Opponent: " + CC.AQUA + profile2.name + (if (data.ranked)
            """
                ${CC.WHITE}
                Elo: ${CC.AQUA}${data.getElo(profile2)}
                """.trimIndent()
        else
            "")

        profile1.player.sendMessage(CC.BOARD_SEPARATOR)
        profile1.player.sendMessage(sb)
        profile1.player.sendMessage(CC.BOARD_SEPARATOR)
    }

    private fun setWorldParameters(world: World) {
        val nmsWorld = (world as CraftWorld).handle
        nmsWorld.getWorldData().f(false)
        nmsWorld.getWorldData().isThundering = false
        nmsWorld.getWorldData().setStorm(false)
        nmsWorld.allowMonsters = false
    }

    fun noArenas(): Boolean {
        val arenaNull = data.arenaId.toInt() == -1

        if (arenaNull) {
            broadcast(participants, ErrorMessages.ARENA_NOT_FOUND)
            profile1?.let { end(it) }
        }

        return arenaNull
    }

    fun setupLocations(location1: Location, location2: Location?) = setWorldParameters(location1.world)

    open fun teleportPlayers(location1: Location, location2: Location) {
        profile1?.let { PlayerUtil.teleport(it, location1) }
        profile2?.let { PlayerUtil.teleport(it, location2) }
    }

    fun startCountdown() = Countdown(5, this).start()

    open fun start() {
        if (noArenas()) return
        if (!registerMatch(this)) return
        val arena = arenas[data.arenaId]
        val location1 = arena.location1.bukkit(world)
        val location2 = arena.location2.bukkit(world)

        setupLocations(location1, location2)
        teleportPlayers(location1, location2)

        prepareForMatch(participants)
        handleOpponentMessages()
        startCountdown()
    }

    open fun end(victim: Profile) {
        if (ended) return

        ended = true
        getOpponent(victim)?.let { end(it, victim) }
    }

    open fun getOpponent(p: Profile): Profile? {
        return if (profile1?.equals(p) == true) profile2 else profile1
    }

    open fun incrementTeamHitCount(attacker: Profile, victim: Profile): Boolean {
        stat(attacker) { obj: MatchStatisticCollector -> obj.increaseHitCount() }
        stat(victim) { obj: MatchStatisticCollector -> obj.resetCombo() }
        stat(attacker) { if (it.hitCount >= 100 && data.boxing) end(victim) }
        return ended
    }

    private fun updateElo(attacker: Profile, victim: Profile) {
        val gametype = data.gametype ?: return

        gametype.getEloMap(attacker, victim)
            .thenAccept { map: Object2IntOpenHashMap<UUID> ->
                val attackerElo = map.getInt(attacker.uuid)
                val victimElo = map.getInt(victim.uuid)
                val newAttackerElo = MathUtil.getNewRating(attackerElo, victimElo, true)
                val newVictimElo = MathUtil.getNewRating(victimElo, attackerElo, false)

                gametype.setElo(newAttackerElo, attacker)
                gametype.setElo(newVictimElo, victim)
                gametype.updatePlayerLeaderboard(victim, newVictimElo, victimElo)
                gametype.updatePlayerLeaderboard(attacker, newAttackerElo, attackerElo)

                val rankedMessage = (CC.GREEN + attacker.name + " (+" + (newAttackerElo - attackerElo) + ") "
                        + CC.RED
                        + victim.name + " (" + (newVictimElo - victimElo) + ")")
                attacker.player.sendMessage(rankedMessage)
                victim.player.sendMessage(rankedMessage)
            }
    }

    open fun end(attacker: Profile, victim: Profile) {
        stat(attacker) { it.end(true) }
        stat(victim) { it.end(false) }

        deathAnimation(attacker, victim)

        Bukkit.getScheduler().cancelTask(timeTaskId)

        stat(
            attacker
        ) { matchStatisticCollector: MatchStatisticCollector ->
            this.setInventoryStats(
                matchStatisticCollector
            )
        }
        stat(
            victim
        ) { matchStatisticCollector: MatchStatisticCollector ->
            this.setInventoryStats(
                matchStatisticCollector
            )
        }

        val winMessage = getWinMessage(attacker)
        val loseMessage = getLoseMessage(victim)

        for (profile in participants) {
            profile.player.sendMessage(CC.SEPARATOR)
            profile.player.sendMessage(Strings.MATCH_RESULTS)
            profile.player.spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage)
            profile.player.sendMessage(CC.SEPARATOR)
        }

        if (data.ranked) updateElo(attacker, victim)

        resetPearlCooldown(attacker, victim)
        attacker.scoreboard = MatchEndScoreboard.INSTANCE
        victim.scoreboard = DefaultScoreboard.INSTANCE
        remove(this)

        victim.player.heal()
        victim.player.removePotionEffects()

        sendBackToLobby(victim)

        giveQueueAgainItem(attacker)

        Bukkit.getServer().scheduler.runTaskLater(PracticePlugin.INSTANCE, {
            if (attacker.match?.ended == false) return@runTaskLater
            attacker.scoreboard = DefaultScoreboard.INSTANCE
            sendBackToLobby(attacker)
        }, POST_MATCH_TIME.toLong())

        for (spectator in spectators) {
            spectator.player.sendMessage(CC.SEPARATOR)
            spectator.player.sendMessage(Strings.MATCH_RESULTS)
            spectator.player.spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage)
            spectator.player.sendMessage(CC.SEPARATOR)
            spectator.stopSpectating()
        }

        clearWorld()
    }

    fun getWinMessage(profile: Profile): TextComponent {
        val winMessage = TextComponent(CC.GREEN + " Winner: " + CC.GRAY + profile.name)
        stat(profile) { collector: MatchStatisticCollector ->
            winMessage.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder(
                    ("""
                        ${CC.GREEN}Health Potions Remaining: ${collector.potionsRemaining}
                        ${CC.GREEN}Hits: ${collector.hitCount}
                        ${CC.GREEN}Health: ${collector.remainingHealth}
                        """.trimIndent())
                ).create()
            )
        }
        winMessage.clickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + profile.name)
        return winMessage
    }

    fun getLoseMessage(profile: Profile): TextComponent {
        val loseMessage = TextComponent(CC.RED + "Loser: " + CC.GRAY + profile.name)
        stat(profile) { collector: MatchStatisticCollector ->
            loseMessage.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder(
                    ("""
                        ${CC.RED}Health Potions Remaining: ${collector.potionsRemaining}
                        ${CC.RED}Hits: ${collector.hitCount}
                        ${CC.RED}Health: 0
                        """.trimIndent())
                )
                    .create()
            )
        }
        loseMessage.clickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + profile.name)
        return loseMessage
    }

    fun deathAnimation(attacker: Profile, victim: Profile) {
        attacker.player.heal()
        attacker.player.removePotionEffects()
        attacker.inventory.clear()
        attacker.player.hidePlayer(victim.player, false)
    }

    open fun giveQueueAgainItem(profile: Profile) {
        val queuetype = data.queuetype
        val gametype = data.gametype

        if (queuetype == null || gametype == null) return
        Bukkit.getServer().scheduler.runTaskLater(
            PracticePlugin.INSTANCE,
            {
                profile.inventory.setItem(
                    profile.inventory.heldItemSlot,
                    ItemStacks.QUEUE_AGAIN,
                    Runnable { profile.addPlayerToQueue(queuetype, gametype) })
            },
            20
        )
    }

    fun sendBackToLobby(profile: Profile) {
        if (profile.match != this) return
        profile.teleportToLobby()
        profile.inventory.setInventoryForLobby()
        profile.match = null
    }

    fun resetPearlCooldown(vararg profiles: Profile) {
        for (profile in profiles) {
            pearlCooldown.cooldowns.removeInt(profile.uuid)
            profile.player.level = 0
        }
    }

    fun clearWorld() {
        Bukkit.getServer().scheduler.runTaskLater(
            PracticePlugin.INSTANCE,
            { Bukkit.unloadWorld(world, false) },
            (POST_MATCH_TIME + 1).toLong()
        )
    }

    fun setInventoryStats(matchStatisticCollector: MatchStatisticCollector): InventoryStatsMenu {
        val profile = matchStatisticCollector.profile

        val menu = InventoryStatsMenu(
            getOpponent(profile)?.name,
            matchStatisticCollector
        )

        if (this !is TeamMatch) setInventoryStats(profile, menu)

        return menu
    }

    fun addParticipants(vararg players: Profile) = participants.addAll(listOf(*players))

    open fun getTeam(profile: Profile, alive: Boolean) = ProfileList(listOf(profile))

    companion object {
        const val POST_MATCH_TIME: Int = 60
        var pearlCooldown: PearlCooldown = PearlCooldown()
    }
}
