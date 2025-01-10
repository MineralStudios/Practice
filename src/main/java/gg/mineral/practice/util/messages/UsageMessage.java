package gg.mineral.practice.util.messages;

import lombok.val;

public class UsageMessage extends Message {

    public UsageMessage(String s) {
        super(s);
        formatMessage();
    }

    private void formatMessage() {
        val prefix = CC.D_RED + "Usage: " + CC.RED;
        this.messageBuilder.insert(0, prefix);
        this.messageBuilder.append(".");
    }

    @Override
    public Message clone() {
        return new UsageMessage(this.messageBuilder.toString());
    }
}
