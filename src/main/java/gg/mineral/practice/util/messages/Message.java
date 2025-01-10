package gg.mineral.practice.util.messages;

import org.bukkit.entity.Player;

public abstract class Message {
    protected final StringBuilder messageBuilder;

    public Message(String string) {
        this.messageBuilder = new StringBuilder(string);
    }

    public void add(Message message) {
        messageBuilder.append(message.toString());
    }

    public void send(Player player) {
        player.sendMessage(toString());
    }

    @Override
    public String toString() {
        return messageBuilder.toString();
    }

    @Override
    public abstract Message clone();
}
