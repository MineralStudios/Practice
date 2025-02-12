package gg.mineral.practice.util.messages

open class ChatMessage(
    string: String,
    val colorPrefix: String = "",
    val bold: Boolean = false
) :
    Message(string) {
    open val prefix by lazy { if (bold) colorPrefix + CC.B else colorPrefix }

    open fun highlightText(c: String, vararg highlighted: String): ChatMessage {
        for (s in highlighted) {
            var index = messageBuilder.indexOf(s)
            while (index != -1) {
                messageBuilder.replace(index, index + s.length, c + s + this.prefix)
                index = messageBuilder.indexOf(s, index + (c + s + this.prefix).length)
            }
        }
        return this
    }

    open fun replace(target: String, replacement: String): ChatMessage {
        var index = messageBuilder.indexOf(target)
        if (index == -1) throw AssertionError("Target string not found in the message.")
        while (index != -1) {
            messageBuilder.replace(index, index + target.length, replacement)
            index = messageBuilder.indexOf(target, index + replacement.length)
        }
        return this
    }

    override fun prepend(builder: StringBuilder): StringBuilder = builder.insert(0, this.prefix)

    override fun clone() = ChatMessage(messageBuilder.toString(), this.colorPrefix, bold)
}
