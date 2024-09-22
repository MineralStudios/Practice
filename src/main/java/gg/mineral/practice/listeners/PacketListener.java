package gg.mineral.practice.listeners;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;

public class PacketListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (event.getPlayer() instanceof CraftPlayer cp)
            if (cp.getHandle().playerConnection.networkManager.channel == null)
                return;

        Profile profile = ProfileManager.getOrCreateProfile(event.getPlayer());

        injectPlayer(profile);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (event.getPlayer() instanceof CraftPlayer cp)
            if (cp.getHandle().playerConnection.networkManager.channel == null)
                return;

        removePlayer(event.getPlayer());
    }

    protected void injectPlayer(Profile profile) {

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise)
                    throws Exception {

                if (packet instanceof PacketPlayOutNamedEntitySpawn namedEntitySpawn) {
                    UUID uuid = namedEntitySpawn.getB();
                    if (!profile.testVisibility(uuid)) {
                        profile.getVisiblePlayers().remove(uuid);
                        return;
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
                        UUID uuid = playerInfoData.a().getId();

                        if (!profile.testTabVisibility(uuid)
                                && action != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                            profile.getVisiblePlayersOnTab().remove(uuid);
                            PacketPlayOutPlayerInfo newPlayerInfo = new PacketPlayOutPlayerInfo(
                                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[0]);
                            newPlayerInfo.getB().add(playerInfoData);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(PracticePlugin.INSTANCE,
                                    () -> profile.getPlayer().getHandle().playerConnection.sendPacket(newPlayerInfo),
                                    500);
                            continue;
                        }

                        if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER)
                            profile.getVisiblePlayersOnTab().add(uuid);
                        else if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER)
                            profile.getVisiblePlayersOnTab().remove(uuid);
                    }

                    if (playerInfo.getB().isEmpty())
                        return;
                }

                super.write(channelHandlerContext, packet, channelPromise);
            }

        };

        ChannelPipeline pipeline = profile.getPlayer().getHandle().playerConnection.networkManager.channel
                .pipeline();
        pipeline.addBefore("packet_handler", profile.getName(), channelDuplexHandler);
    }

    protected void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }
}