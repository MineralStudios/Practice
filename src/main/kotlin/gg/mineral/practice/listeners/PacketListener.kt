package gg.mineral.practice.listeners

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.getOrCreateProfile
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction
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
    fun onJoin(event: PlayerJoinEvent) = injectPlayer(getOrCreateProfile(event.player))

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) = removePlayer(event.player)

    private fun PlayerInfoPacket.getAction() = this.a

    private fun PlayerInfoPacket.getInfoData() = this.b

    private fun PlayerInfoPacket.PlayerInfoData.getGameProfile() = this.a()

    private fun injectPlayer(profile: Profile) {
        val handle = profile.player.handle
        val playerConnection = handle.playerConnection

        playerConnection.incomingPacketListeners.add(Object2BooleanFunction { if (it is PacketPlayInSteerVehicle) profile.inMatchCountdown else false })

        playerConnection.outgoingPacketListeners.add(Object2BooleanFunction { packet: Any ->
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

                return@Object2BooleanFunction packet.getInfoData().isEmpty()
            }

            return@Object2BooleanFunction false
        })
    }

    private fun removePlayer(player: Player) =
        (player as CraftPlayer).handle.playerConnection.outgoingPacketListeners.clear()
}