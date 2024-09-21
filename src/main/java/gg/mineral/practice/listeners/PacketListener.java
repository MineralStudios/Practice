package gg.mineral.practice.listeners;

import java.util.Iterator;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import gg.mineral.bot.api.BotAPI;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;

public class PacketListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (BotAPI.INSTANCE.isFakePlayer(event.getPlayer().getUniqueId()))
            return;

        injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (BotAPI.INSTANCE.isFakePlayer(event.getPlayer().getUniqueId()))
            return;

        removePlayer(event.getPlayer());
    }

    protected void injectPlayer(Player player) {

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise)
                    throws Exception {

                if (packet instanceof PacketPlayOutPlayerInfo infoPacket) {
                    Iterator<PlayerInfoData> infoData = infoPacket.getB().iterator();
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = infoPacket.getA();

                    while (infoData.hasNext()) {
                        PlayerInfoData data = infoData.next();
                        if (BotAPI.INSTANCE.isFakePlayer(data.a().getId())) {
                            if (action != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                                PacketPlayOutPlayerInfo newInfoPacket = new PacketPlayOutPlayerInfo(
                                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                                        new EntityPlayer[0]);
                                newInfoPacket.getB().add(data);
                                entityPlayer.playerConnection.sendPacket(newInfoPacket);
                            }
                        }
                    }
                }

                super.write(channelHandlerContext, packet, channelPromise);
            }

        };

        ChannelPipeline pipeline = entityPlayer.playerConnection.networkManager.channel
                .pipeline();
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    protected void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }
}