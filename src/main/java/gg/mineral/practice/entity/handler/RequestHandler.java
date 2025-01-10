package gg.mineral.practice.entity.handler;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.request.DuelRequest;
import gg.mineral.practice.util.collection.AutoExpireList;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

@RequiredArgsConstructor
public class RequestHandler {
    final Profile profile;
    @Getter
    AutoExpireList<DuelRequest> recievedDuelRequests = new AutoExpireList<>();
    @Getter
    AutoExpireList<Party> recievedPartyRequests = new AutoExpireList<>();
    @Getter
    @Setter
    boolean duelRequests = true, partyRequests = true;
    @Getter
    @Setter
    Profile duelRequestReciever;

    public void sendDuelRequest(Profile receiver) {

        if (receiver.getPlayerStatus() != PlayerStatus.IDLE) {
            profile.message(ErrorMessages.PLAYER_NOT_IN_LOBBY);
            return;
        }

        if (!receiver.getRequestHandler().isDuelRequests()) {
            profile.message(ErrorMessages.DUEL_REQUESTS_DISABLED);
            return;
        }

        for (val request : receiver.getRequestHandler().getRecievedDuelRequests()) {
            if (request.sender().equals(profile)) {
                profile.message(ErrorMessages.DUEL_REQUEST_ALREADY_SENT);
                return;
            }
        }

        var sender = profile.getName();

        if (profile.isInParty()) {
            if (!profile.getParty().getPartyLeader().equals(profile)) {
                profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
                return;
            }

            sender += "'s party (" + profile.getParty().getPartyMembers().size() + ") ";
        }

        val request = new DuelRequest(profile, profile.getDuelSettings());
        receiver.getRequestHandler().getRecievedDuelRequests().add(request);
        profile.removeFromQueue();
        ChatMessages.DUEL_REQUEST_SENT.clone().replace("%player%", receiver.getName()).send(profile.getPlayer());

        val hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(profile.getDuelSettings().toString()).create());

        ChatMessages.DUEL_REQUEST_RECIEVED.clone().replace("%player%", sender)
                .setTextEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + profile.getName()),
                        hoverEvent)
                .send(receiver.getPlayer());
    }
}
