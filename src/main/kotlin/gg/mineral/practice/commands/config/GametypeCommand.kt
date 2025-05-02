package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.arena.Arena
import gg.mineral.practice.arena.EventArena
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.appender.send
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.menus.GametypeArenaEnableMenu
import gg.mineral.practice.kit.Kit
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.managers.GametypeManager.gametypes
import gg.mineral.practice.managers.GametypeManager.getGametypeByName
import gg.mineral.practice.managers.GametypeManager.registerGametype
import gg.mineral.practice.managers.GametypeManager.remove
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command(name = "gametype")
@Permission("practice.config")
class GametypeCommand {

    @Execute(name = "create")
    fun executeCreate(@Context sender: CommandSender, @Arg name: String) {
        if (getGametypeByName(name) != null) {
            sender.send(ErrorMessages.GAMETYPE_ALREADY_EXISTS)
            return
        }

        registerGametype(Gametype(name, GametypeManager.CURRENT_ID++))
        sender.send(ChatMessages.GAMETYPE_CREATED.clone().replace("%gametype%", name))
    }

    @Execute(name = "loadkit")
    fun executeLoadKit(@Context player: Player, @Arg gametype: Gametype) {
        player.inventory.contents = gametype.kit.contents
        player.inventory.armorContents = gametype.kit.armourContents
        ChatMessages.GAMETYPE_LOADED_KIT.clone().replace("%gametype%", gametype.name).send(player)
    }

    @Execute(name = "kit", aliases = ["savekit"])
    fun executeKit(@Context player: Player, @Arg gametype: Gametype) {
        val contents = player.inventory.contents
        val armourContents = player.inventory.armorContents

        gametype.kit = Kit(gametype.name, contents, armourContents)
        ChatMessages.GAMETYPE_KIT_SET.clone().replace("%gametype%", gametype.name).send(player)
    }

    @Execute(name = "setdisplay", aliases = ["display"])
    fun executeSetDisplay(@Context player: Player, @Arg gametype: Gametype, @Arg displayName: Optional<String>) {
        gametype.displayItem = player.itemInHand

        displayName.ifPresent { gametype.displayName = it.replace("&", "§") }

        player.send(ChatMessages.GAMETYPE_DISPLAY_SET.clone().replace("%gametype%", gametype.name))
    }

    @Execute(name = "hitdelay", aliases = ["nodamageticks"])
    fun executeHitDelay(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg delay: Int) {
        gametype.noDamageTicks = delay
        sender.send(
            ChatMessages.GAMETYPE_DAMAGE_TICKS_SET.clone().replace("%gametype%", gametype.name).replace(
                "%delay%",
                delay.toString()
            )
        )
    }

    @Execute(name = "buildlimit")
    fun executeBuildLimit(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg limit: Int) {
        gametype.buildLimit = limit
        sender.send(
            ChatMessages.GAMETYPE_BUILD_LIMIT_SET.clone().replace("%gametype%", gametype.name).replace(
                "%limit%",
                limit.toString()
            )
        )
    }

