package gg.mineral.practice.util.messages

import org.bukkit.command.CommandSender

abstract class Message(string: String) {
    protected val messageBuilder by lazy {
        prepend(StringBuilder(string))
    }

    fun add(message: Message): StringBuilder = messageBuilder.append(message.toString())

    open fun send(sender: CommandSender) = sender.sendMessage(toString())

    abstract fun prepend(builder: StringBuilder): StringBuilder

    final override fun toString() = messageBuilder.toString()

    abstract fun clone(): Message
}
