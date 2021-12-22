package ms.uk.eclipse.party;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.ProfileList;

public class Party {

	Profile partyLeader;
	Boolean partyOpen = false;
	ProfileList partyMembers = new ProfileList();
	final PartyManager partyManager = PracticePlugin.INSTANCE.getPartyManager();
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public Party(Profile partyLeader) {
		this.partyLeader = partyLeader;
		partyManager.registerParty(this);
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
