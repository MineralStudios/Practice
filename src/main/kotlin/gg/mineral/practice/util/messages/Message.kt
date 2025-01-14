package gg.mineral.practice.util.messages

import org.bukkit.command.CommandSender

abstract class Message(string: String) {
    protected val messageBuilder: StringBuilder = StringBuilder(string)

    fun add(message: Message): StringBuilder = messageBuilder.append(message.toString())

    open fun send(sender: CommandSender) = sender.sendMessage(toString())

    override fun toString() = messageBuilder.toString()

    abstract fun clone(): Message
}
