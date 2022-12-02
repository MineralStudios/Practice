package gg.mineral.practice.party;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.util.collection.ProfileList;

public class Party {

	Profile partyLeader;
	Boolean partyOpen = false;
	ProfileList partyMembers = new ProfileList();

	public Party(Profile partyLeader) {
		this.partyLeader = partyLeader;
		PartyManager.registerParty(this);
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
}
