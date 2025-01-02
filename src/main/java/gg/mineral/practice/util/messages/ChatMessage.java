package gg.mineral.practice.util.messages;

import lombok.val;

public class ChatMessage extends Message {
    protected String prefix;

    public ChatMessage(String string) {
        this(string, "", false);
    }

    public ChatMessage(String string, String colorPrefix, boolean bold) {
        super(string);
        formatMessage(colorPrefix, bold);
    }

    public ChatMessage(String string, String colorPrefix) {
        this(string, colorPrefix, false);
    }

    protected void formatMessage(String c, boolean bold) {
        this.prefix = bold ? c + CC.B : c;
        this.messageBuilder.insert(0, prefix);
    }

    public ChatMessage highlightText(String c, String... highlighted) {
        for (val s : highlighted) {
            int index = messageBuilder.indexOf(s);
            while (index != -1) {
                messageBuilder.replace(index, index + s.length(), c + s + this.prefix);
                index = messageBuilder.indexOf(s, index + (c + s + this.prefix).length());
            }
        }
        return this;
    }

    public ChatMessage replace(String target, String replacement) {
        int index = messageBuilder.indexOf(target);
        if (index == -1) {
            throw new AssertionError("Target string not found in the message.");
        }
        while (index != -1) {
            messageBuilder.replace(index, index + target.length(), replacement);
            index = messageBuilder.indexOf(target, index + replacement.length());
        }
        return this;
    }

    @Override
    public ChatMessage clone() {
        return new ChatMessage(this.messageBuilder.toString(), this.prefix);
    }
}
