package gg.mineral.practice.listeners;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import lombok.val;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PacketListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        val profile = ProfileManager.getOrCreateProfile(event.getPlayer());
        injectPlayer(profile);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    protected void injectPlayer(Profile profile) {

        val handle = profile.getPlayer().getHandle();
        val playerConnection = handle.playerConnection;

        playerConnection.getIncomingPacketListeners().add(packet -> {
            try {
                if (packet instanceof PacketPlayInSteerVehicle)
                    return profile.isInMatchCountdown();

                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });

        playerConnection.getOutgoingPacketListeners().add(packet -> {
            try {
                if (packet instanceof PacketPlayOutPlayerInfo playerInfo) {
                    val action = playerInfo.getA();
                    val data = playerInfo.getB().iterator();

                    while (data.hasNext()) {
                        val playerInfoData = data.next();

                        if (playerInfoData == null)
                            continue;
                        val uuid = playerInfoData.a().getId();

                        if (!uuid.equals(profile.getUuid()) && !profile.getSetVisiblePlayersOnTab().contains(uuid)
                                && action != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                            data.remove();
                            continue;
                        }

                        if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER) {
                            profile.getVisiblePlayersOnTab().add(uuid);
                        } else if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                            profile.getVisiblePlayersOnTab().remove(uuid);
                        } else if (!uuid.equals(profile.getUuid()) && !profile.getVisiblePlayersOnTab().contains(uuid))
                            data.remove();
                    }

                    return playerInfo.getB().isEmpty();
                }

                return false;

            } catch (Exception e) {
                System.out.println("Error with packet: " + packet.getClass().getSimpleName());
                e.printStackTrace();
                return false;
            }
        });
    }

    protected void removePlayer(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.getOutgoingPacketListeners().clear();
    }
}