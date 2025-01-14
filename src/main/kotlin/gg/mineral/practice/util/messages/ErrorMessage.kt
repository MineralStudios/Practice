package gg.mineral.practice.util.messages;

import lombok.val;

public class ErrorMessage extends Message {

    public ErrorMessage(String s) {
        super(s);
        formatMessage();
    }

    @Override
    public Message clone() {
        return new ErrorMessage(this.messageBuilder.toString());
    }

    private void formatMessage() {
        val prefix = CC.D_RED + "✖ Error ✖ " + CC.RED;
        this.messageBuilder.insert(0, prefix);
    }
}
