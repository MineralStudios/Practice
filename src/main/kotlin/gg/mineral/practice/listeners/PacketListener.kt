package gg.mineral.practice.listeners

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.getOrCreateProfile
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo as PlayerInfoPacket
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction as InfoAction


class PacketListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        injectPlayer(getOrCreateProfile(event.player))
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) = removePlayer(event.player)

    private fun PlayerInfoPacket.getAction() = this.a

    private fun PlayerInfoPacket.getInfoData() = this.b

    private fun PlayerInfoPacket.PlayerInfoData.getGameProfile() = this.a()

    private fun injectPlayer(profile: Profile) {
        val playerConnection = profile.player.handle.playerConnection
        val channel: Channel? = playerConnection.networkManager.channel

        channel?.pipeline()?.addBefore("packet_handler", profile.name, object : ChannelDuplexHandler() {
            @Throws(Exception::class)
            override fun channelRead(channelHandlerContext: ChannelHandlerContext, packet: Any) {
                if (packet is PacketPlayInSteerVehicle && profile.inMatchCountdown) return
                super.channelRead(channelHandlerContext, packet)
            }

            @Throws(Exception::class)
            override fun write(
                channelHandlerContext: ChannelHandlerContext,
                packet: Any,
                channelPromise: ChannelPromise
            ) {
                if (packet is PlayerInfoPacket) {
                    val action = packet.getAction()
                    val data = packet.getInfoData().iterator()

                    while (data.hasNext()) {
                        val playerInfoData = data.next() ?: continue

                        val uuid = playerInfoData.getGameProfile().id

                        if ((uuid != profile.uuid) && !profile.testTabVisibility(uuid) && action != InfoAction.REMOVE_PLAYER) {
                            data.remove()
                            continue
                        }

                        if (action == InfoAction.ADD_PLAYER) profile.visiblePlayersOnTab.add(uuid)
                        else if (action == InfoAction.REMOVE_PLAYER) profile.visiblePlayersOnTab.remove(uuid)
                        else if (uuid != profile.uuid && !profile.visiblePlayersOnTab.contains(uuid)) data.remove()
                    }

                    if (packet.getInfoData().isEmpty()) return
                }

                super.write(channelHandlerContext, packet, channelPromise)
            }
        })
    }

    private fun removePlayer(player: Player) {
        if (player !is CraftPlayer) return
        val playerConnection = player.handle.playerConnection
        playerConnection.networkManager.channel?.let {
            it.eventLoop().submit { it.pipeline().remove(player.name) }
        }
    }
}