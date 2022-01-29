package ms.uk.eclipse.commands.party;

import java.util.Iterator;

import org.bukkit.Bukkit;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.CreatedMessage;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.JoinMessage;
import ms.uk.eclipse.core.utils.message.JoinedMessage;
import ms.uk.eclipse.core.utils.message.LeftMessage;
import ms.uk.eclipse.core.utils.message.RequestMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.party.Party;
import ms.uk.eclipse.party.PartyRequest;
import ms.uk.eclipse.util.messages.ErrorMessages;
import net.md_5.bungee.api.chat.ClickEvent;

public class PartyCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final PartyManager partyManager = PracticePlugin.INSTANCE.getPartyManager();

	public PartyCommand() {
		super("party");
		setAliases("p");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = "";
		if (args.length > 0) {
			arg = args[0];
		}

		Profile player = playerManager.getProfile(pl);

		PartyRequest pr;
		switch (arg.toLowerCase()) {
			default:
				player.message(new StrikingMessage("Party Help", CC.PRIMARY, true));
				player.message(new ChatMessage("/p create", CC.SECONDARY, false));
				player.message(new ChatMessage("/p invite <Player>", CC.SECONDARY, false));
				player.message(new ChatMessage("/p open <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/p list", CC.SECONDARY, false));
				player.message(new ChatMessage("/p join <PartyLeader>", CC.SECONDARY, false));
				player.message(new ChatMessage("/duel <PartyLeader>", CC.SECONDARY, false));
				player.message(new ChatMessage("/p accept <PartyLeader>", CC.SECONDARY, false));
				player.message(new ChatMessage("/p leave", CC.SECONDARY, false));
				player.message(new ChatMessage("/p disband", CC.SECONDARY, false));

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
				player.message(new CreatedMessage("Your party"));

				return;
			case "invite":

				if (args.length < 2) {
					player.message(new UsageMessage("/party join <Name>"));
					return;
				}

				Profile playerarg = playerManager.getProfile(args[1]);

				if (playerarg == null) {
					player.message(ErrorMessages.PLAYER_NOT_ONLINE);
					return;
				}

				if (!player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				if (playerarg.isInParty()) {
					player.message(ErrorMessages.PLAYER_IN_PARTY);
					return;
				}

				if (!(player.getParty().getPartyLeader() == player)) {
					player.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
					return;
				}

				if (player == playerarg) {
					player.message(ErrorMessages.YOU_CAN_NOT_INVITE_YOURSELF);
					return;
				}

				PartyRequest pR = new PartyRequest(player.getParty());
				playerarg.getRecievedPartyRequests().add(pR);

				RequestMessage message = new RequestMessage(player.getName(), "party invite", "",
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()));
				player.message(new ChatMessage("You have sent a party request", CC.PRIMARY, false));

				playerarg.message(message);

				Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PracticePlugin.INSTANCE,
						new Runnable() {
							public void run() {
								playerarg.getRecievedPartyRequests().remove(pR);
							}
						}, 1200);

				return;
			case "open":
				if (!player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				Party party = player.getParty();

				if (!party.getPartyLeader().equals(player)) {
					player.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
					return;
				}

				party.setOpen(!party.getPartyOpen());
				String status = party.getPartyOpen() ? "open" : "closed";
				String chatColor = party.getPartyOpen() ? CC.GREEN : CC.RED;

				player.message(new ChatMessage("Your party is now " + status, chatColor, false));

				if (party.getPartyOpen() && !player.getPartyOpenCooldown()) {

					player.setPartyOpenCooldown(true);

					JoinMessage message2 = new JoinMessage(player.getName(), "opened", "their party",
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p join " + player.getName()));

					playerManager.broadcast(playerManager.getProfiles(), message2);

					Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PracticePlugin.INSTANCE,
							new Runnable() {
								public void run() {
									player.setPartyOpenCooldown(false);
								}
							}, 400);

					return;
				}

				player.message(new ChatMessage("The message to join can only get broadcasted once every 20 seconds",
						CC.PRIMARY, false));

				return;
			case "join":

				if (args.length < 2) {
					player.message(new UsageMessage("/party join <Name>"));
					return;
				}

				if (player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_ALREADY_IN_PARTY);
					return;
				}

				playerarg = playerManager.getProfile(args[1]);

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
				playerManager.broadcast(party.getPartyMembers(), new JoinedMessage(player.getName(), "party"));

				return;
			case "accept":

				if (args.length < 2) {
					player.message(new UsageMessage("/party join <Name>"));
					return;
				}

				if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
					return;
				}

				Profile player1 = playerManager.getProfile(args[1]);

				if (player1 == null) {
					player.message(ErrorMessages.REQUEST_SENDER_NOT_ONLINE);
					return;
				}

				Iterator<PartyRequest> it = player.getRecievedPartyRequests().iterator();

				while (it.hasNext()) {
					pr = it.next();

					if (!pr.getSender().getPartyLeader().equals(player1)) {
						continue;
					}

					player.getRecievedPartyRequests().remove(pr);
					party = player1.getParty();
					player.addToParty(party);
					playerManager.broadcast(party.getPartyMembers(), new JoinedMessage(player.getName(), "party"));
					return;
				}

				player.message(ErrorMessages.REQUEST_EXPIRED);

				return;
			case "list":
				if (!player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				player.message(new StrikingMessage("Party List", CC.PRIMARY, true));

				for (Profile p : player.getParty().getPartyMembers()) {
					player.message(new ChatMessage(p.getName(), CC.SECONDARY, false));
				}

				return;
			case "leave":
			case "disband":

				if (!player.isInParty()) {
					player.message(ErrorMessages.YOU_ARE_NOT_IN_PARTY);
					return;
				}

				Party p = player.getParty();

				if (p == null) {
					return;
				}

				LeftMessage m = new LeftMessage(player.getName(), "party");

				Iterator<Profile> iter = p.getPartyMembers().iterator();

				if (p.getPartyLeader().equals(player)) {
					while (iter.hasNext()) {
						Profile plr = iter.next();
						iter.remove();
						plr.removeFromParty();
						plr.message(m);
					}

					partyManager.remove(p);
				} else {

					player.removeFromParty();

					while (iter.hasNext()) {
						Profile plr = iter.next();
						plr.message(m);
					}
				}

				return;
		}

	}
}
