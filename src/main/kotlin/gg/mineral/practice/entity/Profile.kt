package gg.mineral.practice.entity

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.arena.SpectatableArena
import gg.mineral.practice.contest.Contest
import gg.mineral.practice.duel.DuelSettings
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.events.Event
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.inventory.PlayerInventory
import gg.mineral.practice.inventory.SubmitAction
import gg.mineral.practice.inventory.menus.MechanicsMenu
import gg.mineral.practice.kit.Kit
import gg.mineral.practice.kit.KitCreator
import gg.mineral.practice.kit.KitEditor
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.practice.managers.ProfileManager.getProfile
import gg.mineral.practice.managers.ProfileManager.isFake
import gg.mineral.practice.managers.ProfileManager.lobbyLocation
import gg.mineral.practice.managers.ProfileManager.playerConfig
import gg.mineral.practice.managers.ProfileManager.setBacktrack
import gg.mineral.practice.match.Match
import gg.mineral.practice.match.TeamMatch
import gg.mineral.practice.party.Party
import gg.mineral.practice.queue.QueueSettings
import gg.mineral.practice.queue.QueueSystem
import gg.mineral.practice.queue.QueuedEntity
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.request.DuelRequest
import gg.mineral.practice.scoreboard.ScoreboardHandler
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard
import gg.mineral.practice.scoreboard.impl.FollowingScoreboard
import gg.mineral.practice.scoreboard.impl.SpectatorScoreboard
import gg.mineral.practice.tournaments.Tournament
import gg.mineral.practice.traits.Spectatable
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.collection.AutoExpireList
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.collection.Registry
import gg.mineral.practice.util.messages.Message
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import gg.mineral.practice.util.world.BlockData
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Consumer
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo as PlayerInfoPacket
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction as InfoAction

