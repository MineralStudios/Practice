package gg.mineral.practice.util.messages;

import org.bukkit.entity.Player;

public class ErrorMessage extends Message {
	public String addition;

	public ErrorMessage(String s) {
		message = s;
		formatMessage();
	}

	private void formatMessage() {
		this.addition = CC.D_RED + "✖ Error ✖ " + CC.RED;
		message = addition + message;
	}

	@Override
	public void send(Player p) {
		p.sendMessage(message);
	}
}
