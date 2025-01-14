package gg.mineral.practice.util.messages

class UsageMessage(s: String) : Message(s) {
    init {
        messageBuilder.insert(0, CC.D_RED + "Usage: " + CC.RED)
        messageBuilder.append(".")
    }

    override fun clone() = UsageMessage(messageBuilder.toString())
}
