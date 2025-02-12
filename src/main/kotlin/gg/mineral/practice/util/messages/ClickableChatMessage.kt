package gg.mineral.practice.util.messages

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ClickableChatMessage(string: String, colorPrefix: String, bold: Boolean = false) :
    ChatMessage(string, colorPrefix, bold) {
    private var clickEvent: ClickEvent? = null
    private var hoverEvent: HoverEvent? = null

    fun setTextEvent(clickEvent: ClickEvent?, hoverEvent: HoverEvent?): ClickableChatMessage {
        this.clickEvent = clickEvent
        this.hoverEvent = hoverEvent
        return this
    }

    override fun replace(target: String, replacement: String): ClickableChatMessage {
        super.replace(target, replacement)
        return this
    }

    override fun highlightText(c: String, vararg highlighted: String): ClickableChatMessage {
        super.highlightText(c, *highlighted)
        return this
    }

    override fun send(sender: CommandSender) {
        val component = TextComponent(toString())

        if (clickEvent != null) component.clickEvent = clickEvent

        if (hoverEvent != null) component.hoverEvent = hoverEvent

        if (sender is Player) sender.spigot().sendMessage(component)
    }

    override fun clone() =
        ClickableChatMessage(messageBuilder.toString(), this.colorPrefix, this.bold).setTextEvent(
            clickEvent,
            hoverEvent
        )
}
