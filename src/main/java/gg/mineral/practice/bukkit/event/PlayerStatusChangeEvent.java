package gg.mineral.practice.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;

public class PlayerStatusChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    protected Profile p;
    PlayerStatus previousStatus;
    PlayerStatus newStatus;
    boolean cancelled = false;

    public PlayerStatusChangeEvent(final Profile p, PlayerStatus newStatus) {
        this.p = p;
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

    public final Profile getPlayer() {
        return p;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
