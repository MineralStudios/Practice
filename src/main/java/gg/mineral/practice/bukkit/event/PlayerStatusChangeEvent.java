package gg.mineral.practice.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import lombok.Getter;

public class PlayerStatusChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    protected Profile profile;
    PlayerStatus previousStatus;
    PlayerStatus newStatus;
    boolean cancelled = false;

    public PlayerStatusChangeEvent(final Profile p, PlayerStatus newStatus) {
        this.profile = p;
        this.previousStatus = p.getPlayerStatus();
        this.newStatus = newStatus;
    }

    public PlayerStatus previousStatus() {
        return previousStatus;
    }

    public PlayerStatus newStatus() {
        return newStatus;
    }

    public boolean cancelled() {
        return cancelled;
    }

    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
