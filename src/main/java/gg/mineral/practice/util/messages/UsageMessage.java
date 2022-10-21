package gg.mineral.practice.util.messages;

import org.bukkit.entity.Player;

public class UsageMessage extends Message {

	public UsageMessage(String s) {
		message = s;
		formatMessage();
	}

	private void formatMessage() {
		String addition = CC.D_RED + "Usage: " + CC.RED;
		message = addition + message + ".";
	}

	@Override
	public void send(Player p) {
		p.sendMessage(message);
	}
}
