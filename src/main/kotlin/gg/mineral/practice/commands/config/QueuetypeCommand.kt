package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.inventory.menus.QueuetypeArenaEnableMenu
import gg.mineral.practice.managers.QueuetypeManager
import gg.mineral.practice.managers.QueuetypeManager.getQueuetypeByName
import gg.mineral.practice.managers.QueuetypeManager.queuetypes
import gg.mineral.practice.managers.QueuetypeManager.registerQueuetype
import gg.mineral.practice.managers.QueuetypeManager.remove
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import gg.mineral.server.combat.KnockbackProfile
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command(name = "queuetype")
@Permission("practice.config")
class QueuetypeCommand : CommandSenderAppender {

    @Execute(name = "create")
    fun executeCreate(@Context sender: CommandSender, @Arg name: String) {
        if (getQueuetypeByName(name) != null) {
            sender.send(ErrorMessages.QUEUETYPE_ALREADY_EXISTS)
            return
        }

        registerQueuetype(Queuetype(name, QueuetypeManager.CURRENT_ID++))
        sender.send(ChatMessages.QUEUETYPE_CREATED.clone().replace("%queuetype%", name))
    }

    @Execute(name = "setdisplay", aliases = ["display"])
    fun executeSetDisplay(@Context player: Player, @Arg queuetype: Queuetype, @Arg displayName: Optional<String>) {
        queuetype.displayItem = player.itemInHand

        displayName.ifPresent { queuetype.displayName = it.replace("&", "§") }

        player.send(ChatMessages.QUEUETYPE_DISPLAY_SET.clone().replace("%queuetype%", queuetype.name))
    }

    @Execute(name = "ranked", aliases = ["elo"])
    fun executeRanked(@Context sender: CommandSender, @Arg queuetype: Queuetype, @Arg toggled: Boolean) {
        queuetype.ranked = toggled
        sender.send(
            ChatMessages.QUEUETYPE_RANKED_SET.clone().replace("%queuetype%", queuetype.name)
                .replace("%toggled%", toggled.toString())
        )
    }

    @Execute(name = "community")
    fun executeCommunity(@Context sender: CommandSender, @Arg queuetype: Queuetype, @Arg toggled: Boolean) {
        queuetype.community = toggled
        sender.send(
            ChatMessages.QUEUETYPE_COMMUNITY_SET.clone().replace("%queuetype%", queuetype.name)
                .replace("%toggled%", toggled.toString())
        )
    }

    @Execute(name = "unranked")
    fun executeUnranked(@Context sender: CommandSender, @Arg queuetype: Queuetype, @Arg toggled: Boolean) {
        queuetype.unranked = toggled
        sender.send(
            ChatMessages.QUEUETYPE_UNRANKED_SET.clone().replace("%queuetype%", queuetype.name)
                .replace("%toggled%", toggled.toString())
        )
    }

    @Execute(name = "bots")
    fun executeBots(@Context sender: CommandSender, @Arg queuetype: Queuetype, @Arg toggled: Boolean) {
        queuetype.botsEnabled = toggled
        sender.send(
            ChatMessages.QUEUETYPE_BOTS_SET.clone().replace("%queuetype%", queuetype.name)
                .replace("%toggled%", toggled.toString())
        )
    }

    @Execute(name = "slot")
    fun executeSlot(@Context sender: CommandSender, @Arg queuetype: Queuetype, @Arg slot: Int) {
        queuetype.slotNumber = slot
        sender.send(
            ChatMessages.QUEUETYPE_SLOT_SET.clone().replace("%queuetype%", queuetype.name)
                .replace("%slot%", slot.toString())
        )
    }

    @Execute(name = "kb", aliases = ["knockback"])
    fun executeKnockback(@Context sender: CommandSender, @Arg queuetype: Queuetype, @Arg knockback: KnockbackProfile) {
        queuetype.knockback = knockback
        sender.send(
            ChatMessages.QUEUETYPE_KB_SET.clone().replace("%queuetype%", queuetype.name)
                .replace("%knockback%", knockback.name)
        )
    }

    @Execute(name = "list")
    fun executeList(@Context sender: CommandSender) {
        val sb = StringBuilder(CC.GRAY + "[")

        val queuetypes = queuetypes.values.iterator()

        while (queuetypes.hasNext()) {
            val q = queuetypes.next()
            sb.append(CC.GREEN + q.name)
            if (queuetypes.hasNext()) sb.append(CC.GRAY + ", ")
        }

        sb.append(CC.GRAY + "]")

        sender.sendMessage(sb.toString())
    }

    @Execute(name = "arena")
    fun executeArena(@Context profile: Profile, @Arg queuetype: Queuetype) =
        profile.openMenu(QueuetypeArenaEnableMenu(queuetype))

    @Execute(name = "delete")
    fun executeDelete(@Context sender: CommandSender, @Arg queuetype: Queuetype) {
        remove(queuetype)
        sender.send(ChatMessages.QUEUETYPE_DELETED.clone().replace("%queuetype%", queuetype.name))
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.QUEUETYPE_COMMANDS,
            ChatMessages.QUEUETYPE_CREATE,
            ChatMessages.QUEUETYPE_DISPLAY,
            ChatMessages.QUEUETYPE_RANKED,
            ChatMessages.QUEUETYPE_COMMUNITY,
            ChatMessages.QUEUETYPE_UNRANKED,
            ChatMessages.QUEUETYPE_BOTS,
            ChatMessages.QUEUETYPE_SLOT,
            ChatMessages.QUEUETYPE_KB,
            ChatMessages.QUEUETYPE_LIST,
            ChatMessages.QUEUETYPE_ARENA,
            ChatMessages.QUEUETYPE_DELETE
        )
    }
}
