package gg.mineral.practice.commands.party;

import java.util.Iterator;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.party.Party;
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
		Profile profile = PlayerManager.get(p -> p.getUUID().equals(pl.getUniqueId()));
		Party party;
		Profile profilearg;
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
				if (profile.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
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

				if (args.length < 3) {
					profile.message(UsageMessages.PARTY_INVITE);
					return;
				}

				profilearg = PlayerManager.get(p -> p.getName().equalsIgnoreCase(args[1]));

				if (profilearg == null) {
					profile.message(ErrorMessages.PLAYER_NOT_ONLINE);
					return;
				}

				if (!profile.isInParty()) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				if (profilearg.isInParty()) {
					profile.message(ErrorMessages.PLAYER_IN_PARTY);
					return;
				}

				if (!profile.getParty().getPartyLeader().equals(profile)) {
					profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
					return;
				}

				if (profile.equals(profilearg)) {
					profile.message(ErrorMessages.YOU_CAN_NOT_INVITE_YOURSELF);
					return;
				}

				if (profilearg.getRecievedPartyRequests().containsKey(profile.getParty())) {
					ErrorMessages.WAIT_TO_INVITE.send(pl);
					return;
				}

				profilearg.getRecievedPartyRequests().add(profile.getParty());

				ChatMessages.PARTY_REQUEST_RECIEVED.clone().replace("%player%", profile.getName()).setTextEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + profile.getName()),
						ChatMessages.CLICK_TO_ACCEPT)
						.send(profilearg.bukkit());

				ChatMessages.PARTY_REQUEST_SENT.clone().replace("%player%", profilearg.getName()).send(pl);

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

				party.setOpen(!party.getPartyOpen());
				ChatMessages.PARTY_OPENED.clone().replace("%opened%", party.getPartyOpen() ? "opened" : "closed")
						.send(pl);

				if (party.getPartyOpen()) {
					if (!profile.getPartyOpenCooldown()) {

						profile.startPartyOpenCooldown();

						ChatMessage messageToBroadcast = ChatMessages.BROADCAST_PARTY_OPEN.clone()
								.replace("%player%", profile.getName()).setTextEvent(
										new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/party join " + profile.getName()),
										ChatMessages.CLICK_TO_JOIN);

						PlayerManager.broadcast(PlayerManager.list(), messageToBroadcast);

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

				profilearg = PlayerManager.get(p -> p.getName().equalsIgnoreCase(args[1]));

				if (profilearg == null) {
					profile.message(ErrorMessages.PLAYER_NOT_ONLINE);
					return;
				}

				if (profile.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				party = profilearg.getParty();

				if (party == null) {
					profile.message(ErrorMessages.PARTY_DOES_NOT_EXIST);
					return;
				}

				if (!party.getPartyOpen()) {
					profile.message(ErrorMessages.PARTY_NOT_OPEN);
					return;
				}

				profile.addToParty(profilearg.getParty());
				joinedMessage = ChatMessages.JOINED_PARTY.clone().replace("%player%", profile.getName());
				PlayerManager.broadcast(party.getPartyMembers(), joinedMessage);

				return;
			case "accept":

				if (args.length < 3) {
					profile.message(UsageMessages.PARTY_ACCEPT);
					return;
				}

				if (profile.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				profilearg = PlayerManager.get(p -> p.getName().equalsIgnoreCase(args[1]));

				if (profilearg == null) {
					profile.message(ErrorMessages.REQUEST_SENDER_NOT_ONLINE);
					return;
				}

				Iterator<it.unimi.dsi.fastutil.objects.Object2LongMap.Entry<Party>> it = profile
						.getRecievedPartyRequests().entryIterator();

				while (it.hasNext()) {
					party = it.next().getKey();

					if (!party.getPartyLeader().equals(profilearg)) {
						continue;
					}

					it.remove();
					profile.addToParty(party);
					joinedMessage = ChatMessages.JOINED_PARTY.clone().replace("%player%", profile.getName());
					PlayerManager.broadcast(party.getPartyMembers(), joinedMessage);
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

				profile.bukkit().sendMessage(sb.toString());

				return;
			case "leave":
			case "disband":

				if (!profile.isInParty()) {
					profile.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				Party p = profile.getParty();

				if (p.getPartyLeader().equals(profile)) {
					p.disband();
				} else {
					p.leave(profile);
				}

				return;
		}

	}
}
