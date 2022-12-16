package gg.mineral.practice.party;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.util.collection.ProfileList;
import lombok.Getter;
import lombok.Setter;

public class Party {

	@Getter
	Profile partyLeader;
	@Setter
	@Getter
	boolean open = false;
	@Getter
	ProfileList partyMembers = new ProfileList();

	public Party(Profile partyLeader) {
		this.partyLeader = partyLeader;
		PartyManager.registerParty(this);
	}

	public boolean equals(Party p) {
		return p.getPartyLeader().equals(partyLeader);
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
