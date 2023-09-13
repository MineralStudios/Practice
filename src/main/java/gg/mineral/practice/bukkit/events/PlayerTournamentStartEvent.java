package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class PlayerTournamentStartEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    @Setter
    boolean cancelled = false;
    final Player host;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
