package gg.mineral.practice.listeners

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import gg.mineral.practice.managers.ProfileManager.getProfile


class PacketEventsListener : PacketListener {

    override fun onPacketReceive(event: PacketReceiveEvent) {
        if (event.packetType == PacketType.Play.Client.PLAYER_FLYING || event.packetType == PacketType.Play.Client.PLAYER_POSITION || event.packetType == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION || event.packetType == PacketType.Play.Client.PLAYER_ROTATION) {
            val profile = getProfile(event.user.profile.uuid) ?: return
            profile.clientTick += 1
        }

        event.isCancelled =
            event.packetType == PacketType.Play.Client.STEER_VEHICLE && getProfile(event.user.profile.uuid)?.inCountdown == true
    }

    override fun onPacketSend(event: PacketSendEvent) {
        when (event.packetType) {
            PacketType.Play.Server.PLAYER_INFO -> {
                val playerInfo = WrapperPlayServerPlayerInfo(event)
                val action = playerInfo.action
                val data = playerInfo.playerDataList
                val iter = data.iterator()

                val profile = getProfile(event.user.profile.uuid) ?: return

                while (iter.hasNext()) {
                    val playerInfoData = iter.next() ?: continue

                    val uuid = playerInfoData.userProfile.uuid

                    if (uuid != profile.uuid && !profile.testTabVisibility(uuid) && action != WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER) {
                        iter.remove()
                        continue
                    }

                    if (action == WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) profile.visiblePlayersOnTab.add(uuid)
                    else if (action == WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER) profile.visiblePlayersOnTab.remove(
                        uuid
                    )
                    else if (uuid != profile.uuid && !profile.visiblePlayersOnTab.contains(uuid)) iter.remove()
                }

                event.isCancelled = data.isEmpty()
            }

            // Cancel statistics packet
            PacketType.Play.Server.STATISTICS -> event.isCancelled = true
        }
    }
}