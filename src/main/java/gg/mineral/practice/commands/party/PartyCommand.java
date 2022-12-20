package gg.mineral.practice.commands.party;

import java.util.Iterator;
import java.util.Map.Entry;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;
import net.md_5.bungee.api.chat.ClickEvent;

public class PartyCommand extends PlayerCommand {

	public PartyCommand() {
		super("party");
		setAliases("p");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = args.length > 0 ? args[0] : "";
		Profile profile = ProfileManager.getOrCreateProfile(pl), profile2;
		Party party;
		StringBuilder sb;
		ChatMessage joinedMessage;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.PARTIES_COMMANDS.send(pl);
				ChatMessages.PARTY_CREATE.send(pl);
				ChatMessages.PARTY_INVITE.send(pl);
				ChatMessages.PARTY_OPEN.send(pl);
				ChatMessages.PARTY_LIST.send(pl);
				ChatMessages.PARTY_JOIN.send(pl);
				ChatMessages.PARTY_DUEL.send(pl);
				ChatMessages.PARTY_ACCEPT.send(pl);
				ChatMessages.PARTY_LEAVE.send(pl);
				ChatMessages.PARTY_DISBAND.send(pl);

				return;
			case "create":
				if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				if (profile.isInParty()) {
					profile.message(ErrorMessages.YOU_ARE_ALREADY_IN_PARTY);
					return;
				}

				profile.addToParty(new Party(profile));
				ChatMessages.PARTY_CREATED.send(pl);
				return;
			case "invite":

				if (args.length < 2) {
					profile.message(UsageMessages.PARTY_INVITE);
					return;
				}

				profile2 = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(args[1]));

				if (profile2 == null) {
					profile.message(ErrorMessages.PLAYER_NOT_ONLINE);
					return;
				}

				if (!profile.isInParty()) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				if (profile2.isInParty()) {
					profile.message(ErrorMessages.PLAYER_IN_PARTY);
					return;
				}

				if (!profile.getParty().getPartyLeader().equals(profile)) {
					profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
					return;
				}

				if (profile.equals(profile2)) {
					profile.message(ErrorMessages.YOU_CAN_NOT_INVITE_YOURSELF);
					return;
				}

				if (profile2.getRecievedPartyRequests().containsKey(profile.getParty())) {
					ErrorMessages.WAIT_TO_INVITE.send(pl);
					return;
				}

				if (!profile2.isPartyRequests()) {
					profile.message(ErrorMessages.PARTY_REQUESTS_DISABLED);
					return;
				}

				profile2.getRecievedPartyRequests().add(profile.getParty());

				ChatMessages.PARTY_REQUEST_RECIEVED.clone().replace("%player%", profile.getName()).setTextEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + profile.getName()),
						ChatMessages.CLICK_TO_ACCEPT)
						.send(profile2.getPlayer());

				ChatMessages.PARTY_REQUEST_SENT.clone().replace("%player%", profile2.getName()).send(pl);

				return;
			case "open":
				if (!profile.isInParty()) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				party = profile.getParty();

				if (!party.getPartyLeader().equals(profile)) {
					profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
					return;
				}

				party.setOpen(!party.isOpen());
				ChatMessages.PARTY_OPENED.clone().replace("%opened%", party.isOpen() ? "opened" : "closed")
						.send(pl);

				if (party.isOpen()) {
					if (!profile.isPartyOpenCooldown()) {

						profile.startPartyOpenCooldown();

						ChatMessage messageToBroadcast = ChatMessages.BROADCAST_PARTY_OPEN.clone()
								.replace("%player%", profile.getName()).setTextEvent(
										new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/party join " + profile.getName()),
										ChatMessages.CLICK_TO_JOIN);

						ProfileManager.broadcast(ProfileManager.getProfiles(), messageToBroadcast);

						return;
					}

					ChatMessages.CAN_NOT_BROADCAST.send(pl);
				}

				return;
			case "join":

				if (args.length < 2) {
					profile.message(UsageMessages.PARTY_JOIN);
					return;
				}

				if (profile.isInParty()) {
					profile.message(ErrorMessages.YOU_ARE_ALREADY_IN_PARTY);
					return;
				}

				profile2 = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(args[1]));

				if (profile2 == null) {
					profile.message(ErrorMessages.PLAYER_NOT_ONLINE);
					return;
				}

				if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				party = profile2.getParty();

				if (party == null) {
					profile.message(ErrorMessages.PARTY_DOES_NOT_EXIST);
					return;
				}

				if (!party.isOpen()) {
					profile.message(ErrorMessages.PARTY_NOT_OPEN);
					return;
				}

				profile.addToParty(profile2.getParty());
				joinedMessage = ChatMessages.JOINED_PARTY.clone().replace("%player%", profile.getName());
				ProfileManager.broadcast(party.getPartyMembers(), joinedMessage);

				return;
			case "accept":

				if (args.length < 2) {
					profile.message(UsageMessages.PARTY_ACCEPT);
					return;
				}

				if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				profile2 = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(args[1]));

				if (profile2 == null) {
					profile.message(ErrorMessages.REQUEST_SENDER_NOT_ONLINE);
					return;
				}

				Iterator<Entry<Party, Long>> it = profile.getRecievedPartyRequests().entryIterator();

				while (it.hasNext()) {
					party = it.next().getKey();

					if (!party.getPartyLeader().equals(profile2)) {
						continue;
					}

					it.remove();
					profile.addToParty(party);
					joinedMessage = ChatMessages.JOINED_PARTY.clone().replace("%player%", profile.getName());
					ProfileManager.broadcast(party.getPartyMembers(), joinedMessage);
					return;
				}

				profile.message(ErrorMessages.REQUEST_EXPIRED);

				return;
			case "list":
				if (!profile.isInParty()) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				sb = new StringBuilder(CC.GRAY + "[");

				Iterator<Profile> profileIter = profile.getParty().getPartyMembers().iterator();

				while (profileIter.hasNext()) {
					Profile p = profileIter.next();
					sb.append(CC.GREEN + p.getName());

					if (profileIter.hasNext()) {
						sb.append(CC.GRAY + ", ");
					}
				}

				sb.append(CC.GRAY + "]");

				profile.getPlayer().sendMessage(sb.toString());

				return;
			case "leave":
			case "disband":

				if (!profile.isInParty()) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				profile.getParty().leave(profile);
				return;
		}

	}
}