class Profile(player: Player) : ExtendedProfileData(player.name, player.uniqueId),
    QueuedEntity, CommandSenderAppender {
    override val profiles = LinkedList(listOf(this))
    private var nameCache = name
    override val name: String
        get() {
            nameCache = player?.name ?: nameCache
            return nameCache
        }
    val player get() = Bukkit.getPlayer(uuid) as CraftPlayer?
    val inventory: PlayerInventory
    var match: Match? = null
        set(value) {
            field = value
            playerStatus = if (value != null) PlayerStatus.FIGHTING
            else PlayerStatus.IDLE
            if (value == null) this.inCountdown = false
        }

    var scoreboard = DefaultScoreboard.INSTANCE
    private var scoreboardHandler: ScoreboardHandler
    val taskIds = LinkedList<Int>()
    override val queueSettings = QueueSettings()
    var duelSettings: DuelSettings
    var playersVisible = true
    var partyOpenCooldown = false
    var scoreboardEnabled = true
        set(value) {
            field = value
            if (value) scoreboardHandler = player?.let { ScoreboardHandler(it) } ?: return
            else scoreboardHandler.delete()
        }
    var dead: Boolean = false
    var openMenu: Menu? = null
    var playerStatus = PlayerStatus.IDLE
        set(value) {
            player?.gameMode = value.gameMode
            val canFly = value.canFly(this)

            this.player?.allowFlight = canFly
            this.player?.isFlying = canFly

            field = value

            this.updateVisibility()
        }
    var party: Party? = null
        set(value) {
            field?.remove(this)
            if (value?.add(this) == true) inventory.setInventoryForParty()
            else if (playerStatus == PlayerStatus.IDLE) inventory.setInventoryForLobby()
            field = value
        }
    var kitEditor: KitEditor? = null
        set(value) {
            if (field == value) return
            if (value == null) {
                scoreboard = DefaultScoreboard.INSTANCE
                inventory.inventoryClickCancelled = true
                teleportToLobby()
                inventory.setInventoryForLobby()
            }
            field = value
        }
    var kitCreator: KitCreator? = null
        set(value) {
            if (field == value) return
            if (value == null) {
                scoreboard = DefaultScoreboard.INSTANCE
                inventory.inventoryClickCancelled = true
                teleportToLobby()
                inventory.setInventoryForLobby()
            }
            field?.submitAction?.let { openMenu(MechanicsMenu(null, it)) }
            field = value
        }
    var contest: Contest? = null
        set(value) {
            if (field == value) return
            if (value != null) when (value) {
                is Tournament -> inventory.setInventoryForTournament()
                is Event -> inventory.setInventoryForEvent()
            }
            else {
                field?.removePlayer(this)

                if (match == null || match?.ended == false) {
                    teleportToLobby()
                    inventory.setInventoryForLobby()
                }
            }
            field = value
        }
    var killer: Profile? = null
    var kitLoaded = false
    var inCountdown = false
    var ridingEntityID = -1
    private val fakeBlocks = Registry { obj: BlockData -> obj.toString() }
    val customKits = Short2ObjectOpenHashMap<Int2ObjectOpenHashMap<Array<ItemStack?>>>()
    val visiblePlayersOnTab = ObjectOpenHashSet<UUID>()
    var spectatable: Spectatable? = null
        set(value) {
            if (field == value) return
            if (field is TeamMatch) {
                val groups = (field as TeamMatch).nametagGroups

                if (groups != null) {
                    for (nametagGroup in groups) {
                        nametagGroup.remove(player)
                        player?.let { TeamMatch.refreshBukkitScoreboard(it) }
                    }
                }
            }

            field?.spectators?.remove(this)

            value?.let {
                if (playerStatus !== PlayerStatus.IDLE
                    && playerStatus !== PlayerStatus.FOLLOWING
                ) return message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

                if (it.ended) return
                it.spectators.add(this)

                if (value is TeamMatch) {
                    val groups = value.nametagGroups

                    var alreadyInGroup = false

                    if (groups != null) {
                        for (group in groups) {
                            if (group.players.contains(player)) {
                                alreadyInGroup = true
                                break
                            }
                        }
                    }

                    if (!alreadyInGroup) groups?.get(0)?.add(player)
                }

                if (it !is Event) {
                    broadcast(
                        it.participants, ChatMessages.SPECTATING_YOUR_MATCH.clone().replace(
                            "%player%",
                            name
                        )
                    )
                }
                scoreboard = SpectatorScoreboard.INSTANCE
                if (playerStatus !== PlayerStatus.FOLLOWING) {
                    inventory.setInventoryForSpectating()
                    playerStatus = PlayerStatus.SPECTATING
                }
            } ?: run {
                teleportToLobby()

                if (playerStatus !== PlayerStatus.FOLLOWING) {
                    if (party != null) inventory.setInventoryForParty()
                    else inventory.setInventoryForLobby()
                }

                scoreboard =
                    if (playerStatus === PlayerStatus.FOLLOWING)
                        FollowingScoreboard.INSTANCE
                    else
                        DefaultScoreboard.INSTANCE
            }

            field = value
        }
    var following: Profile? = null
        set(value) {
            if (value != null) {
                if (playerStatus !== PlayerStatus.IDLE) return message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

                playerStatus = PlayerStatus.FOLLOWING
                inventory.setInventoryToFollow()
                scoreboard = FollowingScoreboard.INSTANCE
                if (value.playerStatus === PlayerStatus.FIGHTING || value.contest is Event) spectate(value)
            } else {
                if (playerStatus !== PlayerStatus.FOLLOWING) return message(ErrorMessages.NOT_FOLLOWING)
                playerStatus = PlayerStatus.SPECTATING
                stopSpectating()
            }

            field?.followers?.remove(this)
            value?.followers?.add(this)
            field = value
        }
    var followers: ProfileList = ProfileList()
    var recievedDuelRequests: AutoExpireList<DuelRequest> = AutoExpireList()
    var recievedPartyRequests: AutoExpireList<Party> = AutoExpireList()
    var duelRequests: Boolean = true
    var partyRequests: Boolean = true
    var duelRequestReciever: Profile? = null
    private var clientTickStartMillis = 0L
    var clientTick = 0
        set(value) {
            if (field == 0) clientTickStartMillis = System.nanoTime() / 1000000
            field = value
        }
    val clientTimeMillis: Long
        get() {
            return if (clientTick == 0) System.nanoTime() / 1000000
            else clientTickStartMillis + (clientTick * 50L)
        }

    init {
        this.duelSettings = DuelSettings()
        this.inventory = PlayerInventory(this)
        this.scoreboardHandler = ScoreboardHandler(player)

        taskIds.add(
            Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(
                PracticePlugin.INSTANCE,
                {
                    if (!scoreboardHandler.deleted) scoreboard.updateBoard(
                        scoreboardHandler,
                        this
                    )
                }, 0, 10
            )
        )

        taskIds.add(
            Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(
                PracticePlugin.INSTANCE,
                {
                    fakeBlocks.registeredObjects.forEach(Consumer { blockData: BlockData ->
                        this.player?.let {
                            blockData.update(
                                it
                            )
                        }
                    })
                }, 0, 3
            )
        )
    }

    private fun getCustomKit(gametype: Gametype, section: ConfigurationSection): Array<ItemStack?> {
        val kit = gametype.kit.contents.clone()

        for (key in section.getKeys(false)) {
            val `object` = section[key]

            val index = key.toInt()

            if (`object` == null) {
                kit[index] = null
                continue
            }

            if (`object` is ItemStack) {
                kit[index] = `object`
                continue
            }

            kit[index] = null
        }

        return kit
    }

    fun getCustomKits(queuetype: Queuetype, gametype: Gametype): Int2ObjectOpenHashMap<Array<ItemStack?>> {
        val hash = (queuetype.id.toInt() shl 8 or gametype.id.toInt()).toShort()
        return getCustomKits(queuetype, gametype, hash)
    }

    fun getCustomKits(
        queuetype: Queuetype,
        gametype: Gametype,
        hash: Short
    ): Int2ObjectOpenHashMap<Array<ItemStack?>> {
        val kitLoadouts = customKits.getOrDefault(hash, Int2ObjectOpenHashMap())
        if (kitLoadouts.isNotEmpty()) return kitLoadouts

        val cs = playerConfig
            .getConfigurationSection(
                (name + ".KitData."
                        + gametype.name + "." + queuetype.name)
            ) ?: return kitLoadouts

        for (key in cs.getKeys(false)) {
            val cs1 = playerConfig
                .getConfigurationSection(
                    (name + ".KitData."
                            + gametype.name + "." + queuetype.name + "." + key)
                ) ?: continue

            kitLoadouts.put(key.toInt(), getCustomKit(gametype, cs1))
        }

        customKits.put(hash, kitLoadouts)

        return kitLoadouts
    }

    fun openMenu(menu: Menu) = menu.open(this)

    fun giveKit(kit: Kit) {
        inventory.setContents(kit.contents)
        inventory.armorContents = kit.armourContents
        inventory.holder.player?.updateInventory()
        kitLoaded = true
    }

    fun testTabVisibility(uuid: UUID) =
        Bukkit.getPlayer(uuid)?.isFake() == false || player?.world?.players?.any { it.uniqueId == uuid } ?: false

    fun message(message: Message) = this.player?.send(message) ?: Unit

    fun removeFromQueue() {
        QueueSystem.removePlayerFromQueue(this)
        message(ChatMessages.LEFT_QUEUE)
        playerStatus = PlayerStatus.IDLE
    }

    fun removeFromQueue(queuetype: Queuetype, gametype: Gametype) {
        if (!QueueSystem.removePlayerFromQueue(this, queuetype, gametype)) {
            removeFromQueue()
            player?.closeInventory()

            if (party != null) inventory.setInventoryForParty()
            else inventory.setInventoryForLobby()
        }
    }

    fun addPlayerToQueue(
        queuetype: Queuetype,
        gametype: Gametype
    ) {
        if (party != null && party!!.partyLeader != this) return message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER)
        val botQueue = queuetype.botsEnabled && queueSettings.botQueue
        if (botQueue && !gametype.botsEnabled) return message(ErrorMessages.COMING_SOON)

        val queueEntry = QueueSystem.getQueueEntry(this, queuetype, gametype)
        if (queueEntry != null) return message(ErrorMessages.ALREADY_IN_QUEUE)
        if (playerStatus == PlayerStatus.FIGHTING && match?.ended == false) return message(ErrorMessages.IN_MATCH)

        if (playerStatus === PlayerStatus.IDLE || playerStatus === PlayerStatus.QUEUEING || match?.ended == true) {
            if (playerStatus !== PlayerStatus.QUEUEING) {
                playerStatus = PlayerStatus.QUEUEING
                inventory.setInventoryForQueue()
            }

            val message = ChatMessages.JOINED_QUEUE.clone().replace("%queue%", queuetype.displayName)
                .replace(
                    "%category%",
                    if (gametype.inCategory)
                        " " + gametype.categoryName
                    else
                        ""
                )
                .replace("%gametype%", gametype.displayName)

            party?.let { it.partyMembers.forEach { profile -> profile.message(message) } } ?: message(message)

            QueueSystem.addPlayerToQueue(
                party ?: this,
                QueueSettings.toEntry(
                    queuetype, gametype, queueSettings.teamSize.toInt(), queueSettings.botQueue,
                    queueSettings.oldCombat,
                    queueSettings.opponentDifficulty,
                    queueSettings.botTeamSetting, queueSettings.enabledArenas
                )
            )
        }
    }

    fun teleportToLobby() {
        if (match?.ended == false) return

        this.player?.setBacktrack(false)
        PlayerUtil.teleport(this, lobbyLocation)

        if (playerStatus !== PlayerStatus.FOLLOWING && playerStatus !== PlayerStatus.QUEUEING) playerStatus =
            PlayerStatus.IDLE
    }

    fun sendToKitEditor(queuetype: Queuetype, gametype: Gametype) {
        message(ChatMessages.LEAVE_KIT_EDITOR)
        this.kitEditor = KitEditor(gametype, queuetype, this)
        kitEditor!!.start()
    }

    fun sendToKitCreator(submitAction: SubmitAction) {
        message(ChatMessages.LEAVE_KIT_CREATOR)
        this.kitCreator = KitCreator(this, submitAction)
        kitCreator!!.start()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Profile) return false
        return other.uuid == player?.uniqueId
    }

    private fun removeFromTab(uuid: UUID) {
        if (!visiblePlayersOnTab.contains(uuid)) return

        val player = Bukkit.getPlayer(uuid)

        if (player is CraftPlayer) this.player?.handle?.playerConnection?.sendPacket(
            PlayerInfoPacket(
                InfoAction.REMOVE_PLAYER,
                player.handle
            )
        )
    }

    private fun showOnTab(uuid: UUID) {
        if (visiblePlayersOnTab.contains(uuid)) return

        val player = Bukkit.getPlayer(uuid)

        if (player is CraftPlayer) this.player?.handle?.playerConnection?.sendPacket(
            PlayerInfoPacket(InfoAction.ADD_PLAYER, player.handle)
        )
    }

    private fun testVisibility(uuid: UUID): Boolean {
        if (playerStatus === PlayerStatus.KIT_CREATOR || playerStatus === PlayerStatus.KIT_EDITOR) return false
        if (contest?.concurrentMatches == false && contest?.participants?.contains(this) == true) return true

        if (playerStatus === PlayerStatus.IDLE || playerStatus === PlayerStatus.QUEUEING) {
            if (!this.playersVisible) return false

            val p = getProfile(uuid)
            return p == null || p.player?.hasPermission("practice.visible") == true
        }

        return true
    }

    fun updateVisibility() {
        player?.hiddenPlayers?.let {
            for (uuid in it) {
                val player = Bukkit.getPlayer(uuid) ?: continue
                this.player?.hidePlayer(player)
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(PracticePlugin.INSTANCE) {
            for (uuid in visiblePlayersOnTab) if (!testTabVisibility(uuid)) removeFromTab(uuid)
            val players: List<Player> = player?.world?.players ?: return@scheduleSyncDelayedTask
            for (player in players) {
                val uuid = player.uniqueId

                if (this.testTabVisibility(uuid)) showOnTab(uuid)
                else removeFromTab(uuid)

                val profile = getProfile(uuid) ?: continue

                if (testVisibility(uuid)) this.player?.showPlayer(profile.player)
                else this.player?.hidePlayer(profile.player, false)

                if (profile.testVisibility(this.uuid)) profile.player?.showPlayer(player)
                else profile.player?.hidePlayer(player, false)

                if (profile.testTabVisibility(this.uuid)) profile.showOnTab(this.uuid)
                else profile.removeFromTab(this.uuid)
            }
        }
    }

    fun resetDuelSettings() {
        duelSettings = DuelSettings()
    }

    fun startPartyOpenCooldown() {
        partyOpenCooldown = true

        Bukkit.getServer().scheduler.runTaskLaterAsynchronously(
            PracticePlugin.INSTANCE,
            { partyOpenCooldown = false }, 400
        )
    }

    override fun hashCode() = super.hashCode()

    fun stopSpectating() {
        if (playerStatus !== PlayerStatus.SPECTATING
            && playerStatus !== PlayerStatus.FOLLOWING
        ) {
            message(ErrorMessages.NOT_SPEC)
            return
        }

        spectatable = null
    }

    fun spectate(toBeSpectated: Profile) {
        if (toBeSpectated == this) return message(ErrorMessages.NOT_SPEC_SELF)

        this.spectatable = toBeSpectated.contest as? Event ?: toBeSpectated.match

        spectatable?.let {
            PlayerUtil.teleport(
                this,
                if (it is Event) it.arena.waitingLocation.bukkit(it.world)
                    ?: throw NullPointerException("Event arena not found")
                else toBeSpectated.player?.location ?: throw NullPointerException("Player location not found")
            )

            message(
                if (it is Event) ChatMessages.SPECTATING_EVENT
                else ChatMessages.SPECTATING.clone().replace("%player%", toBeSpectated.name)
            )

            message(ChatMessages.STOP_SPECTATING)
        } ?: message(ErrorMessages.PLAYER_NOT_IN_MATCH_OR_EVENT)
    }

    fun spectate(spectatable: Spectatable) {
        this.spectatable = spectatable
        spectatable.let {
            when (it) {
                is Event -> it.arena.waitingLocation.bukkit(it.world)
                is Match -> arenas[it.data.arenaId]?.location1?.bukkit(it.world)
                is SpectatableArena -> it.arena.location1.bukkit(it.world)
                else -> throw IllegalArgumentException("Invalid spectatable type")
            }?.let { it2 ->
                PlayerUtil.teleport(
                    this,
                    it2
                )
            }

            message(
                when (it) {
                    is Event -> ChatMessages.SPECTATING_EVENT
                    is SpectatableArena -> ChatMessages.VIEW_ARENA
                    else ->
                        ChatMessages.SPECTATING.clone().replace(
                            "%player%",
                            spectatable.participants.first().name
                        )
                }
            )

            message(ChatMessages.STOP_SPECTATING)
        }
    }

    fun sendDuelRequest(receiver: Profile) {
        if (receiver.playerStatus !== PlayerStatus.IDLE) return message(ErrorMessages.PLAYER_NOT_IN_LOBBY)
        if (!receiver.duelRequests) return message(ErrorMessages.DUEL_REQUESTS_DISABLED)

        for (request in receiver.recievedDuelRequests) if (request.sender == this) return message(ErrorMessages.DUEL_REQUEST_ALREADY_SENT)

        val request = DuelRequest(this, duelSettings)
        receiver.recievedDuelRequests.add(request)
        if (this.playerStatus === PlayerStatus.QUEUEING)
            this.removeFromQueue()
        message(ChatMessages.DUEL_REQUEST_SENT.clone().replace("%player%", receiver.name))

        val hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder(duelSettings.toString()).create()
        )
        receiver.message(
            ChatMessages.DUEL_REQUEST_RECIEVED.clone().replace("%player%", party?.let {
                if (it.partyLeader != this) return message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER)
                "$name's party (" + it.partyMembers.size + ") "
            } ?: this.name)
                .setTextEvent(
                    ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + this.name),
                    hoverEvent
                )
        )
    }
}
