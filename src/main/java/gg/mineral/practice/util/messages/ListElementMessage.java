package gg.mineral.practice.util.messages;

public class ListElementMessage extends ChatMessage {

    public ListElementMessage(String s, String c) {
        super(s, c);
    }

    @Override
    protected void formatMessage(String c, boolean bold) {
        this.prefix = CC.ACCENT + "✱ " + c;
        this.messageBuilder.insert(0, prefix);
        this.messageBuilder.append(CC.SECONDARY);
    }
}
