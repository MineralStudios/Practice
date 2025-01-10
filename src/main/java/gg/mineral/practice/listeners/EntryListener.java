package gg.mineral.practice.listeners;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.managers.EloManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import lombok.val;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EntryListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        ProfileManager.removeIfExists(event.getPlayer());
        val profile = ProfileManager.getOrCreateProfile(event.getPlayer());
        profile.setGameMode(GameMode.SURVIVAL);
        profile.heal();

        if (BotAPI.INSTANCE.isFakePlayer(profile.getPlayer().getUniqueId()))
            return;

        EloManager.updateName(profile);
        profile.getInventory().setInventoryForLobby();
        profile.removePotionEffects();
        profile.setPlayerStatus(PlayerStatus.IDLE);
        profile.setScoreboard(DefaultScoreboard.INSTANCE);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);

        val victim = ProfileManager.getOrCreateProfile(e.getPlayer());

        victim.removeScoreboard();

        if (victim.isInParty())
            victim.getParty().leave(victim);
        else if (victim.isInTournament())
            victim.getTournament().removePlayer(victim);
        else if (victim.isInEvent())
            victim.getEvent().removePlayer(victim);

        switch (victim.getPlayerStatus()) {
            case FIGHTING -> victim.getMatch().end(victim);
            case QUEUEING -> victim.removeFromQueue();
            default -> {
            }
        }

        ProfileManager.remove(victim);
    }

    @EventHandler
    public void onPlayerInitialSpawn(PlayerInitialSpawnEvent e) {

        if (BotAPI.INSTANCE.isFakePlayer(e.getPlayer().getUniqueId()))
            return;

        e.setSpawnLocation(ProfileManager.getSpawnLocation());
    }
}
