package gg.mineral.practice.listeners;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;

public class PacketListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = ProfileManager.getOrCreateProfile(event.getPlayer());
        injectPlayer(profile);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    protected void injectPlayer(Profile profile) {

        profile.getPlayer().getHandle().playerConnection.getOutgoingPacketListeners().add(packet -> {
            if (packet instanceof PacketPlayOutNamedEntitySpawn namedEntitySpawn) {
                UUID uuid = namedEntitySpawn.getB();
                if (!profile.testVisibility(uuid)) {
                    profile.getVisiblePlayers().remove(uuid);
                    return true;
                }
                profile.getVisiblePlayers().add(uuid);
            }

            if (packet instanceof PacketPlayOutEntityDestroy destroy)
                for (int id : destroy.getA())
                    for (UUID uuid : profile.getVisiblePlayers())
                        if (profile.getPlayer().getHandle().getId() == id)
                            profile.getVisiblePlayers().remove(uuid);

            if (packet instanceof PacketPlayOutPlayerInfo playerInfo) {
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = playerInfo.getA();
                Iterator<PlayerInfoData> data = playerInfo.getB().iterator();

                while (data.hasNext()) {
                    PlayerInfoData playerInfoData = data.next();

                    if (playerInfoData == null)
                        continue;
                    UUID uuid = playerInfoData.a().getId();

                    if (!profile.testTabVisibility(uuid)
                            && action != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                        profile.getVisiblePlayersOnTab().remove(uuid);
                        data.remove();
                        continue;
                    }

                    if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER)
                        profile.getVisiblePlayersOnTab().add(uuid);
                    else if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER)
                        profile.getVisiblePlayersOnTab().remove(uuid);
                    else if (!profile.getVisiblePlayersOnTab().contains(uuid))
                        data.remove();
                }

                if (playerInfo.getB().isEmpty())
                    return true;
            }

            return false;
        });
    }

    protected void removePlayer(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.getOutgoingPacketListeners().clear();
    }
}