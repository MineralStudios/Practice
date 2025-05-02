package gg.mineral.practice.commands.party

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.appender.send
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.practice.party.Party
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.command.CommandSender

@Command(name = "party", aliases = ["p"])
class PartyCommand {
    @Execute(name = "create")
    fun executeCreate(@Context profile: Profile) {
        if (profile.playerStatus !== PlayerStatus.IDLE) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)
        if (profile.party != null) return profile.message(ErrorMessages.YOU_ARE_ALREADY_IN_PARTY)

        profile.party = Party(profile)
        profile.message(ChatMessages.PARTY_CREATED)
    }

    @Execute(name = "invite")
    fun executeInvite(@Context profile: Profile, @Arg invitee: Profile) {
        if (profile == invitee) return profile.message(ErrorMessages.YOU_CAN_NOT_INVITE_YOURSELF)
        if (invitee.party != null) return profile.message(ErrorMessages.PLAYER_IN_PARTY)
        if (!invitee.partyRequests) return profile.message(ErrorMessages.PARTY_REQUESTS_DISABLED)

        profile.party?.let {
            if (it.partyLeader != profile) return profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER)
            if (invitee.recievedPartyRequests.containsKey(it)) return profile.message(ErrorMessages.WAIT_TO_INVITE)

            invitee.recievedPartyRequests.add(it)

            invitee.message(
                ChatMessages.PARTY_REQUEST_RECIEVED.clone().replace("%player%", profile.name).setTextEvent(
                    ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + profile.name),
                    ChatMessages.CLICK_TO_ACCEPT
                )
            )

            profile.message(ChatMessages.PARTY_REQUEST_SENT.clone().replace("%player%", invitee.name))
        } ?: profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY)
    }

    @Execute(name = "open")
    fun executeOpen(@Context profile: Profile) {
        profile.party?.let {
            if (it.partyLeader != profile) return profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER)

            it.open = !it.open
            profile.message(ChatMessages.PARTY_OPENED.clone().replace("%opened%", if (it.open) "opened" else "closed"))

            if (it.open) {
                if (!profile.partyOpenCooldown) {
                    profile.startPartyOpenCooldown()

                    val messageToBroadcast = ChatMessages.BROADCAST_PARTY_OPEN.clone()
                        .replace("%player%", profile.name).setTextEvent(
                            ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/party join " + profile.name
                            ),
                            ChatMessages.CLICK_TO_JOIN
                        )

                    broadcast(messageToBroadcast)
                } else profile.message(ChatMessages.CAN_NOT_BROADCAST)
            }
        } ?: profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY)
    }

    @Execute(name = "join")
    fun executeJoin(@Context profile: Profile, @Arg partyLeader: Profile) {
        if (profile.playerStatus !== PlayerStatus.IDLE) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)
        if (profile.party != null) return profile.message(ErrorMessages.YOU_ARE_ALREADY_IN_PARTY)

        partyLeader.party?.let {
            if (!it.open) return profile.message(ErrorMessages.PARTY_NOT_OPEN)

            profile.party = it
            broadcast(it.partyMembers, ChatMessages.JOINED_PARTY.clone().replace("%player%", profile.name))
        } ?: profile.message(ErrorMessages.PARTY_DOES_NOT_EXIST)
    }

    @Execute(name = "accept")
    fun executeAccept(@Context profile: Profile, @Arg partyLeader: Profile) {
        if (profile.playerStatus !== PlayerStatus.IDLE) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

        val it = profile.recievedPartyRequests
            .entryIterator()

        while (it.hasNext()) {
            val party = it.next().key

            if (party.partyLeader != partyLeader) continue

            it.remove()
            profile.party = party
            broadcast(party.partyMembers, ChatMessages.JOINED_PARTY.clone().replace("%player%", profile.name))
            return
        }

        profile.message(ErrorMessages.REQUEST_EXPIRED)
    }

    @Execute(name = "list")
    fun executeList(@Context profile: Profile) {
        profile.party?.let {
            val sb = StringBuilder(CC.GRAY + "[")

            val profileIter: Iterator<Profile> = it.partyMembers.iterator()

            while (profileIter.hasNext()) {
                val p = profileIter.next()
                sb.append(CC.GREEN).append(p.name)

                if (profileIter.hasNext()) sb.append(CC.GRAY).append(", ")
            }

            sb.append(CC.GRAY).append("]")

            profile.player?.sendMessage(sb.toString())
        } ?: profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY)
    }

    @Execute(name = "leave", aliases = ["disband"])
    fun executeLeave(@Context profile: Profile) =
        profile.party?.leave(profile) ?: profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY)

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.PARTIES_COMMANDS,
            ChatMessages.PARTY_CREATE,
            ChatMessages.PARTY_INVITE,
            ChatMessages.PARTY_OPEN,
            ChatMessages.PARTY_LIST,
            ChatMessages.PARTY_JOIN,
            ChatMessages.PARTY_DUEL,
            ChatMessages.PARTY_ACCEPT,
            ChatMessages.PARTY_LEAVE,
            ChatMessages.PARTY_DISBAND
        )
    }
}
