package gg.mineral.practice.util.messages;

import org.bukkit.entity.Player;

import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ClickableChatMessage extends ChatMessage {

    ClickEvent clickEvent;
    HoverEvent hoverEvent;

    ClickableChatMessage(String s) {
        super(s);
    }

    public ClickableChatMessage(String s, String c, boolean bold) {
        super(s, c, bold);
    }

    public ClickableChatMessage(String s, String c) {
        super(s, c);
    }

    public ClickableChatMessage setTextEvent(ClickEvent clickEvent, HoverEvent hoverEvent) {
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        return this;
    }

    @Override
    public void send(Player p) {
        TextComponent component = new TextComponent(message);

        if (clickEvent != null)
            component.setClickEvent(clickEvent);

        if (hoverEvent != null)
            component.setHoverEvent(hoverEvent);

        p.spigot().sendMessage(component);
    }

    public ClickableChatMessage highlightText(String c, String... highlighted) {

        for (val s : highlighted)
            message = message.replace(s, c + s + this.addition);

        return this;
    }

    public ClickableChatMessage clone() {
        return new ClickableChatMessage(message).setTextEvent(clickEvent, hoverEvent);
    }

    public ClickableChatMessage replace(String message, String replacement) {
        this.message = this.message.replace(message, replacement);
        return this;
    }
}
