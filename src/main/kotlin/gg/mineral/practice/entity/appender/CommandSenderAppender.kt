package gg.mineral.practice.entity.appender

import gg.mineral.practice.util.messages.Message
import org.bukkit.command.CommandSender

interface CommandSenderAppender {
    fun CommandSender.send(vararg messages: Message) {
        messages.forEach { it.send(this) }
    }
}