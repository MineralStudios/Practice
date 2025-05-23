package gg.mineral.practice.match

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.battle.Battle
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
import gg.mineral.server.combat.KnockbackProfileList
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
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer
import java.util.function.Function
import kotlin.math.log10
import kotlin.math.max

open class Match(
    open val data: MatchData,
    var profile1: Profile? = null,
    var profile2: Profile? = null
) : Spectatable, Battle, PlayerAppender {
    override var spectators: ProfileList = ProfileList()
    final override val participants: ProfileList = ProfileList()
    override var ended: Boolean = false
    var placedTnt: Int = 0
    var buildLog: GlueList<Location> = GlueList()
    var itemRemovalQueue: Queue<Item> = ConcurrentLinkedQueue()
    override val world: WeakReference<World> by lazy { this.generateWorld() }
    private val matchStatisticMap: MutableMap<UUID, MatchStatisticCollector> = Object2ObjectOpenHashMap()
    protected var timeRemaining: Int = 0
    protected var timeTaskId: Int = 0
    val kit: Kit
        get() = Kit(data.kit)
    protected val timeLimitSec by lazy {
        val mins = 30 * log10((5 * participants.size).toDouble())
        (mins * 60).toInt()
    }

    init {
        profile1?.let { addParticipants(it) }
        profile2?.let { addParticipants(it) }
    }

    open fun cleanup() {
        participants.clear()
        spectators.clear()
        matchStatisticMap.clear()
        buildLog.clear()
        itemRemovalQueue.clear()

        Bukkit.getScheduler().cancelTask(timeTaskId)
        clearWorld()
    }

    val buildLimit: Int
        get() {
            val arena = arenas[data.arenaId] ?: return 0

            val maxSpawnY = max(arena.location1.y, arena.location2.y).toInt()

            return maxSpawnY + (data.gametype?.buildLimit ?: 0)
        }

    open fun generateWorld(): WeakReference<World> {
        val arena = arenas[data.arenaId] ?: throw NullPointerException("Arena not found")
        return arena.generate()
    }

    fun prepareForMatch(profiles: ProfileList) {
        for (profile in profiles) prepareForMatch(profile)
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

    fun stat(uuid: UUID, consumer: Consumer<MatchStatisticCollector>) {
        if (ended) return

        val collector = matchStatisticMap.computeIfAbsent(
            uuid
        ) {
            val profile: Profile = this.participants.get(uuid) ?: error("Profile is null")
            MatchStatisticCollector(profile)
        }
        consumer.accept(collector)
    }

    protected fun stat(profile: Profile, consumer: Consumer<MatchStatisticCollector>) {
        val collector = matchStatisticMap.computeIfAbsent(
            profile.uuid
        ) { MatchStatisticCollector(profile) }
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

    protected fun onError(message: String) {
        participants.mapNotNull { it.player }.forEach {
            it.kill()
            it.sendMessage(CC.RED + message)
        }
    }

    private fun setAttributes(p: Profile) {
        stat(p) { it.clearHitCount() }
        p.dead = false
        p.player?.let {
            it.maximumNoDamageTicks = data.noDamageTicks
            it.setKnockback(
                data.knockback
                    ?: if (data.noDamageTicks <= 10) KnockbackProfileList.getComboKnockbackProfile() else KnockbackProfileList.getDefaultKnockbackProfile()
            )
            it.setBacktrack(data.oldCombat)
            it.setKnockbackSync(!data.oldCombat)
            it.saturation = 20f
            it.foodLevel = 20
        }

        p.inventory.inventoryClickCancelled = false
    }

    private fun setPotionEffects(p: Profile) {
        if (!data.damage) p.player
            ?.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 255))

        if (data.boxing) p.player?.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 999999999, 1))
    }

    fun prepareForMatch(p: Profile) {
        QueueSystem.removePlayerFromQueue(p)

        val currentMatch = p.match
        p.match = this

        if (currentMatch != null && !currentMatch.ended) currentMatch.end(p)

        p.recievedDuelRequests.clear()
        stat(p) { it.start() }
        p.kitLoaded = false

        p.player?.closeInventory()
        giveLoadoutSelection(p)
        setAttributes(p)
        setPotionEffects(p)
        setScoreboard(p)
        handleFollowers(p)
    }

    private fun rideInvisibleArmorStand(profile: Profile) {
        val loc = profile.player?.location ?: return
        val x = loc.x
        val y = loc.y
        val z = loc.z
        val handle = profile.player?.handle ?: return
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
            val h = p.player?.handle ?: continue
            h.playerConnection.sendPacket(spawnArmorStand)
            h.playerConnection.sendPacket(ridingPacket)
        }
        profile.ridingEntityID = entity.id
    }

    private fun destroyArmorStand(profile: Profile) {
        profile.player?.fallDistance = 0f
        val entityID = profile.ridingEntityID
        val destroyPacket = PacketPlayOutEntityDestroy(entityID)
        for (p in participants) p.player?.handle?.playerConnection?.sendPacket(destroyPacket)
        profile.ridingEntityID = -1
    }

    private fun giveLoadoutSelection(profile: Profile) {
        val map = data.getCustomKits(profile)

        profile.inventory.clear()

        if (map.isEmpty()) return

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

    override fun onCountdownStart(profile: Profile) = rideInvisibleArmorStand(profile)

    override fun onStart(profile: Profile) {
        destroyArmorStand(profile)
        if (!profile.kitLoaded) profile.giveKit(kit)
    }

    override fun onStart() = startMatchTimeLimit()

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

    private fun handleRankedOpponentMessages(): CompletableFuture<Void> = CompletableFuture.allOf(
        profile1?.let { profile2?.let { it1 -> handleRankedOpponentMessages(it, it1) } },
        profile2?.let { profile1?.let { it1 -> handleRankedOpponentMessages(it, it1) } })

    private fun handleRankedOpponentMessages(profile1: Profile, profile2: Profile): CompletableFuture<Void> =
        data.getElo(profile2).thenAccept {
            profile1.player?.let {
                it.sendMessage(CC.BOARD_SEPARATOR)
                it.sendMessage("Opponent: " + CC.AQUA + profile2.name)
                it.sendMessage(CC.WHITE + "Elo: " + CC.AQUA + it)
                it.sendMessage(CC.BOARD_SEPARATOR)
            }
        }

    private fun handleOpponentMessages(profile1: Profile, profile2: Profile) {
        check(!data.ranked) { "Ranked matches are not supported." }

        profile1.player?.let {
            it.sendMessage(CC.BOARD_SEPARATOR)
            it.sendMessage("Opponent: " + CC.AQUA + profile2.name)
            it.sendMessage(CC.BOARD_SEPARATOR)
        }
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
        val arena = arenas[data.arenaId] ?: throw NullPointerException("Arena not found")
        val location1 = arena.location1.bukkit(world) ?: return
        val location2 = arena.location2.bukkit(world) ?: return

        setupLocations(location1, location2)
        teleportPlayers(location1, location2)

        prepareForMatch(participants)

        if (data.ranked) handleRankedOpponentMessages().thenAccept { startCountdown() }
        else {
            handleOpponentMessages()
            startCountdown()
        }
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
        stat(attacker) { it.increaseHitCount() }
        stat(victim) { it.resetCombo() }
        stat(attacker) { if (it.hitCount >= 100 && data.boxing) end(victim) }
        return ended
    }

    private fun updateElo(attacker: Profile, victim: Profile): CompletableFuture<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val gametype =
            data.gametype ?: return CompletableFuture.completedFuture(Pair(Pair(1000, 1000), Pair(1000, 1000)))

        return gametype.getEloMap(attacker, victim)
            .thenApply {
                val attackerElo = it.getInt(attacker.uuid)
                val victimElo = it.getInt(victim.uuid)
                val newAttackerElo = MathUtil.getNewRating(attackerElo, victimElo, true)
                val newVictimElo = MathUtil.getNewRating(victimElo, attackerElo, false)

                gametype.setElo(newAttackerElo, attacker)
                gametype.setElo(newVictimElo, victim)
                gametype.updatePlayerLeaderboard(victim, newVictimElo, victimElo)
                gametype.updatePlayerLeaderboard(attacker, newAttackerElo, attackerElo)

                val oldPair = Pair(attackerElo, victimElo)
                val newPair = Pair(newAttackerElo, newVictimElo)

                Pair(oldPair, newPair)
            }
    }

    open fun end(attacker: Profile, victim: Profile) {
        stat(attacker) { it.end(true) }
        stat(victim) { it.end(false) }

        deathAnimation(attacker, victim)

        Bukkit.getScheduler().cancelTask(timeTaskId)

        stat(
            attacker
        ) {
            this.setInventoryStats(
                it
            )
        }
        stat(
            victim
        ) {
            this.setInventoryStats(
                it
            )
        }

        val winMessage = getWinMessage(attacker)
        val loseMessage = getLoseMessage(victim)

        fun printMessagesAndStopSpectators(eloMessage: TextComponent? = null, eloMessage2: TextComponent? = null) {
            for (profile in participants) {
                profile.player?.let {
                    it.sendMessage(CC.SEPARATOR)
                    it.sendMessage(Strings.MATCH_RESULTS)
                    if (data.ranked && eloMessage != null && eloMessage2 != null)
                        it.spigot()
                            ?.sendMessage(winMessage, eloMessage, TextComponents.SPLITTER, loseMessage, eloMessage2)
                    else
                        it.spigot()?.sendMessage(winMessage, TextComponents.SPLITTER, loseMessage)

                    if (this !is BotMatch && this !is TeamMatch)
                        getOpponent(profile)?.let { opponent ->
                            it.sendMessage(" ")
                            it.spigot()?.sendMessage(getRematchMessage(opponent))
                        }
                    it.sendMessage(CC.SEPARATOR)
                }
            }

            for (spectator in spectators) {
                spectator.player?.let {
                    it.sendMessage(CC.SEPARATOR)
                    it.sendMessage(Strings.MATCH_RESULTS)
                    if (data.ranked && eloMessage != null && eloMessage2 != null)
                        it.spigot()
                            ?.sendMessage(winMessage, eloMessage, TextComponents.SPLITTER, loseMessage, eloMessage2)
                    else
                        it.spigot()?.sendMessage(winMessage, TextComponents.SPLITTER, loseMessage)
                    it.sendMessage(CC.SEPARATOR)
                }

                spectator.stopSpectating()
            }
        }

        if (data.ranked) {
            updateElo(attacker, victim).thenAccept {
                val oldPair = it.first
                val newPair = it.second

                val attackerEloDiff = newPair.first - oldPair.first
                val victimEloDiff = newPair.second - oldPair.second

                val eloMessage = TextComponent(
                    CC.GREEN + " +" + attackerEloDiff + CC.GRAY + " (" + CC.GREEN + newPair.first + CC.GRAY + ")"
                )

                val eloMessage2 = TextComponent(
                    CC.RED + " " + victimEloDiff + CC.GRAY + " (" + CC.RED + newPair.second + CC.GRAY + ")"
                )

                printMessagesAndStopSpectators(eloMessage, eloMessage2)
            }
        } else printMessagesAndStopSpectators()

        resetPearlCooldown(attacker, victim)
        attacker.scoreboard = MatchEndScoreboard.INSTANCE
        victim.scoreboard = DefaultScoreboard.INSTANCE
        remove(this)

        victim.player?.let {
            it.heal()
            it.removePotionEffects()
        }

        sendBackToLobby(victim)

        giveQueueAgainItem(attacker)

        Bukkit.getServer().scheduler.runTaskLater(PracticePlugin.INSTANCE, {
            if (attacker.match?.ended == false) return@runTaskLater
            attacker.scoreboard = DefaultScoreboard.INSTANCE
            sendBackToLobby(attacker)
        }, POST_MATCH_TIME.toLong())

        cleanup()
    }

    fun getWinMessage(profile: Profile): TextComponent {
        val winMessage = TextComponent(CC.GREEN + " Winner: " + CC.GRAY + profile.name)
        stat(profile) {
            winMessage.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder(
                    ("""
                        ${CC.GREEN}Health Potions Remaining: ${it.potionsRemaining}
                        ${CC.GREEN}Hits: ${it.hitCount}
                        ${CC.GREEN}Health: ${it.remainingHealth}
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
        stat(profile) {
            loseMessage.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder(
                    ("""
                        ${CC.RED}Health Potions Remaining: ${it.potionsRemaining}
                        ${CC.RED}Hits: ${it.hitCount}
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

    private fun getRematchMessage(opponent: Profile): TextComponent {
        fun centerString(baseLength: Int, insert: String): String {
            // Calculate the spaces needed to center the string
            val totalPadding = baseLength - insert.length
            if (totalPadding <= 0) return insert // If the insert string is longer, return it as is

            val paddingStart = totalPadding / 2
            val paddingEnd = totalPadding - paddingStart

            // Add spaces to center the string
            return "\u2064".repeat(paddingStart) + insert + "\u2064".repeat(paddingEnd)
        }

        val message = TextComponent(CC.GREEN + CC.B + centerString(CC.SPLITTER.length, "Click To Rematch"))
        message.clickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel " + opponent.name)
        message.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder(
                ("""
                    ${CC.GREEN}Click to send a rematch request to ${opponent.name}
                        """.trimIndent())
            )
                .create()
        )
        return message
    }

    fun deathAnimation(attacker: Profile, victim: Profile) {
        attacker.player?.let {
            it.heal()
            it.removePotionEffects()
            victim.player?.apply { it.hidePlayer(this, false) }
        }

        attacker.inventory.clear()
    }

    open fun giveQueueAgainItem(profile: Profile) {
        val queuetype = data.queuetype
        val gametype = data.gametype

        Bukkit.getServer().scheduler.runTaskLater(
            PracticePlugin.INSTANCE,
            {
                if (profile.party?.isPartyLeader(profile) == false) return@runTaskLater

                if (queuetype == null || gametype == null) {
                    getOpponent(profile)?.let {
                        profile.inventory.setItem(
                            profile.inventory.heldItemSlot,
                            ItemStacks.REMATCH,
                            Runnable {
                                profile.duelSettings = data.deriveDuelSettings()

                                val party1 = profile.party
                                val party2 = getOpponent(profile)?.party

                                if (party1 != null && party2 != null && party1 == party2) {
                                    val partyMatch = TeamMatch(party1, MatchData(profile.duelSettings))
                                    partyMatch.start()
                                    return@Runnable
                                }

                                profile.sendDuelRequest(it)
                            })

                    }
                    return@runTaskLater
                }

                profile.inventory.setItem(
                    profile.inventory.heldItemSlot,
                    ItemStacks.QUEUE_AGAIN,
                    Runnable { profile.addPlayerToQueue(queuetype, gametype) })
            },
            20
        )
    }

    open fun sendBackToLobby(profile: Profile) {
        if (profile.match != this) return
        profile.teleportToLobby()
        profile.inventory.setInventoryForLobby()
        profile.match = null
    }

    fun resetPearlCooldown(vararg profiles: Profile) {
        for (profile in profiles) {
            pearlCooldown.cooldowns.removeInt(profile.uuid)
            profile.player?.level = 0
        }
    }

    private fun clearWorld() {
        Bukkit.getServer().scheduler.runTaskLater(
            PracticePlugin.INSTANCE,
            { world.get()?.let { Bukkit.unloadWorld(it, false) } },
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
