package gg.mineral.practice.util.messages;

import org.bukkit.entity.Player;

import lombok.val;

public class UsageMessage extends Message {

	public UsageMessage(String s) {
		message = s;
		formatMessage();
	}

	private void formatMessage() {
		val addition = CC.D_RED + "Usage: " + CC.RED;
		message = addition + message + ".";
	}

	@Override
	public void send(Player p) {
		p.sendMessage(message);
	}
}
