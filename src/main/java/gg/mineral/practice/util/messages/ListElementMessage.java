package gg.mineral.practice.util.messages;

public class ListElementMessage extends ChatMessage {

    public ListElementMessage(String s, String c) {
        super(s, c);
    }

    @Override
    protected void formatMessage(String c, boolean bold) {
        this.addition = CC.ACCENT + "✱ " + c;
        message = addition + message + CC.SECONDARY;
    }
}
