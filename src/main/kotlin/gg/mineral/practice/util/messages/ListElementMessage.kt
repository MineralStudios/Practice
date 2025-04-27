package gg.mineral.practice.util.messages

class ListElementMessage(s: String, c: String) : ChatMessage(s, c) {
    override val prefix by lazy {
        CC.ACCENT + "✱ " + colorPrefix
    }

    override fun prepend(builder: StringBuilder): StringBuilder = super.prepend(builder).append(CC.SECONDARY)
}
