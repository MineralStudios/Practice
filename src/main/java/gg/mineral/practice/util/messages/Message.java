package gg.mineral.practice.util.messages;

public abstract class Message {
    String message;

    public void add(Message m) {
        message = message + m.toString();
    }

    public abstract void send(org.bukkit.entity.Player p);

    @Override
    public String toString() {
        return message;
    }
}
