package gg.mineral.practice.util.messages;

import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ClickableChatMessage extends ChatMessage {
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;

    public ClickableChatMessage(String string, String colorPrefix) {
        super(string, colorPrefix);
    }

    public ClickableChatMessage setTextEvent(ClickEvent clickEvent, HoverEvent hoverEvent) {
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        return this;
    }

    @Override
    public ClickableChatMessage replace(String target, String replacement) {
        super.replace(target, replacement);
        return this;
    }

    @Override
    public ClickableChatMessage highlightText(String c, String... highlighted) {
        super.highlightText(c, highlighted);
        return this;
    }

    @Override
    public void send(Player p) {
        val component = new TextComponent(toString());

        if (clickEvent != null)
            component.setClickEvent(clickEvent);

        if (hoverEvent != null)
            component.setHoverEvent(hoverEvent);

        p.spigot().sendMessage(component);
    }

    @Override
    public ClickableChatMessage clone() {
        return new ClickableChatMessage(this.messageBuilder.toString(), this.prefix).setTextEvent(clickEvent, hoverEvent);
    }
}
