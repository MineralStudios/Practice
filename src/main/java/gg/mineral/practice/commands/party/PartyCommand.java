package gg.mineral.practice.commands.party;

import java.util.Iterator;
import java.util.Map.Entry;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.PlayerManager;
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
		Profile player = PlayerManager.getProfile(pl);
		Party party;
		Profile playerarg;
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
				if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				if (player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_ALREADY_IN_PARTY);
					return;
				}

				player.addToParty(new Party(player));
				ChatMessages.PARTY_CREATED.send(pl);
				return;
			case "invite":

				if (args.length < 3) {
					player.message(UsageMessages.PARTY_INVITE);
					return;
				}

				playerarg = PlayerManager.getProfile(args[1]);

				if (playerarg == null) {
					player.message(ErrorMessages.PLAYER_NOT_ONLINE);
					return;
				}

				if (!player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				if (playerarg.isInParty()) {
					player.message(ErrorMessages.PLAYER_IN_PARTY);
					return;
				}

				if (!player.getParty().getPartyLeader().equals(player)) {
					player.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
					return;
				}

				if (player.equals(playerarg)) {
					player.message(ErrorMessages.YOU_CAN_NOT_INVITE_YOURSELF);
					return;
				}

				if (playerarg.getRecievedPartyRequests().containsKey(player.getParty())) {
					ErrorMessages.WAIT_TO_INVITE.send(pl);
					return;
				}

				playerarg.getRecievedPartyRequests().add(player.getParty());

				ChatMessages.PARTY_REQUEST_RECIEVED.clone().replace("%player%", player.getName()).setTextEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()),
						ChatMessages.CLICK_TO_ACCEPT)
						.send(playerarg.bukkit());

				ChatMessages.PARTY_REQUEST_SENT.clone().replace("%player%", playerarg.getName()).send(pl);

				return;
			case "open":
				if (!player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				party = player.getParty();

				if (!party.getPartyLeader().equals(player)) {
					player.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
					return;
				}

				party.setOpen(!party.getPartyOpen());
				ChatMessages.PARTY_OPENED.clone().replace("%opened%", party.getPartyOpen() ? "opened" : "closed")
						.send(pl);

				if (party.getPartyOpen()) {
					if (!player.getPartyOpenCooldown()) {

						player.startPartyOpenCooldown();

						ChatMessage messageToBroadcast = ChatMessages.BROADCAST_PARTY_OPEN.clone()
								.replace("%player%", player.getName()).setTextEvent(
										new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/party join " + player.getName()),
										ChatMessages.CLICK_TO_JOIN);

						PlayerManager.broadcast(PlayerManager.getProfiles(), messageToBroadcast);

						return;
					}

					ChatMessages.CAN_NOT_BROADCAST.send(pl);
				}

				return;
			case "join":

				if (args.length < 2) {
					player.message(UsageMessages.PARTY_JOIN);
					return;
				}

				if (player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_ALREADY_IN_PARTY);
					return;
				}

				playerarg = PlayerManager.getProfile(args[1]);

				if (playerarg == null) {
					player.message(ErrorMessages.PLAYER_NOT_ONLINE);
					return;
				}

				if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				party = playerarg.getParty();

				if (party == null) {
					player.message(ErrorMessages.PARTY_DOES_NOT_EXIST);
					return;
				}

				if (!party.getPartyOpen()) {
					player.message(ErrorMessages.PARTY_NOT_OPEN);
					return;
				}

				player.addToParty(playerarg.getParty());
				joinedMessage = ChatMessages.JOINED_PARTY.clone().replace("%player%", player.getName());
				PlayerManager.broadcast(party.getPartyMembers(), joinedMessage);

				return;
			case "accept":

				if (args.length < 3) {
					player.message(UsageMessages.PARTY_ACCEPT);
					return;
				}

				if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				playerarg = PlayerManager.getProfile(args[1]);

				if (playerarg == null) {
					player.message(ErrorMessages.REQUEST_SENDER_NOT_ONLINE);
					return;
				}

				Iterator<Entry<Party, Long>> it = player.getRecievedPartyRequests().entryIterator();

				while (it.hasNext()) {
					party = it.next().getKey();

					if (!party.getPartyLeader().equals(playerarg)) {
						continue;
					}

					it.remove();
					player.addToParty(party);
					joinedMessage = ChatMessages.JOINED_PARTY.clone().replace("%player%", player.getName());
					PlayerManager.broadcast(party.getPartyMembers(), joinedMessage);
					return;
				}

				player.message(ErrorMessages.REQUEST_EXPIRED);

				return;
			case "list":
				if (!player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				sb = new StringBuilder(CC.GRAY + "[");

				Iterator<Profile> profileIter = player.getParty().getPartyMembers().iterator();

				while (profileIter.hasNext()) {
					Profile p = profileIter.next();
					sb.append(CC.GREEN + p.getName());

					if (profileIter.hasNext()) {
						sb.append(CC.GRAY + ", ");
					}
				}

				sb.append(CC.GRAY + "]");

				player.bukkit().sendMessage(sb.toString());

				return;
			case "leave":
			case "disband":

				if (!player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				Party p = player.getParty();

				ChatMessage leftMessage = ChatMessages.LEFT_PARTY.clone().replace("%player%", player.getName());

				Iterator<Profile> iter = p.getPartyMembers().iterator();

				if (p.getPartyLeader().equals(player)) {
					while (iter.hasNext()) {
						Profile plr = iter.next();
						iter.remove();
						plr.removeFromParty();
						plr.message(leftMessage);
					}

					PartyManager.remove(p);
				} else {

					player.removeFromParty();

					while (iter.hasNext()) {
						Profile plr = iter.next();
						plr.message(leftMessage);
					}
				}

				return;
		}

	}
}
