package gg.mineral.practice.util.messages

class ListElementMessage(s: String, c: String) : ChatMessage(s, c) {
    override val prefix: String
        get() {
            val string = CC.ACCENT + "✱ " + colorPrefix
            messageBuilder.insert(0, string)
            messageBuilder.append(CC.SECONDARY)
            return string
        }
}
