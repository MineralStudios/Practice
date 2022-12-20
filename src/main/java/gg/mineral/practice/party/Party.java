package gg.mineral.practice.party;

import java.util.Iterator;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;
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

	public void leave(Profile profile) {
		ChatMessage leftMessage = ChatMessages.LEFT_PARTY.clone().replace("%player%", profile.getName());

		Iterator<Profile> iter = getPartyMembers().iterator();

		if (getPartyLeader().equals(profile)) {
			while (iter.hasNext()) {
				Profile member = iter.next();
				iter.remove();
				member.removeFromParty();
				member.message(leftMessage);
			}

			PartyManager.remove(this);
		} else {

			profile.removeFromParty();

			while (iter.hasNext()) {
				Profile member = iter.next();
				member.message(leftMessage);
			}
		}
	}
}
