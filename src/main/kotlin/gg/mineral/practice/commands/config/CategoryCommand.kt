package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.category.Category
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.CategoryManager
import gg.mineral.practice.managers.CategoryManager.categories
import gg.mineral.practice.managers.CategoryManager.getCategoryByName
import gg.mineral.practice.managers.CategoryManager.registerCategory
import gg.mineral.practice.managers.CategoryManager.remove
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command(name = "category")
@Permission("practice.config")
class CategoryCommand : CommandSenderAppender {

    @Execute(name = "create")
    fun executeCreate(@Context sender: CommandSender, @Arg name: String) {
        if (getCategoryByName(name) != null) {
            sender.send(ErrorMessages.CATEGORY_ALREADY_EXISTS)
            return
        }

        val category = Category(name, CategoryManager.CURRENT_ID++)
        registerCategory(category)
        sender.send(ChatMessages.CATEGORY_CREATED.clone().replace("%category%", name))
    }

    @Execute(name = "setdisplay", aliases = ["display"])
    fun executeSetDisplay(@Context player: Player, @Arg category: Category, @Arg displayName: Optional<String>) {
        category.displayItem = player.itemInHand

        displayName.ifPresent { category.displayName = it.replace("&", "§") }

        player.send(ChatMessages.CATEGORY_DISPLAY_SET.clone().replace("%category%", category.name))
    }

    @Execute(name = "queue")
    fun executeQueue(
        @Context sender: CommandSender,
        @Arg category: Category,
        @Arg queuetype: Queuetype,
        @Arg slot: Int
    ) {
        if (slot == -1)
            queuetype.removeMenuEntry(category)
        else
            queuetype.addMenuEntry(category, slot)

        sender.send(
            ChatMessages.CATEGORY_SLOT.clone().replace("%category%", category.name).replace(
                "%slot%",
                slot.toString()
            )
        )
    }


    @Execute(name = "add")
    fun executeAdd(@Context sender: CommandSender, @Arg category: Category, @Arg gametype: Gametype) {
        category.addGametype(gametype)
        sender.send(
            ChatMessages.CATEGORY_ADDED.clone().replace("%gametype%", gametype.name)
                .replace("%category%", category.name)
        )
    }

    @Execute(name = "remove")
    fun executeRemove(@Context sender: CommandSender, @Arg category: Category, @Arg gametype: Gametype) {
        category.removeGametype(gametype)
        sender.send(
            ChatMessages.CATEGORY_REMOVED.clone().replace("%gametype%", gametype.name)
                .replace("%category%", category.name)
        )
    }

    @Execute(name = "list")
    fun executeList(@Context sender: CommandSender) {
        val sb = StringBuilder(CC.GRAY + "[")

        val categoryIter = categories.values.iterator()

        while (categoryIter.hasNext()) {
            categoryIter.next()?.let {
                sb.append(CC.GREEN + it.name)
                if (categoryIter.hasNext()) sb.append(CC.GRAY + ", ")
            }
        }

        sb.append(CC.GRAY + "]")

        sender.sendMessage(sb.toString())
    }

    @Execute(name = "delete")
    fun executeDelete(@Context sender: CommandSender, @Arg category: Category) {
        remove(category)
        sender.send(ChatMessages.CATEGORY_DELETED.clone().replace("%category%", category.name))
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.CATEGORY_COMMANDS,
            ChatMessages.CATEGORY_CREATE,
            ChatMessages.CATEGORY_DISPLAY,
            ChatMessages.CATEGORY_QUEUE,
            ChatMessages.CATEGORY_LIST,
            ChatMessages.CATEGORY_ADD,
            ChatMessages.CATEGORY_REMOVE,
            ChatMessages.CATEGORY_DELETE
        )
    }
}