    @Execute(name = "bots")
    fun executeBots(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.botsEnabled = toggled
        sender.send(
            ChatMessages.GAMETYPE_BOTS_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "regen")
    fun executeRegen(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.regeneration = toggled
        sender.send(
            ChatMessages.GAMETYPE_REGEN_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "griefing")
    fun executeGriefing(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.griefing = toggled
        sender.send(
            ChatMessages.GAMETYPE_GRIEFING_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "queue")
    fun executeQueue(
        @Context sender: CommandSender,
        @Arg gametype: Gametype,
        @Arg queuetype: Queuetype,
        @Arg slot: Int
    ) {
        if (slot == -1)
            queuetype.removeMenuEntry(gametype)
        else
            queuetype.addMenuEntry(gametype, slot)

        sender.send(
            ChatMessages.GAMETYPE_SLOT_SET.clone().replace("%gametype%", gametype.name).replace(
                "%slot%",
                slot.toString()
            )
        )
    }

    @Execute(name = "build")
    fun executeBuild(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.build = toggled
        sender.send(
            ChatMessages.GAMETYPE_BUILD_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "deadlywater")
    fun executeDeadlyWater(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.deadlyWater = toggled
        sender.send(
            ChatMessages.GAMETYPE_DEADLY_WATER_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "looting")
    fun executeLooting(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.looting = toggled
        sender.send(
            ChatMessages.GAMETYPE_LOOTING_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "damage")
    fun executeDamage(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.damage = toggled
        sender.send(
            ChatMessages.GAMETYPE_DAMAGE_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "hunger")
    fun executeHunger(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.hunger = toggled
        sender.send(
            ChatMessages.GAMETYPE_HUNGER_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "boxing")
    fun executeBoxing(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.boxing = toggled
        sender.send(
            ChatMessages.GAMETYPE_BOXING_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "epearl")
    fun executeEpearl(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg cooldown: Int) {
        gametype.pearlCooldown = cooldown
        sender.send(
            ChatMessages.GAMETYPE_PEARL_COOLDOWN_SET.clone().replace("%gametype%", gametype.name).replace(
                "%cooldown%",
                cooldown.toString()
            )
        )
    }

    @Execute(name = "list")
    fun executeList(@Context sender: CommandSender) {
        val sb = StringBuilder(CC.GRAY + "[")

        val iterator = gametypes.values.iterator()

        while (iterator.hasNext()) {
            iterator.next()?.let {
                sb.append(CC.GREEN).append(it.name)
                if (iterator.hasNext()) sb.append(CC.GRAY).append(", ")
            }
        }

        sb.append(CC.GRAY).append("]")

        sender.sendMessage(sb.toString())
    }

    @Execute(name = "arena")
    fun executeArena(@Context profile: Profile, @Arg gametype: Gametype) =
        profile.openMenu(GametypeArenaEnableMenu(gametype))

    @Execute(name = "event")
    fun executeEvent(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg toggled: Boolean) {
        gametype.event = toggled
        sender.send(
            ChatMessages.GAMETYPE_EVENT_SET.clone().replace("%gametype%", gametype.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "seteventarena", aliases = ["eventarena"])
    fun executeSetEventArena(@Context sender: CommandSender, @Arg gametype: Gametype, @Arg arena: Arena) {
        gametype.setEventArena(arena as? EventArena ?: return)
        sender.send(
            ChatMessages.GAMETYPE_EVENT_ARENA_SET.clone().replace("%gametype%", gametype.name).replace(
                "%arena%",
                arena.name
            )
        )
    }

    @Execute(name = "enablearenaforall")
    fun executeEnableArenaForAll(@Context sender: CommandSender, @Arg arena: Arena, @Arg toggled: Boolean) {
        for (gametype in gametypes.values) gametype?.enableArena(arena, toggled)
        sender.send(
            ChatMessages.GAMETYPE_ARENA_FOR_ALL_SET.clone().replace("%arena%", arena.name).replace(
                "%toggled%",
                toggled.toString()
            )
        )
    }

    @Execute(name = "delete", aliases = ["remove"])
    fun executeDelete(@Context sender: CommandSender, @Arg gametype: Gametype) {
        remove(gametype)
        sender.send(ChatMessages.GAMETYPE_DELETED.clone().replace("%gametype%", gametype.name))
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.GAMETYPE_COMMANDS,
            ChatMessages.GAMETYPE_CREATE,
            ChatMessages.GAMETYPE_KIT,
            ChatMessages.GAMETYPE_LOAD_KIT,
            ChatMessages.GAMETYPE_DAMAGE_TICKS,
            ChatMessages.GAMETYPE_BUILD_LIMIT,
            ChatMessages.GAMETYPE_GRIEFING,
            ChatMessages.GAMETYPE_BUILD,
            ChatMessages.GAMETYPE_LOOTING,
            ChatMessages.GAMETYPE_DAMAGE,
            ChatMessages.GAMETYPE_HUNGER,
            ChatMessages.GAMETYPE_BOXING,
            ChatMessages.GAMETYPE_BOTS,
            ChatMessages.GAMETYPE_REGEN,
            ChatMessages.GAMETYPE_EPEARL,
            ChatMessages.GAMETYPE_ARENA_FOR_ALL,
            ChatMessages.GAMETYPE_DISPLAY,
            ChatMessages.GAMETYPE_QUEUE,
            ChatMessages.GAMETYPE_LIST,
            ChatMessages.GAMETYPE_ARENA,
            ChatMessages.GAMETYPE_EVENT_ARENA,
            ChatMessages.GAMETYPE_EVENT,
            ChatMessages.GAMETYPE_DEADLY_WATER,
            ChatMessages.GAMETYPE_DELETE
        )
    }
}
