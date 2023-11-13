package gg.mineral.practice.util.messages;

import org.bukkit.entity.Player;

public class ChatMessage extends Message {
	String addition;

	ChatMessage(String s) {
		message = s;
	}

	public ChatMessage(String s, String c, boolean bold) {
		message = s;
		formatMessage(c, bold);
	}

	public ChatMessage(String s, String c) {
		message = s;
		formatMessage(c, false);
	}

	protected void formatMessage(String c, boolean bold) {
		this.addition = bold ? c + CC.B : c;
		message = addition + message;
	}

	public ChatMessage highlightText(String c, String... highlighted) {

		for (String s : highlighted) {
			message = message.replace(s, c + s + this.addition);
		}

		return this;
	}

	public ChatMessage replace(String message, String replacement) {
		this.message = this.message.replace(message, replacement);
		return this;
	}

	public ChatMessage clone() {
		return new ChatMessage(message);
	}

	@Override
	public void send(Player p) {
		p.sendMessage(message);
		return;
	}
}
