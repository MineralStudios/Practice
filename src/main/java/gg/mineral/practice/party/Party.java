package gg.mineral.practice.party;

import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class Party {

	Profile partyLeader;
	Boolean partyOpen = false;
	ProfileList partyMembers = new ProfileList();

	public Party(Profile partyLeader) {
		this.partyLeader = partyLeader;
		PartyManager.register(this);
	}

	public ProfileList getPartyMembers() {
		return partyMembers;
	}

	public Profile getPartyLeader() {
		return partyLeader;
	}

	public void setOpen(boolean open) {
		this.partyOpen = open;
	}

	public boolean equals(Party p) {
		return p.getPartyLeader().equals(partyLeader);
	}

	public boolean getPartyOpen() {
		return partyOpen;
	}

	public void add(Profile p) {
		partyMembers.add(p);
	}

	public void remove(Profile p) {
		partyMembers.remove(p);
	}

	public boolean contains(Profile p) {
		return partyMembers.contains(p);
	}

	public void leave(Profile profile) {
		ChatMessage leftMessage = ChatMessages.LEFT_PARTY.clone().replace("%player%", getPartyLeader().getName());
		profile.removeFromParty();
		PlayerManager.broadcast(partyMembers, leftMessage);
	}

	public void disband() {
		ChatMessage leftMessage = ChatMessages.LEFT_PARTY.clone().replace("%player%", getPartyLeader().getName());

		while (!getPartyMembers().isEmpty()) {
			Profile plr = getPartyMembers().removeFirst();
			plr.removeFromParty();
			plr.message(leftMessage);
		}

		PartyManager.remove(this);
	}

}
