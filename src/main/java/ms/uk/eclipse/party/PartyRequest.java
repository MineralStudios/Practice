package ms.uk.eclipse.party;

public class PartyRequest {
	final Party sender;

	public PartyRequest(Party sender) {
		this.sender = sender;
	}

	public Party getSender() {
		return sender;
	}
}
